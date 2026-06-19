# Kitsune - Scanner Engine Specification (v0.2)

## Purpose

Dokumen ini mendefinisikan perilaku Scanner Engine Kitsune menggunakan Hybrid SAF dan Natural Sorting.

---

# Scanner Philosophy

- **Hybrid SAF:** Menggunakan `DocumentFile` untuk pemindaian hirarki folder yang aman dan kompatibel.
- **Incremental Scan:** Menggunakan atribut `lastModified` dari `DocumentFile` untuk menentukan apakah folder perlu dipindai ulang.
- **Relative Mapping:** Hasil scan dikonversi menjadi **Relative Path** (relatif terhadap Root URI) untuk disimpan di database.
- **Natural Sorting:** Semua daftar yang ditemukan (Comic, Chapter, Video, Episode) wajib diurutkan secara natural.

---

# Responsibilities

- Deteksi folder Root (via Persisted URI).
- Inisialisasi struktur folder (Comics, Videos, Backup, Cache).
- Pemindaian Library (Comic & Video).
- Deteksi Cover dan Poster dengan ekstensi fleksibel (jpg, jpeg, png, webp).
- Pengelolaan metadata ringan (Title, Relative Path).

---

# Scanning Strategy

## Startup Scan
- Hanya memindai folder kategori (`Comics/` dan `Videos/`).
- Mengambil judul folder dan URI Cover/Poster.
- **Lazy Load:** Tidak memindai daftar chapter atau episode di tahap ini.

## Incremental Scan
1. Ambil list folder dari filesystem via `DocumentFile`.
2. Bandingkan `lastModified` filesystem dengan data di database.
3. Hanya proses folder yang berubah atau baru.
4. Hapus entri database jika folder fisiknya sudah tidak ada.

## Comic Detail Scan
- Dilakukan hanya saat user membuka layar detail komik.
- Memindai file `.cbz` di dalam folder komik terkait.
- Mengurutkan hasil menggunakan **Natural Sorting**.

---

# Cover & Poster Detection

1. Cari file dengan nama tepat `cover` (untuk komik) atau `poster` (untuk video).
2. Dukung ekstensi: `.jpg`, `.jpeg`, `.png`, `.webp`.
3. Jika terdapat multiple file (e.g. `cover.jpg` dan `cover.webp`), pilih salah satu (prioritas tidak ditentukan, namun konsisten).

---

# Threading & Performance

- **Background Execution:** Wajib dijalankan di `Dispatchers.IO`.
- **SAF Optimization:** Mengingat `DocumentFile` bisa lebih lambat dari File API, gunakan `listFiles()` secara efisien dan hindari operasi `findFile()` berulang dalam loop.

---

# Scanner Decision Summary

1. Hybrid SAF (`DocumentFile`).
2. Relative Path mapping.
3. Natural Sorting untuk semua penemuan file/folder.
4. Incremental scan berdasarkan `lastModified`.
5. Deteksi cover/poster fleksibel (4 ekstensi).
