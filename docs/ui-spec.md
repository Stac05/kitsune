# Kitsune - UI Specification (v0.2)

## Purpose

Dokumen ini mendefinisikan seluruh UI resmi Kitsune menggunakan Jetpack Compose.

---

# Design Principles

- **Dark Theme First:** Dominan hitam/gelap.
- **Primary Accent:** Orange (Branding Kitsune).
- **Minimal Layout:** Menghilangkan elemen yang tidak perlu.
- **Fast Navigation:** Akses cepat ke konten.

---

# Global UI Rules

- **Theme:** Dark Mode (default), OLED Black (optional).
- **Grid System:** Configurable (2, 3, 4 columns). Default: 3.
- **Cover Ratio:** 2:3 untuk komik dan poster video.
- **Touch Target:** Minimum 48dp untuk elemen interaktif.

---

# Splash & Setup Flow

## Splash Screen
- Menampilkan logo Kitsune.
- Mengecek keberadaan `rootFolderUri` di database.
- Jika tidak ada, menampilkan tombol **"Select Library Folder"**.

## SAF Picker Integration
- Menggunakan System Folder Picker (SAF).
- Setelah folder dipilih, aplikasi menampilkan ringkasan struktur yang akan dibuat.

---

# Local Screen (Home)

Menampilkan dua bagian utama:
1. **Last Read:** Card besar berisi komik terakhir, progress (halaman/chapter), dan tombol lanjut.
2. **Library Shortcuts:** Tombol cepat menuju "Comics" dan "Videos".

---

# Comic Library Screen

- **Grid View:** Menampilkan cover komik.
- **Natural Sorting:** Daftar komik diurutkan secara alfabetis menggunakan logika Natural Sort.
- **Search:** Filter instan berdasarkan judul folder.

---

# Comic Detail Screen

- **Header:** Cover (blur background), Judul, dan Metadata (jika ada).
- **Action Buttons:** Read (Resume/Start), Favorite, Bookmark, Playlist.
- **Chapter List:** Menampilkan daftar file `.cbz`.
    - **Natural Sorting:** Chapter 2 tampil sebelum Chapter 10.
    - **Display Name:** Tanpa ekstensi `.cbz`.

---

# Reader Screen

- **Reading Area:** Menampilkan gambar manga sesuai mode (Vertical, LTR, RTL).
- **Top Bar (Auto-hide):** Judul dan nomor chapter.
- **Bottom Bar (Auto-hide):**
    - **Page Slider:** Untuk navigasi cepat.
    - **Page Counter:** `Current / Total`.
    - **Chapter Nav:** Tombol Next/Prev chapter.

---

# Settings (Other) Screen

- **Library Section:**
    - `rootFolderUri` display (Shortened path).
    - Tombol "Change Root Folder" (Akan memicu SAF Picker ulang).
    - Tombol "Rescan Library".
- **Reading Section:** Reading Mode, Auto Next Chapter.
- **Appearance:** Dark Mode, OLED Black, Grid Size.

---

# UI Decision Summary

1. Dark Theme + Orange Accent.
2. Splash menangani pemilihan Root Folder via SAF.
3. Natural Sorting diterapkan pada semua list (Library & Chapters).
4. Cover ratio 2:3.
5. Grid size dapat diatur (2, 3, 4).
