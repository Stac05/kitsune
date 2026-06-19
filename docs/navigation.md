# Kitsune - Navigation Specification (v0.2)

## Purpose

Dokumen ini mendefinisikan seluruh alur navigasi resmi Kitsune menggunakan Navigation Compose.

---

# Navigation System

- **Official:** Navigation Compose
- **Pattern:** Screen -> Route -> Navigation Graph

---

# Application Flow

```text
Splash
↓
Initialization (Check SAF Permission)
↓
Local
```

---

# First Launch Flow (SAF Integration)

1. **Splash Screen:** Cek apakah `rootFolderUri` sudah ada di Settings.
2. **Root Selection:** Jika belum ada, arahkan ke SAF Folder Picker.
3. **Permission:** Simpan **Persisted URI Permission**.
4. **Initialization:** Buat struktur folder `/Kitsune/` (Comics, Videos, dll).
5. **Local Screen:** Masuk ke home.

---

# Bottom Navigation

1. Bookmark
2. Playlist
3. Local (Home)
4. Other (Settings)

---

# Route Definitions

## Parameters Handling
Karena Kitsune menggunakan **Relative Path** sebagai identifier, route parameter mungkin mengandung karakter `/`.
**Rule:** Semua relative path wajib di-encode menggunakan `Uri.encode()` sebelum dimasukkan ke dalam route dan di-decode saat diterima.

## Main Routes
- `splash`: Entry point.
- `bookmark`: Daftar bookmark.
- `playlist`: Daftar playlist.
- `local`: Home screen.
- `other`: Settings.

## Child Routes
- `comic_library`: Grid seluruh komik.
- `video_library`: Grid seluruh video.
- `comic_detail/{comicRelativePath}`: Detail komik.
- `video_detail/{videoRelativePath}`: Detail video.
- `reader/{comicRelativePath}/{chapterRelativePath}`: Reader manga.

---

# Reader Navigation

## Auto Chapter Transition
Reader mendukung perpindahan chapter otomatis (Next/Previous) tanpa harus kembali ke `comic_detail`.
State navigasi harus diperbarui saat chapter berpindah.

---

# Back Navigation Rules

- **Reader -> Comic Detail**
- **Comic Detail -> Comic Library**
- **Comic Library -> Local**
- **Root Destinations (Bookmark, Playlist, Local, Other) -> Exit App**

---

# State Restoration

Navigation Compose wajib mempertahankan state (scroll position, dll) saat berpindah antar tab Bottom Navigation.

---

# Navigation Decision Summary

1. Navigation Compose.
2. Relative Path sebagai parameter route (Wajib URL Encoded).
3. Splash menangani flow SAF Root Selection.
4. Reader mendukung navigasi antar chapter internal.
5. Back stack harus konsisten sesuai hirarki folder.
