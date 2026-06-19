# Kitsune - Database Specification (v0.2)

## Purpose

Dokumen ini mendefinisikan struktur database resmi Kitsune menggunakan Room Database.

---

# Database Philosophy

Filesystem menyimpan media (Comics, Chapters, Videos, Episodes).
Room Database menyimpan metadata aplikasi (Bookmarks, Playlists, Reading Progress, Settings).

**Kunci Utama Identifikasi:**
Database menggunakan **Relative Path** dari Root Folder sebagai identifier untuk Comic dan Video, bukan Full URI atau Absolute Path.
Contoh: `Comics/One Piece`.

---

# Forbidden Storage

- Chapter List
- Episode List
- Page List
- Full URI / Absolute Path
- Media Files (Images, Videos)

---

# Database Engine

- **Official:** Room Database
- **Database Name:** `kitsune.db`

---

# Entity Overview

## Settings Entity
Menyimpan konfigurasi global.
Fields:
- `id`: Primary Key (fixed to 1)
- `rootFolderUri`: Full URI Root Folder yang dipilih via SAF (String)
- `gridSize`: Integer (2, 3, 4)
- `readingMode`: String (Vertical, LTR, RTL)
- `darkMode`: Boolean
- `oledBlack`: Boolean
- `keepScreenOn`: Boolean
- `showPageNumber`: Boolean
- `preloadPages`: Boolean

## Bookmark Entity
Representasi kategori bookmark.
Fields:
- `id`: Long (Auto Generate)
- `name`: String
- `createdAt`: Long

## BookmarkComic Entity
Relasi antara bookmark dan comic.
Fields:
- `id`: Long
- `bookmarkId`: Long (Foreign Key)
- `comicRelativePath`: String (e.g. `Comics/One Piece`)
- `createdAt`: Long

## Playlist Entity
Fields:
- `id`: Long
- `name`: String
- `createdAt`: Long

## PlaylistComic Entity
Fields:
- `id`: Long
- `playlistId`: Long
- `comicRelativePath`: String
- `position`: Integer (Natural Sorting order)
- `createdAt`: Long

## ReadingProgress Entity
Fields:
- `id`: Long
- `comicRelativePath`: String
- `chapterRelativePath`: String (e.g. `Comics/One Piece/Chapter 1.cbz`)
- `pageNumber`: Integer
- `totalPages`: Integer
- `lastReadAt`: Long

---

# Data Integrity Rules

1. **Relative Path Consistency:** Semua `relativePath` harus diawali dari folder kategori (`Comics/` atau `Videos/`).
2. **URI Handling:** Full URI hanya disimpan di `Settings.rootFolderUri`. Bagian aplikasi lainnya harus membangun Full URI secara dinamis dengan menggabungkan Root URI + Relative Path.
3. **Natural Sorting:** `PlaylistComic.position` diatur secara manual oleh user, tetapi defaultnya mengikuti Natural Sorting dari filesystem saat pertama ditambahkan.

---

# Performance Rules

1. **Index:** Gunakan index pada field `comicRelativePath` untuk query cepat pada tabel Progress dan Bookmark.
2. **No Blobs:** Jangan menyimpan gambar atau data biner dalam database.

---

# Database Decision Summary

1. Room Database.
2. Identifier: **Relative Path** (Relative to Root).
3. Full URI hanya di tabel Settings.
4. Tidak menyimpan daftar chapter atau halaman.
5. ReadingProgress mereferensikan relative path chapter.
