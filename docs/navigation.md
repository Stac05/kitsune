# Kitsune - Navigation Specification (v1.0)

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
4. Settings (Konfigurasi)

---

# Route Definitions

## Parameters Handling
Karena Kitsune menggunakan **Relative Path** sebagai identifier, route parameter wajib di-encode menggunakan `Uri.encode()` sebelum dimasukkan ke dalam route dan di-decode saat diterima guna menangani karakter khusus seperti `/`.

## Main Routes
- `splash`: Entry point aplikasi.
- `bookmark`: Daftar kategori bookmark.
- `playlist`: Daftar kategori playlist.
- `local`: Home screen (Last Read & Shortcuts).
- `other`: Layar Settings (tetap menggunakan route internal `other` namun ditampilkan sebagai "Settings" di UI).

## Child Routes
- `comic_library`: Grid seluruh koleksi komik.
- `video_library`: Grid seluruh koleksi video (Phase 7+).
- `comic_detail/{comicRelativePath}`: Detail informasi komik dan daftar chapter.
- `bookmark_detail/{bookmarkId}`: Daftar komik dalam kategori bookmark tertentu.
- `playlist_detail/{playlistId}`: Daftar komik dalam kategori playlist tertentu.
- `reader/{comicRelativePath}/{chapterRelativePath}`: Reader manga/komik.

---

# Reader Navigation

## Auto Chapter Transition
Reader mendukung perpindahan chapter otomatis (Next/Previous) berdasarkan urutan Natural Sorting. State navigasi diperbarui secara internal di dalam Reader untuk menjaga performa.

---

# Back Navigation Rules

- **Reader -> Comic Detail**
- **Comic Detail -> Comic Library**
- **Comic Library -> Local**
- **Root Destinations (Bookmark, Playlist, Local, Settings) -> Exit App**

---

# State Restoration

Navigation Compose mempertahankan state (seperti scroll position) saat berpindah antar tab utama di Bottom Navigation melalui penggunaan `saveState = true` dan `restoreState = true`.

---

# Navigation Decision Summary

1. Navigation Compose.
2. Relative Path wajib URL Encoded.
3. Splash menangani alur SAF Root Selection.
4. Label UI "Settings" menggantikan "Other" demi kejelasan UX.
