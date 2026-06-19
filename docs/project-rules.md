# Kitsune - Project Rules (v0.2)

## Purpose

Dokumen ini berisi aturan absolut proyek Kitsune. Jika terdapat konflik antara keputusan AI dan dokumen ini, dokumen ini harus diprioritaskan.

---

# Core Philosophy

Kitsune adalah:
- **Offline First**
- **Filesystem First (via Hybrid SAF)**
- **Performance Oriented**
- **Reader Focused**

---

# ALWAYS

AI Agent harus SELALU:

## Hybrid SAF First
Gunakan filesystem sebagai sumber data utama melalui Storage Access Framework.
- Simpan **Persisted URI Permission**.
- Gunakan **Relative Path** sebagai identifier di database.

## Natural Sorting
Wajib menggunakan **Natural Sorting** untuk:
- Urutan Chapter.
- Urutan Episode.
- Urutan Image dalam CBZ.

## Lazy Loading
Load data hanya ketika dibutuhkan (Library -> Detail -> Reader).

## Min SDK 26
Patuhi batasan dan fitur mulai dari Android 8.0+.

## Use Kotlin & Compose & MVVM
Seluruh kode wajib mengikuti stack modern ini.

---

# NEVER

AI Agent tidak boleh melakukan hal berikut:

## Never Store Absolute Paths or Full URIs
Dilarang menyimpan path absolut atau full URI sebagai identifier utama di database (kecuali Root URI di Settings). Gunakan **Relative Path**.

## Never Store Chapters or Page Lists In Database
Metadata file harus dibaca langsung dari filesystem saat dibutuhkan.

## Never Scan CBZ At Startup
Startup hanya boleh memindai folder kategori dan metadata dasar (Title, Cover).

## Never Use Java, XML, or Fragments
Proyek ini murni Kotlin, Compose, dan Single Activity (Navigation Compose).

## Never Break Naming Convention
- Cover/Poster: Nama `cover` atau `poster`.
- CBZ Images: `image_n`.

---

# DO

## Do Handle SAF Gracefully
Tangani kasus di mana permission URI hilang atau folder root tidak lagi dapat diakses.

## Do Use Numeric/Natural Sorting
Pastikan `Chapter 2` muncul sebelum `Chapter 10`.

## Do Support Multiple Image Extensions
Dukung `jpg`, `jpeg`, `png`, dan `webp` untuk cover, poster, dan isi CBZ.

---

# Performance Rules

- Seluruh akses SAF dan Database wajib di `Dispatchers.IO`.
- Gunakan `ContentResolver` untuk streaming data gambar demi performa.
- Implementasikan **Incremental Scan** berdasarkan `lastModified`.

---

# Decision Hierarchy

1. **AI_AGENT_CONTEXT.md**
2. **project-rules.md**
3. **foundation.md**
4. **architecture.md**
5. Dokumen lain

---

# Project Rules Summary

1. Hybrid SAF (URI Persistence).
2. Relative Path as ID.
3. Natural Sorting wajib.
4. Offline First.
5. Lazy Loading.
6. Kotlin, Compose, MVVM.
7. Jangan simpan daftar chapter di database.
8. Dukung jpg, jpeg, png, webp.
9. Min SDK 26.
10. Prioritaskan performa dan konsistensi.
