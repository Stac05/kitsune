# Kitsune - Architecture Specification (v0.2)

## Purpose

Dokumen ini mendefinisikan arsitektur teknis resmi untuk aplikasi Kitsune.

---

# Technology Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Architecture Pattern:** MVVM
- **Database:** Room Database
- **Image Loading:** Coil
- **Navigation:** Navigation Compose
- **Storage Access:** Hybrid SAF (Storage Access Framework)
- **Min SDK:** 26 (Android 8.0)

---

# Architectural Principles

## Hybrid SAF First
Filesystem adalah sumber data utama yang diakses melalui SAF.
- Gunakan `DocumentFile` untuk scanning dan browsing.
- Gunakan `ContentResolver` untuk streaming data (e.g. membaca CBZ).
- Simpan **Persisted URI Permission** untuk akses jangka panjang.

## Relative Identification
Database TIDAK menyimpan absolute path atau full URI sebagai identifier utama.
- Gunakan **Relative Path** dari Root Folder (e.g. `Comics/One Piece`).
- Hal ini memungkinkan data (Progress/Bookmark) tetap valid jika Root Folder dipindahkan.

## Natural Sorting
Semua komponen yang menampilkan list berurutan (Chapter, Episode, CBZ Images) wajib menggunakan logika **Natural Sorting**.

## Lazy Loading
Jangan pernah melakukan scanning berat (e.g. isi CBZ) pada saat library browsing.

---

# Module Structure

```text
app
│
├── core        (Constants, Utils, NaturalSort, StorageHelper)
├── data        (Repositories, DataSources, SAF implementation)
├── domain      (Models, UseCases)
├── ui          (Compose Screens, ViewModels)
├── reader      (CBZ Parser, Image Handling)
├── scanner     (DocumentFile Scanner, Relative Path logic)
├── navigation  (Routes, URI Encoding)
└── database    (Room, Entities, TypeConverters)
```

---

# Data & Repository Rules

## Repository Responsibilities
- Mengonversi Relative Path di database menjadi URI yang dapat diakses SAF.
- Mengelola permission URI.

## Scanner Responsibilities
- Menggunakan `DocumentFile` untuk memindai struktur.
- Menghasilkan model dengan relative path.

---

# State Management

Gunakan `StateFlow` untuk state UI.
Gunakan `Result` wrapper untuk penanganan error.

---

# Dependency Rules

- **UI -> Domain**
- **Domain -> Data**
- **Data -> Database & Scanner & Reader**
- **Scanner & Reader -> Core (StorageHelper)**

---

# Performance Rules

- **Async SAF:** Akses SAF wajib di `Dispatchers.IO`.
- **Incremental Scan:** Cek `lastModified` sebelum melakukan scan ulang folder.
- **Cache URI:** Cache `DocumentFile` objek jika diperlukan untuk sesi aktif, jangan simpan permanen.

---

# Architecture Decision Summary

1. MVVM + Compose.
2. Hybrid SAF (URI Persistence).
3. Relative Path Identifiers.
4. Natural Sorting.
5. Min SDK 26.
6. Room untuk metadata.
7. Coil untuk gambar.
