# Kitsune - Testing Strategy (v0.2)

## Purpose

Dokumen ini mendefinisikan strategi testing resmi Kitsune menggunakan Hybrid SAF dan Natural Sorting.

---

# Testing Philosophy

- **Stability First:** Prioritaskan stabilitas akses file dan akurasi progress.
- **Natural Sorting Accuracy:** Pastikan urutan item selalu benar bagi manusia.
- **SAF Resilience:** Uji ketahanan aplikasi saat permission URI hilang atau folder dipindahkan.

---

# Testing Scope

## Scanner Tests
- **Discovery:** Pastikan folder komik ditemukan via `DocumentFile`.
- **Natural Sorting:**
    - Input: `Chapter 1`, `Chapter 10`, `Chapter 2`.
    - Expected: `Chapter 1`, `Chapter 2`, `Chapter 10`.
- **Cover Detection:** Verifikasi deteksi `cover` dengan ekstensi jpg, jpeg, png, webp.
- **Incremental Scan:** Verifikasi folder tidak diproses ulang jika `lastModified` tidak berubah.

## Reader Tests
- **CBZ Streaming:** Verifikasi kelancaran pembukaan stream via `ContentResolver`.
- **Image Sorting:**
    - Input: `image_1.jpg`, `image_10.jpg`, `image_2.png`.
    - Expected: `image_1.jpg`, `image_2.png`, `image_10.jpg`.
- **Progress Saving:** Verifikasi progress disimpan menggunakan **Relative Path**.
- **Auto Transition:** Verifikasi transisi chapter menggunakan urutan Natural Sort.

## Infrastructure & SAF Tests
- **URI Persistence:** Verifikasi aplikasi tetap memiliki akses setelah proses restart.
- **Root Migration:** Verifikasi data (Bookmark/Progress) tetap valid jika root folder dipindahkan ke lokasi baru (selama struktur internal tetap).
- **SecurityException Handling:** Pastikan aplikasi tidak crash jika permission dicabut secara manual oleh user.

## Database Tests
- **Relative ID:** Verifikasi tabel tidak menyimpan absolute path.
- **Natural Sorting Order:** Verifikasi data playlist mengikuti urutan yang diinginkan.

---

# Integration Tests

- **SAF Selection Flow:** Splash -> SAF Picker -> Persist Permission -> Folder Creation.
- **End-to-End Reading:** Scan -> Open Detail -> Open Reader -> Change Chapter -> Verify Progress.

---

# Manual Testing Checklist

- **First Launch:** Pilih folder kosong, pastikan struktur `/Kitsune/` dibuat.
- **External Storage:** Uji menggunakan SD Card (jika perangkat mendukung).
- **Various Formats:** Uji CBZ berisi campuran ekstensi gambar (jpg & webp).
- **Natural Sort Edge Cases:** Folder dengan nama `1. One Piece`, `10. Naruto`, `2. Bleach`.

---

# Performance Testing

- **Startup Time:** Kecepatan memuat library dengan 100+ folder via SAF.
- **Reader Smoothness:** Transisi halaman tanpa stuttering saat streaming dari CBZ.

---

# Success Criteria

1. Tidak ada crash saat akses storage terputus.
2. Natural Sorting 100% akurat.
3. Progress saving selalu sinkron dengan relative path.
4. UI responsif meskipun akses SAF memiliki overhead.
