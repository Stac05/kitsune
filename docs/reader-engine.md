# Kitsune - Reader Engine Specification (v0.2)

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
4. **Parse CBZ:** Ekstrak daftar entri gambar.
5. **Natural Sorting:** Urutkan gambar berdasarkan nama file secara natural (e.g., `image_2.jpg` sebelum `image_10.jpg`).
6. **Render:** Tampilkan halaman menggunakan Coil.

---

# Reading Modes

- **Vertical:** Scroll kontinu secara vertikal.
- **Left To Right (LTR):** Pindah halaman horizontal ke kanan.
- **Right To Left (RTL):** Pindah halaman horizontal ke kiri (Manga style).

---

# Navigation & Progress

## Page Slider
Memungkinkan lompatan cepat antar halaman menggunakan slider UI.

## Progress Saving
Simpan `pageNumber` dan `totalPages` ke tabel `ReadingProgress` setiap kali halaman berubah. Gunakan `chapterRelativePath` sebagai identifier.

## Auto Chapter Transition
- **Next Chapter:** Jika di halaman terakhir, muat chapter berikutnya berdasarkan **Natural Sorting** file di dalam folder komik.
- **Previous Chapter:** Jika di halaman pertama, muat chapter sebelumnya.

---

# Memory & Performance

- **Lazy Loading:** Jangan muat seluruh gambar ke memori. Gunakan `Coil` dengan subsampling jika memungkinkan.
- **Preloading:** Muat 1-2 halaman di depan/belakang untuk kelancaran transisi.
- **Stream Efficiency:** Pastikan InputStream ditutup segera setelah data dibaca.

---

# Error Handling

- **Missing Chapter:** Jika file CBZ tidak ditemukan (mungkin dihapus di luar aplikasi), tampilkan pesan error dan tawarkan untuk rescan.
- **Corrupted Image:** Jika satu gambar gagal dimuat, tampilkan ikon error dan izinkan user lanjut ke halaman berikutnya.

---

# Reader Decision Summary

1. Hybrid SAF (ContentResolver).
2. Natural Sorting untuk urutan gambar dan chapter.
3. Mendukung jpg, jpeg, png, webp.
4. Progress disimpan berdasarkan Relative Path.
5. Auto transition antar chapter wajib.
