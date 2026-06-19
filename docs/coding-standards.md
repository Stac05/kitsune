# Kitsune - Coding Standards (v0.2)

## Purpose

Dokumen ini mendefinisikan standar coding resmi Kitsune menggunakan Hybrid SAF dan Natural Sorting.

---

# Language & UI Rules

- **Official Language:** Kotlin (Min SDK 26).
- **Official UI Framework:** Jetpack Compose.
- **Forbidden:** Java, XML Layouts, Fragments, LiveData.

---

# Architecture Rules

- **Pattern:** MVVM (UI -> ViewModel -> Repository -> Data Source).
- **State Management:** `StateFlow` untuk UI state.
- **Navigation:** Navigation Compose (Wajib URL Encoding untuk Relative Path).

---

# Storage Access Rules (Hybrid SAF)

- **Scanner Layer:** Gunakan `DocumentFile` untuk navigasi direktori.
- **Reader/Stream Layer:** Gunakan `ContentResolver.openInputStream` untuk performa.
- **Persistence:** Wajib mengelola **Persisted URI Permission**.
- **Forbidden:** Penggunaan `java.io.File` untuk akses library user.

---

# Identification & Sorting

- **Identifier:** Wajib menggunakan **Relative Path** (relatif terhadap root) di dalam database dan antar layer aplikasi.
- **Sorting:** Wajib menggunakan logika **Natural Sorting** untuk semua list (Chapter, Episode, Images).
    - Implementasi Natural Sorting harus menangani angka di tengah string (e.g. `image_2` < `image_10`).

---

# Database Rules

- **Engine:** Room Database.
- **Rule:** Jangan simpan full URI di tabel selain `Settings`.
- **Rule:** Jangan simpan daftar file (Chapter/Page) ke database.

---

# Coroutine Rules

- **IO Operations:** Seluruh akses SAF dan Database wajib di `Dispatchers.IO`.
- **UI Operations:** State updates di `Dispatchers.Main`.

---

# Error Handling

- Gunakan `Result<T>` wrapper.
- Penanganan khusus untuk `SecurityException` saat akses URI SAF yang permission-nya kadaluarsa.

---

# Coding Standards Summary

1. Kotlin (Min SDK 26).
2. Jetpack Compose + MVVM.
3. Hybrid SAF (DocumentFile + ContentResolver).
4. Relative Path as ID.
5. Natural Sorting wajib.
6. StateFlow wajib.
7. Room untuk metadata.
