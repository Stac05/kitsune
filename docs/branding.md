# Kitsune - Branding Specification (v0.1)

## Purpose

Dokumen ini mendefinisikan branding resmi aplikasi Kitsune.

AI Agent wajib menggunakan asset yang didefinisikan di dokumen ini dan tidak boleh membuat logo, icon, atau branding pengganti tanpa instruksi eksplisit.

---

# Brand Name

Official Name:

```text
Kitsune
```

---

# Brand Identity

Kitsune adalah:

```text
Offline First
Comic Reader
Media Library
Dark Theme
Performance Focused
```

---

# Official Assets

Lokasi asset branding:

```text
assets/branding/
```

---

# Logo Assets

## Main Logo

File:

```text
assets/branding/kitsune-logo-app.png
```

Purpose:

```text
Primary Brand Logo
```

Digunakan untuk:

- About Page
- Branding Documentation
- Marketing Material
- Repository Branding
- Future Website

---

## Splash Logo

File:

```text
assets/branding/kitsune-logo-splash.png
```

Purpose:

```text
Splash Screen Logo
```

Digunakan untuk:

- Splash Screen

---

# Splash Screen Rules

Splash Screen wajib menggunakan:

```text
kitsune-logo-splash.png
```

Flow:

```text
Logo
↓
Loading Library
↓
Open Local Screen
```

AI Agent tidak boleh mengganti logo splash dengan logo lain.

---

# Application Icon

Saat ini menggunakan identitas visual Kitsune.

Jika icon launcher dibuat:

Gunakan asset resmi dari:

```text
assets/branding/
```

Jangan menggunakan Android default icon.

---

# Theme Colors

Primary Accent:

```text
Orange
```

Mengikuti warna logo Kitsune.

---

Default Theme:

```text
Dark Theme
```

---

Optional Theme:

```text
OLED Black
```

---

# Branding Restrictions

AI Agent tidak boleh:

- Membuat logo baru.
- Mengganti warna identitas utama.
- Menggunakan placeholder logo.
- Menggunakan Android launcher icon default.
- Menggunakan icon pihak ketiga.

---

# Asset Source Of Truth

Source of truth branding:

```text
assets/branding/
```

Jika asset di Android resources berbeda dengan asset branding, maka asset branding dianggap benar.

---

# Android Resource Mapping

Contoh implementasi:

```text
assets/branding/kitsune-logo-splash.png
↓
app/src/main/res/drawable/kitsune_logo_splash.png
```

```text
assets/branding/kitsune-logo-app.png
↓
app/src/main/res/drawable/kitsune_logo_app.png
```

---

# AI Agent Instructions

Saat membangun UI:

1. Cari asset di assets/branding.
2. Gunakan logo resmi.
3. Jangan membuat placeholder.
4. Jangan membuat branding alternatif.
5. Ikuti warna identitas Kitsune.

---

# Branding Summary

1. Nama aplikasi: Kitsune.
2. Main logo: kitsune-logo-app.png.
3. Splash logo: kitsune-logo-splash.png.
4. Accent color: Orange.
5. Theme default: Dark.
6. Branding asset berada di assets/branding.
7. Jangan gunakan placeholder branding.
8. Jangan mengganti logo tanpa instruksi eksplisit.
