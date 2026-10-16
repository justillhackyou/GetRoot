import java.util.Properties

plugins {
    id("com.android.application") version "9.1.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.20" apply false
    id("org.jetbrains.kotlin.jvm") version "2.4.20" apply false
}

private fun localProperties(): Properties = Properties().also { properties ->
    val propertiesFile = rootProject.file("local.properties")
    if (propertiesFile.isFile) {
        propertiesFile.inputStream().use(properties::load)
    }
}

private fun ondkHome(): String? =
    System.getenv("ONDK_HOME")?.takeIf(String::isNotBlank)
        ?: localProperties().getProperty("ondk.dir")?.takeIf(String::isNotBlank)

private fun useOndk(): Boolean = !ondkHome().isNullOrBlank()

private fun resolveNdkDir(): String? {
    val ondk = ondkHome()
    if (ondk != null) return ondk

    val properties = localProperties()
    val ndkEnvironment = System.getenv("ANDROID_NDK_HOME")
        ?: System.getenv("ANDROID_NDK_ROOT")
    if (!ndkEnvironment.isNullOrBlank()) return ndkEnvironment

    properties.getProperty("ndk.dir")?.takeIf(String::isNotBlank)?.let { return it }

    val sdkDir = properties.getProperty("sdk.dir") ?: System.getenv("ANDROID_HOME")
    if (!sdkDir.isNullOrBlank()) {
        val ndkRoot = File(sdkDir, "ndk")
        val versions = ndkRoot.listFiles()
            ?.filter(File::isDirectory)
            ?.map(File::getName)
            ?.sorted()
            .orEmpty()
        if (versions.isNotEmpty()) return File(ndkRoot, versions.last()).absolutePath
    }

    return null
}

private data class NdkTools(val clang: String, val ar: String)

private fun resolveCargoExecutable(): String {
    val cargoOnPath = System.getenv("PATH")
        .orEmpty()
        .split(File.pathSeparator)
        .asSequence()
        .map { File(it, "cargo") }
        .firstOrNull { it.isFile && it.canExecute() }
    if (cargoOnPath != null) return cargoOnPath.absolutePath

    val cargoInRustupHome = File(System.getProperty("user.home"), ".cargo/bin/cargo")
    return if (cargoInRustupHome.isFile && cargoInRustupHome.canExecute()) {
        cargoInRustupHome.absolutePath
    } else {
        "cargo"
    }
}

private fun extractNdkTools(): NdkTools? {
    val ndk = resolveNdkDir() ?: return null
    val osName = System.getProperty("os.name").lowercase()
    val isWindows = osName.contains("windows")
    val prebuilt = when {
        isWindows -> "windows-x86_64"
        osName.contains("mac") -> "darwin-x86_64"
        else -> "linux-x86_64"
    }
    val binDir = File(ndk, "toolchains/llvm/prebuilt/$prebuilt/bin")
    return NdkTools(
        clang = File(
            binDir,
            if (isWindows) "aarch64-linux-android34-clang.cmd" else "aarch64-linux-android34-clang",
        ).absolutePath,
        ar = File(binDir, if (isWindows) "llvm-ar.exe" else "llvm-ar").absolutePath,
    )
}

// Every module's output lives under the root build/ directory (native,
// host-test, extract, kernel-profiles, app). Delete the whole tree here so a
// single root `clean` resets all of them.
tasks.register<Delete>("clean") {
    description = "Delete the root build/ directory (all module outputs)."
    delete(layout.buildDirectory)
}

tasks.register<Exec>("buildGhostlockNative") {
    description = "buildGhostlockNative"
    val prebuiltSo = file("app/src/main/jniLibs/arm64-v8a/libghostlock.so")
    val nativeOut = file("build/native/ghostlock")
    outputs.file(nativeOut)
    onlyIf { !prebuiltSo.exists() }
    workingDir(file("src"))
    commandLine("make", "ghostlock")
    doFirst {
        val ndk = resolveNdkDir() ?: throw GradleException("NDK not found; set ANDROID_NDK_HOME or ndk.dir in local.properties")
        environment("ANDROID_NDK_HOME", ndk)
        environment("NDK_ROOT", ndk)
    }
}

tasks.register<Copy>("prepareGhostlockJniLibs") {
    description = "prepareGhostlockJniLibs"
    val prebuiltSo = file("app/src/main/jniLibs/arm64-v8a/libghostlock.so")
    val nativeOut = file("build/native/ghostlock")
    onlyIf { !prebuiltSo.exists() && nativeOut.exists() }
    dependsOn("buildGhostlockNative")
    from("build/native/ghostlock")
    into("app/src/main/jniLibs/arm64-v8a")
    rename { "libghostlock.so" }
    doLast {
        val tools = extractNdkTools()
        if (tools != null) {
            val stripPath = File(tools.clang).resolveSibling("llvm-strip").absolutePath
            val packagedPath = File(rootDir, "app/src/main/jniLibs/arm64-v8a/libghostlock.so").absolutePath
            ProcessBuilder(stripPath, "--strip-all", packagedPath)
                .inheritIO()
                .start()
                .waitFor()
        }
    }
}

tasks.register<Exec>("buildGhostlockExtract") {
    description = "buildGhostlockExtract"
    val prebuiltSo = file("app/src/main/jniLibs/arm64-v8a/libextract.so")
    val extractOut = file("build/extract/aarch64-linux-android/release/ghostlock-extract")
    outputs.file(extractOut)
    onlyIf { !prebuiltSo.exists() }
    workingDir(rootProject.file("tools/extract_rs"))
    commandLine("cargo", "version")
    doFirst {
        val tools = extractNdkTools() ?: throw GradleException("NDK not found")
        val isOndk = useOndk()
        val cmd = mutableListOf(resolveCargoExecutable())
        if (isOndk) cmd += "+ondk"
        cmd += listOf("build", "--release", "--target", "aarch64-linux-android")
        if (isOndk) {
            cmd += listOf("-Z", "build-std=std,panic_abort")
            cmd += listOf("-Z", "build-std-features=optimize_for_size")
        }
        commandLine(cmd)
        environment("CC_aarch64_linux_android", tools.clang)
        environment("AR_aarch64_linux_android", tools.ar)
        environment("CARGO_TARGET_AARCH64_LINUX_ANDROID_LINKER", tools.clang)
        environment("RUSTFLAGS", "-C force-unwind-tables=no -C link-arg=-Wl,--icf=all")
        if (isOndk) environment("RUSTC_BOOTSTRAP", "1")
    }
}

tasks.register<Copy>("prepareGhostlockExtractJniLibs") {
    description = "prepareGhostlockExtractJniLibs"
    val prebuiltSo = file("app/src/main/jniLibs/arm64-v8a/libextract.so")
    val extractOut = file("build/extract/aarch64-linux-android/release/ghostlock-extract")
    onlyIf { !prebuiltSo.exists() && extractOut.exists() }
    dependsOn("buildGhostlockExtract")
    from("build/extract/aarch64-linux-android/release/ghostlock-extract")
    into("app/src/main/jniLibs/arm64-v8a")
    rename { "libextract.so" }
}
