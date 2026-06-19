# Kitsune - Release Plan (v0.1)

## Purpose

Dokumen ini mendefinisikan strategi rilis resmi Kitsune.

AI Agent wajib menggunakan dokumen ini untuk menentukan target implementasi setiap versi.

---

# Release Philosophy

Prioritas utama:

```text
Stable First
Features Second
```

Kitsune tidak mengejar banyak fitur.

Kitsune mengejar:

- Stability
- Performance
- Reader Experience

---

# Release Lifecycle

```text
Prototype
↓
Alpha
↓
Beta
↓
Release Candidate
↓
Stable
```

---

# Versioning Strategy

Format:

```text
MAJOR.MINOR.PATCH
```

Contoh:

```text
0.1.0
0.2.0
0.2.1
1.0.0
```

---

# v0.1.0

## Foundation Release

Status:

```text
Target MVP Foundation
```

---

Goals

- Setup Project
- Setup Architecture
- Setup Navigation
- Setup Database
- Setup Scanner Foundation

---

Features

```text
Splash
Root Folder Selection
Settings Foundation
Local Screen
```

---

Success Criteria

```text
Project berjalan stabil.
```

---

# v0.2.0

## Library Release

Status:

```text
Planned
```

---

Goals

Menampilkan library comic lokal.

---

Features

```text
Comic Scanner
Comic Library
Cover Loading
Search
Incremental Scan
```

---

Success Criteria

```text
Comic Library dapat digunakan.
```

---

# v0.3.0

## Reader Release

Status:

```text
Planned
```

---

Goals

Mengimplementasikan fitur utama.

---

Features

```text
Comic Detail
Chapter List
CBZ Reader
Vertical Mode
LTR Mode
RTL Mode
Progress Saving
```

---

Success Criteria

```text
Comic dapat dibaca dengan lancar.
```

---

# v0.4.0

## Reader Enhancement Release

Status:

```text
Planned
```

---

Features

```text
Page Slider
Auto Next Chapter
Auto Previous Chapter
Continue Reading
Last Read
```

---

Success Criteria

```text
Reader experience lengkap.
```

---

# v0.5.0

## Organization Release

Status:

```text
Planned
```

---

Features

```text
Bookmarks
Playlists
Bookmark Search
Playlist Search
```

---

Success Criteria

```text
Library dapat diorganisasi.
```

---

# v0.6.0

## Settings & Polish Release

Status:

```text
Planned
```

---

Features

```text
Grid Size
Dark Mode
OLED Black
Keep Screen On
Reading Mode Settings
```

---

Success Criteria

```text
Seluruh setting stabil.
```

---

# v0.7.0

## Optimization Release

Status:

```text
Planned
```

---

Goals

Optimasi performa.

---

Features

```text
Cache Improvements
Startup Improvements
Reader Optimization
Database Optimization
```

---

Success Criteria

```text
Library besar tetap responsif.
```

---

# v0.8.0

## Beta Release

Status:

```text
Planned
```

---

Goals

Persiapan menuju stable.

---

Focus

```text
Bug Fixes
Testing
Stability
```

---

Success Criteria

```text
Tidak ada bug kritis.
```

---

# v0.9.0

## Release Candidate

Status:

```text
Planned
```

---

Goals

Final validation.

---

Focus

```text
Regression Testing
Performance Validation
Documentation Review
```

---

Success Criteria

```text
Siap menuju 1.0.
```

---

# v1.0.0

## Stable Release

Status:

```text
Target Stable
```

---

Goals

Rilis stabil pertama.

---

Features

```text
Comic Library
Comic Detail
Reader
Bookmarks
Playlists
Settings
Progress Saving
```

---

Out Of Scope

```text
Video Support
Cloud Features
Plugin System
```

---

Success Criteria

```text
Stable Daily Use
```

---

# Post 1.0 Roadmap

Setelah 1.0:

---

## v1.1

Possible Features

```text
Video Library
Video Detail
Video Progress
```

---

## v1.2

Possible Features

```text
History
Collections
Tags
```

---

## v1.3

Possible Features

```text
Metadata Support
Manual Metadata Editing
```

---

## v1.4

Possible Features

```text
Backup System
Restore System
```

---

## v2.0

Possible Features

```text
Multiple Libraries
Plugin System
Advanced Metadata
```

---

# Release Rules

Fitur tidak boleh masuk release jika:

```text
Critical Bugs Exist
Reader Unstable
Scanner Unstable
```

---

# Quality Gates

Sebelum release:

```text
Testing Complete
Documentation Updated
No Critical Crashes
```

---

# AI Agent Rules

Saat mengerjakan fitur:

1. Tentukan target release.
2. Pastikan fitur berada pada release tersebut.
3. Jangan mengimplementasikan fitur dari release yang lebih jauh tanpa instruksi.
4. Prioritaskan stabilitas.

---

# Release Plan Summary

1. Foundation.
2. Library.
3. Reader.
4. Reader Enhancements.
5. Bookmarks & Playlists.
6. Settings.
7. Optimization.
8. Beta.
9. Release Candidate.
10. Stable 1.0.
