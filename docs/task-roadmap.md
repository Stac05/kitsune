# Kitsune - Task Roadmap (v0.2)

## Purpose

Dokumen ini mendefinisikan urutan pengerjaan resmi Kitsune berdasarkan arsitektur Hybrid SAF dan Relative Path.

---

# Development Phases

```text
Phase 0 - Project Setup
Phase 1 - Core Infrastructure (SAF & Database)
Phase 2 - Library System (Scanner)
Phase 3 - Reader Engine (Natural Sorting)
Phase 4 - Bookmark & Playlist
Phase 5 - Settings & Polish
Phase 6 - Optimization
Phase 7 - Video Support
```

---

# Phase 0: Project Setup

## Tasks
- **Android Project:** Setup Min SDK 26, Target SDK Stable.
- **Dependencies:** Compose, Room, Navigation Compose, Coil, Activity-Ktx.
- **Package Structure:** Setup core, data, domain, ui, reader, scanner.

---

# Phase 1: Core Infrastructure

## Tasks
- **Storage Helper:** Implementasi SAF Picker, URI Persistence, dan `DocumentFile` utilities.
- **Settings System:** Entity untuk menyimpan `rootFolderUri`.
- **Initialization:** Logic untuk membuat folder `/Kitsune/Comics` dan `/Kitsune/Videos` via SAF.
- **Navigation:** Setup Navigation Graph dengan dukungan URL Encoding untuk Relative Path.
- **Splash Screen:** Flow pengecekan permission SAF.

---

# Phase 2: Library System

## Tasks
- **Scanner Engine:** Implementasi `DocumentFile` scanning dengan Relative Path mapping.
- **Natural Sorting:** Implementasi helper untuk Natural Sorting (alfabet + numerik).
- **Incremental Scan:** Logic perbandingan `lastModified`.
- **UI:** Comic Library Grid dengan cover detection (jpg, jpeg, png, webp).

---

# Phase 3: Reader Engine

## Tasks
- **Comic Detail:** Chapter list dengan Natural Sorting.
- **CBZ Parser:** Streaming image via `ContentResolver`.
- **Reader UI:** Vertical, LTR, RTL modes.
- **Progress System:** Simpan progress berdasarkan Relative Path chapter.
- **Auto Transition:** Navigasi antar chapter menggunakan Natural Sorting.

---

# Phase 4: Bookmark & Playlist

## Tasks
- **Data Layer:** Implementasi Bookmark/Playlist menggunakan `comicRelativePath`.
- **UI:** CRUD Bookmark & Playlist.

---

# Phase 5: Settings & Polish

## Tasks
- **Reading Settings:** Mode, Auto Next.
- **Appearance:** Grid Size, Dark Mode, OLED Black.
- **Library:** Change Root Folder (SAF Re-picker).

---

# Phase 6: Optimization

## Tasks
- **SAF Performance:** Optimasi pemindaian `DocumentFile`.
- **Memory:** Coil memory management untuk image resolusi tinggi.

---

# Phase 7: Video Support

- **Video Scanner:** Deteksi video dan poster.
- **Player:** Media3 integration (Future).

---

# Roadmap Summary

1. Setup Project (Min SDK 26).
2. Infrastructure (SAF & Root Selection).
3. Library (Scanner & Natural Sort).
4. Reader (CBZ Streaming & Progress).
5. Bookmark & Playlist (Relative ID).
6. Settings & Polish.
7. Optimization.
