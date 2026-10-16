# GetRoot 🚀

<div align="center">

![Android](https://img.shields.io/badge/Android-14%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Devices](https://img.shields.io/badge/Devices-Xiaomi%20%7C%20Redmi%20%7C%20POCO-FF6900?style=for-the-badge)
![Kernels](https://img.shields.io/badge/Kernel-5.15%20%7C%206.1%20%7C%206.6-blue?style=for-the-badge&logo=linux&logoColor=white)
![License](https://img.shields.io/badge/License-GPL--3.0-green?style=for-the-badge)

**Android 14+ (HyperOS / MIUI) qurilmalari (Redmi, Xiaomi, POCO) uchun kompyutersiz va xavfsiz vaqtinchalik ROOT vositasi.**

[🇺🇿 O‘zbekcha](#-ozbekcha-tavsif) • [🇷🇺 Русский](#-описание-на-русском) • [🇬🇧 English](#-english-description)

---

### 📦 Yuklab olish / Download:
👉 **[GetRoot.apk yuklab olish (v1.2)](GetRoot.apk)**

</div>

---

## 🇺🇿 O‘zbekcha Tavsif

**GetRoot** — bu Android 14 va undan yuqori versiyadagi Xiaomi, Redmi va POCO smartfonlarida kompyuter, TWRP yoki Bootloaderni ochish talab qilinmasdan turib to‘liq Root (KernelSU) huquqini olish imkonini beruvchi kuchli vosita.

Ilova Linux yadrosi (Kernel 5.15 va 6.x) ning PI-futex zaifligidan foydalangan holda xavfsiz tarzda Root huquqini faollashtiradi.

### ✨ Asosiy Imkoniyatlari:
* ⚡ **Kompyutersiz va Bootloader ochmasdan:** Barcha jarayon to‘g‘ridan-to‘g‘ri telefonning o‘zida, bir necha soniya ichida amalga oshiriladi.
* 🛡️ **100% Xavfsiz (Vaqtinchalik Root):** Root huquqlari smartfon qayta ishga tushirilguncha (rebootgacha) amal qiladi. Telefon o‘chib yonganda tizim to‘liq asl, toza holatiga qaytadi. Tizim fayllari shikastlanmaydi.
* 📦 **1-klikda KernelSU Menejeri:** KernelSU ilovasi paket ichiga joylashtirilgan. Alohida yuklab olish shart emas — bir klik bilan o‘rnatiladi va ochiladi.
* ⚡ **1-klikda Shizuku Integratsiyasi:** Shizuku ilovasi ham to‘g‘ridan-to‘g‘ri kiritilgan bo‘lib, kerakli 5.15 yadroli qurilmalarda bir zumda o‘rnatiladi.
* 🎨 **Zamonaviy va Estetik Interfeys:** Miuix uslubidagi go‘zal dizayn, gradientli animatsiyali progress-bar va bosqichlar (**Muhit ➔ Poyga ➔ W1 ➔ W2 ➔ W3 ➔ Root**).
* 🇺🇿 **To‘liq O‘zbek tilida:** Qurilma tilidan qat’i nazar barcha menyu va sozlamalar tushunarli o‘zbek tilida.

---

### 📱 Mos Keluvchi Qurilmalar:
| Parametr | Talab |
| :--- | :--- |
| **Brendlar** | Redmi, Xiaomi, POCO |
| **Android versiyasi** | Android 14, Android 15 (HyperOS 1.0, 2.0 / MIUI 14) |
| **Linux Yadrosi (Kernel)** | `5.15.x`, `6.1.x`, `6.6.x` |

---

### 📝 Qadam-baqadam Ishlatish:
1. **[GetRoot.apk](GetRoot.apk)** faylini yuklab oling va smartfoningizga o‘rnating.
2. Ilovani oching va ekrandagi **"KernelSU o‘rnatish"** tugmasini bosib menejerni o‘rnating.
3. Katta **"Ishga tushirish"** tugmasini bosing va jarayon 100% ga yetishini kuting.
4. Muvaffaqiyatli yakunlangach, **"KernelSU ni ochish"** tugmasi orqali modullarni boshqaring va Rootdan rohatlaning!
5. *Eslatma:* Telefon qayta o‘chib yonsa, GetRoot ilovasiga kirib yana bir bor "Ishga tushirish" tugmasini bosish kifoya.

---

## 🇷🇺 Описание на русском

**GetRoot** — утилита для безопасного получения временного Root (KernelSU) в один клик на смартфонах **Xiaomi, Redmi, POCO** под управлением **Android 14+** без компьютера и без разблокировки загрузчика.

### 🌟 Главные фичи:
- **Root в один клик:** Не требует ПК, TWRP или патчинга boot.img.
- **Безопасность (Root до перезагрузки):** Работает до первой перезагрузки устройства. После ребута телефон возвращается в абсолютно стоковое состояние без модификаций системы.
- **Вшитые KernelSU и Shizuku:** Установка менеджеров прямо из приложения в один клик.
- **Полная кастомизация и новый UI:** Эстетичный градиентный прогресс-бар, отслеживание стадий эксплойта и интерфейс на узбекском языке.

---

## 🇬🇧 English Description

**GetRoot** is an on-device temporary root privilege escalation utility tailored for **Xiaomi, Redmi, and POCO** devices running **Android 14+** (HyperOS / MIUI).

Powered by the Linux PI-futex kernel vulnerability, it enables seamless root execution and KernelSU module loading without requiring unlocked bootloaders or a PC.

---

## 🛠 Arxitektura va Qanday Ishlaydi?

Ilova yadro xotirasidagi waiter obyektlarini PI-futex poygasi orqali qayta yozadi va bosqichma-bosqich huquqlarni kengaytiradi:
1. **W1 (SELinux Bypass):** SELinux enforcing rejimini chetlab o‘tish.
2. **W2 (UID 0 Credentials):** Jarayonga to‘liq `root` (uid 0) huquqlarini berish.
3. **W3 (Seccomp Bypass):** Seccomp cheklovlarini olib tashlash.
4. **Handoff:** Boshqaruvni `root_child` orqali KernelSU / ksud modul tizimiga uzatish.

---

## 💻 Loyihani Yig‘ish (Building from Source)

Loyiha Android Studio va Gradle orqali osongina yig‘iladi:

```bash
# Repozitoriyni klonlash
git clone https://github.com/justillhackyou/GetRoot.git
cd GetRoot

# Release APK yig'ish (JDK 17+)
./gradlew assembleRelease
```
Tayyor APK fayli `build/app/outputs/apk/release/` katalogida hosil bo‘ladi.

---

## 👨‍💻 Dasturchi va Loyiha Muallifi

- **Forkmaster:** **Hikmatullo** ([@ill_hack_you](https://t.me/ill_hack_you))
- **Ta’lim:** **ITPU (IT Park University)** — *Software Engineering yo‘nalishi 1-kurs talabasi*
- ✈️ **Telegram:** [@ill_hack_you](https://t.me/ill_hack_you)
- 📷 **Instagram:** [@mr_hikmatullo_](https://instagram.com/mr_hikmatullo_)
- 📞 **Telefon:** `+998 95 141 98 36`
- 🌐 **Veb-sayt:** [hikmatullo.dev](https://hikmatullo.dev)
- 🐙 **GitHub:** [justillhackyou](https://github.com/justillhackyou)

---

## 🙏 Minnatdorchilik va Asl Manba (Credits)

Ushbu loyiha asosidagi dastlabki eksploit mexanizmi va yadro arxitekturasi uchun minnatdorchilik bildiramiz:
- **GhostLock Muallifi:** [YuKongA](https://github.com/YuKongA)
- **Asl loyiha repozitoriysi:** [YuKongA/ghostlock-app](https://github.com/YuKongA/ghostlock-app)

---

## 📜 Litsenziya

Loyiha [GNU General Public License v3.0](LICENSE) litsenziyasi ostida taqdim etiladi.
