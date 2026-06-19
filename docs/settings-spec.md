# Kitsune - Settings Specification (v0.2)

## Purpose

Dokumen ini mendefinisikan seluruh pengaturan resmi Kitsune menggunakan Hybrid SAF dan Relative Path logic.

---

# Library Settings

## Root Folder URI
- **Purpose:** Menyimpan URI folder utama yang dipilih user via SAF.
- **Value:** Full Persisted URI String.
- **Action:** Mengganti folder ini akan memicu SAF Picker baru dan mereset cache scanner (namun tetap mempertahankan progress/bookmark jika relative path cocok).

## Rescan Library
- **Purpose:** Memaksa Scanner untuk melakukan penelusuran ulang folder `Comics` dan `Videos`.

---

# Reading Settings

## Reading Mode
- **Options:** `Vertical` (Default), `Left To Right`, `Right To Left`.

## Auto Next Chapter
- **Options:** `Enabled` (Default), `Disabled`.
- **Behavior:** Jika aktif, reader otomatis memuat chapter berikutnya berdasarkan **Natural Sorting** setelah halaman terakhir.

---

# Reader Settings

## Show Page Number
- **Options:** `Enabled` (Default), `Disabled`.

## Keep Screen On
- **Options:** `Enabled` (Default), `Disabled`.

## Preload Pages
- **Options:** `Enabled` (Default), `Disabled`.

---

# Appearance Settings

## Grid Size
- **Options:** `2`, `3` (Default), `4` columns.

## Dark Mode
- **Options:** `Enabled` (Default), `Disabled`.

## OLED Black
- **Options:** `Enabled`, `Disabled` (Default).

---

# About Section

- **App Name:** Kitsune
- **Version:** v0.x.x
- **Min SDK:** 26 (Android 8.0)

---

# Settings Persistence

Semua settings disimpan di tabel `Settings` pada Room Database sebagai single record.

---

# Settings Decision Summary

1. Root Folder menggunakan SAF URI.
2. Identitas komik/video tetap menggunakan Relative Path.
3. Natural Sorting adalah perilaku default untuk urutan chapter/halaman.
4. Min SDK 26 tercermin dalam spesifikasi.
