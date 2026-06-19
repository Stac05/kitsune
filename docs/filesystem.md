# Kitsune - Filesystem Specification (v0.2)

## Purpose

Dokumen ini mendefinisikan seluruh aturan filesystem Kitsune menggunakan Hybrid SAF (Storage Access Framework).

---

# Storage Strategy

## Root Folder Selection
User memilih root folder melalui SAF Folder Picker. Aplikasi wajib meminta dan menyimpan **Persisted URI Permission** agar akses tidak hilang setelah aplikasi ditutup atau perangkat direboot.

## Hybrid Access
- **Scanner:** Menggunakan `DocumentFile` untuk navigasi hirarki folder dan deteksi file.
- **Reader:** Menggunakan `ContentResolver` untuk membuka file (e.g. `openInputStream`) demi performa streaming yang lebih baik.

---

# Root Structure

```text
/Kitsune (Selected Root)
│
├── Comics
├── Videos
├── Backup
├── Cache
└── .nomedia
```

---

# .nomedia Policy

Aplikasi wajib membuat file `.nomedia` di root folder `/Kitsune/`.

---

# Comic Structure

```text
Comics
└── One Piece
    ├── cover.[jpg|jpeg|png|webp]
    ├── Chapter 1.cbz
    ├── Chapter 2.cbz
    └── .nomedia
```

## Cover Rules
Nama file wajib: `cover`.
Ekstensi yang didukung: `jpg`, `jpeg`, `png`, `webp`.
Contoh valid: `cover.jpg`, `cover.webp`.

## Chapter Rules
Format resmi: `Chapter X.cbz`.
Nomor chapter numerik diproses menggunakan **Natural Sorting**.

---

# CBZ Content Structure

```text
Chapter 1.cbz
│
├── image_1.jpg
├── image_2.png
├── image_n.webp
└── ...
```

## Image Naming
Naming tetap: `image_n`.
Ekstensi didukung: `jpg`, `jpeg`, `png`, `webp`.

## Image Sorting
Wajib menggunakan **Natural Sorting**.
Contoh urutan: `image_1`, `image_2`, `image_10`.

---

# Video Structure

```text
Videos
└── Naruto
    ├── poster.[jpg|jpeg|png|webp]
    ├── Eps 1.mp4
    ├── Eps 2.mkv
    └── .nomedia
```

## Poster Rules
Nama file wajib: `poster`.
Ekstensi yang didukung: `jpg`, `jpeg`, `png`, `webp`.

## Episode Rules
Format resmi: `Eps X.ext`.
Sorting menggunakan **Natural Sorting**.

---

# Identification System

Aplikasi tidak menyimpan Full URI atau Absolute Path di database untuk mengidentifikasi konten.
Gunakan **Relative Path** dari Root Folder sebagai identifier.

Contoh:
- Root URI: `content://com.android.externalstorage.documents/tree/primary%3AKitsune`
- Comic Relative Path: `Comics/One Piece`
- Chapter Relative Path: `Comics/One Piece/Chapter 1.cbz`

Keuntungan: Jika user memindahkan folder `Kitsune` ke lokasi lain, data progress tetap valid selama user memilih root folder baru tersebut.

---

# Library Scan Rules (v0.2)

1. Ambil Root URI dari Settings.
2. Gunakan `DocumentFile.fromTreeUri`.
3. Telusuri folder `Comics` dan `Videos`.
4. Untuk setiap sub-folder, cari file `cover` atau `poster`.
5. Gunakan **Natural Sorting** saat menampilkan list folder dan file.
6. Hindari scanning rekursif yang dalam; scan chapter hanya saat detail komik dibuka.

---

# Filesystem Decision Summary

1. Hybrid SAF (DocumentFile + ContentResolver).
2. Persisted URI Permission wajib.
3. Relative Path sebagai identifier utama di DB.
4. Cover/Poster: Nama `cover`/`poster` dengan ekstensi fleksibel (jpg, jpeg, png, webp).
5. Natural Sorting untuk semua urutan.
6. Min SDK 26.
