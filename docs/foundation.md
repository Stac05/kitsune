# Kitsune - Foundation Specification (v0.2)

## Overview

Kitsune adalah aplikasi Android offline untuk membaca manga/comic dan mengelola library video lokal.

Dokumen ini merupakan pondasi utama proyek dan menjadi sumber referensi untuk pembuatan dokumen AI Agent lainnya.

---

# Core Principles

1. Offline-first.
2. Menggunakan Hybrid Storage Access Framework (SAF) untuk akses file.
3. Menggunakan filesystem lokal sebagai source of truth.
4. Database hanya menyimpan metadata aplikasi menggunakan identifier jalur relatif (relative path).
5. Loading harus ringan dan scalable.
6. Lazy loading untuk chapter, episode, dan halaman.
7. Natural Sorting untuk semua urutan item (Chapter, Episode, Images).

---

# Platform Support

- Minimum SDK: 26 (Android 8.0)
- Target SDK: Latest Stable

---

# Root Folder Structure

User memilih lokasi root folder melalui SAF Folder Picker saat first launch. Aplikasi menyimpan persisted URI permission.

```text
/Kitsune
│
├── Comics
├── Videos
├── Backup
├── Cache
└── .nomedia
```

Aplikasi wajib menginisialisasi struktur ini secara otomatis.

---

# .nomedia Policy

Aplikasi wajib membuat file `.nomedia` untuk mencegah media muncul di galeri.

---

# Comic Structure

```text
Comics
└── One Piece
    ├── cover.[jpg|jpeg|png|webp]
    ├── Chapter 1.cbz
    ├── Chapter 2.cbz
    └── .nomedia
```

Rules:
- Cover wajib bernama `cover` dengan ekstensi yang didukung.
- Chapter wajib format `Chapter X.cbz`.
- Ekstensi tidak ditampilkan di UI.
- Sorting wajib menggunakan **Natural Sorting**.

---

# Video Structure

```text
Videos
└── Naruto
    ├── poster.[jpg|jpeg|png|webp]
    ├── Eps 1.mp4
    ├── Eps 2.mkv
    └── .nomedia
```

Rules:
- Poster wajib bernama `poster` dengan ekstensi yang didukung.
- Episode wajib format `Eps X.ext`.
- Sorting wajib menggunakan **Natural Sorting**.

---

# CBZ Content Structure

```text
Chapter 1.cbz
│
├── image_1.jpg
├── image_2.png
├── image_3.webp
└── image_n.jpg
```

Rules:
- Penamaan tetap `image_n`.
- Ekstensi didukung: jpg, jpeg, png, webp.
- Sorting wajib menggunakan **Natural Sorting**.

---

# Loading Strategy

## Library Page
Hanya load folder name dan cover image menggunakan `DocumentFile`.

## Comic Detail Page
Load daftar chapter menggunakan `DocumentFile`.

## Reader Page
Load isi CBZ dan daftar image. Gunakan `ContentResolver` jika diperlukan untuk efisiensi stream.

---

# Database & Identification

- Database: Room.
- Identifier Utama: **Relative Path** dari Root Folder (bukan full URI atau absolute path).
- Hal ini menjamin integritas data (Bookmark/Progress) tetap valid meskipun Root Folder dipindahkan oleh user, selama struktur di dalamnya tetap.

---

# MVP Scope (v1)

Prioritas:
1. Infrastructure (SAF Setup & URI Persistence)
2. Splash Screen
3. Local Page
4. Comic Library
5. Comic Detail
6. Reader Engine (Natural Sorting)
7. Last Read
8. Bookmark
9. Playlist
10. Settings

---

# Vision

Kitsune fokus pada performa ringan, akses filesystem yang aman melalui SAF, dan pengalaman membaca yang konsisten dengan Natural Sorting.
