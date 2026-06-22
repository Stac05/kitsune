# Kitsune - Reader Engine Specification (v1.0)

## Purpose

Dokumen ini mendefinisikan perilaku Reader Engine Kitsune menggunakan Hybrid SAF dan Natural Sorting.

---

# Supported Formats

## Chapter Format
- Official: `Chapter X.cbz`
- Access: Menggunakan `ContentResolver` untuk streaming data dari URI SAF.

## Image Extensions
Di dalam CBZ, file gambar berikut didukung:
- `.jpg`, `.jpeg`, `.png`, `.webp`

---

# Reader Flow

1. **Input:** `comicRelativePath` dan `chapterRelativePath`.
2. **URI Resolution:** Gabungkan Root URI (dari Settings) dengan relative path untuk mendapatkan Full URI.
3. **Open Stream:** Gunakan `contentResolver.openInputStream(uri)`.
4. **Parse CBZ:** Ekstrak daftar entri gambar melalui `CbzParser`.
5. **Natural Sorting:** Urutkan gambar berdasarkan nama file secara natural (e.g., `image_2.jpg` sebelum `image_10.jpg`).
6. **Render:** Tampilkan halaman menggunakan Coil dengan `CbzImageFetcher` kustom.

---

# Reading Modes

- **Vertical:** Scroll kontinu secara vertikal (LazyColumn).
- **Left To Right (LTR):** Pindah halaman horizontal ke kanan (HorizontalPager).
- **Right To Left (RTL):** Pindah halaman horizontal ke kiri (Manga style).

---

# Navigation & Progress

## Page Slider
Memungkinkan lompatan cepat antar halaman menggunakan slider UI yang tersinkronisasi dengan state Reader.

## Progress Saving
Simpan `pageNumber` dan `totalPages` ke tabel `ReadingProgress` setiap kali halaman berubah. Menggunakan `comicRelativePath` sebagai identifier utama dan `chapterRelativePath` untuk posisi terakhir.

## Auto Chapter Transition
- **Next Chapter:** Jika di halaman terakhir, muat chapter berikutnya berdasarkan **Natural Sorting** file di dalam folder komik.
- **Previous Chapter:** Jika di halaman pertama, muat chapter sebelumnya.

---

# Memory & Performance

## Reader Metadata Cache
- **Mekanisme:** Menggunakan `pageCache` (Memory Map) di `ReaderRepository`.
- **Fungsi:** Menyimpan daftar `Page` yang sudah di-parse berdasarkan key (path + lastModified). Hal ini menghilangkan delay parsing ZIP saat navigasi antar chapter yang sudah pernah dibuka.

## Preloading
Coil secara otomatis melakukan preloading halaman berikutnya untuk kelancaran transisi.

---

# Error Handling

## SecurityException Handling
- **Mekanisme:** Seluruh akses `ContentResolver` di `CbzParser` dibungkus dalam blok try-catch.
- **Behavior:** Jika permission SAF dicabut oleh sistem saat aplikasi berjalan, Reader akan menangkap `SecurityException` dan menampilkan pesan error informatif kepada user alih-alih crash.

## Missing/Corrupted Data
- **Missing Chapter:** Tampilkan pesan "Chapter file not found".
- **Empty CBZ:** Tampilkan pesan "No pages found".

---

# Known Limitations

- **Linear ZIP Access:** `ZipInputStream` memerlukan iterasi linear dari awal file untuk mencapai entri tertentu. Pada file CBZ yang sangat besar (500+ halaman), jumping ke halaman akhir mungkin mengalami delay I/O kecil.

---

# Reader Decision Summary

1. Hybrid SAF (ContentResolver).
2. Natural Sorting wajib.
3. Reader Metadata Cache (`pageCache`).
4. SecurityException resilience.
5. Auto transition antar chapter.
