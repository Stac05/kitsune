# Kitsune - UI Specification (v1.0)

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
- **Icons:**
    - Comics: `Icons.Default.Book`
    - Videos: `Icons.Default.PlayArrow`
    - Selection: `Icons.Default.CheckCircle`

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
2. **Library Shortcuts:** Tombol cepat menuju "Comics" (Icon: Book) dan "Videos" (Icon: PlayArrow).

---

# Comic Library Screen

- **Grid View:** Menampilkan cover komik.
- **Natural Sorting:** Daftar komik diurutkan secara alfabetis menggunakan logika Natural Sort.
- **Search:** Filter instan berdasarkan judul folder.
- **Empty State:** Jika hasil pencarian tidak ditemukan, tampilkan icon `SearchOff` dengan pesan "No results for...".

---

# Comic Detail Screen

- **Header:** Cover, Judul, dan Metadata library.
- **Action Buttons:** Continue Reading (jika ada progres), Bookmark, Playlist.
- **Chapter List:** Menampilkan daftar file `.cbz`.
    - **Natural Sorting:** Urutan numerik yang tepat.
    - **Display Name:** Tanpa ekstensi `.cbz`.

---

# Reader Screen

- **Reading Area:** Menampilkan gambar manga sesuai mode (Vertical, LTR, RTL).
- **Overlay Controls (Auto-hide):** 
    - **Top Bar:** Judul chapter dan navigasi balik.
    - **Bottom Bar:** Page Slider (Current/Total) dan tombol navigasi antar chapter.

---

# Collections (Bookmark & Playlist)

- **Grid View:** Menampilkan kategori koleksi.
- **Selection Mode:** Diaktifkan via Long-press.
    - Mendukung seleksi massal kategori untuk penghapusan.
    - Mendukung seleksi massal item di dalam kategori untuk "Bulk Remove".
    - Indikator seleksi menggunakan `CheckCircle` dan border aksen oranye.

---

# Settings Screen

- **Library Section:** Management Root Folder dan Rescan Library.
- **Reading Section:** Reading Mode (Vertical, LTR, RTL).
- **Appearance:** Grid Size (2, 3, 4), Dark Mode, OLED Black.
- **About:** Informasi versi dan branding.

---

# UI Decision Summary

1. Dark Theme + Orange Accent.
2. Icon spesifik untuk Comics (Book) dan Videos (PlayArrow).
3. Selection Mode untuk manajemen koleksi massal.
4. Natural Sorting pada semua list.
5. Search dengan visual Empty State (SearchOff icon).
