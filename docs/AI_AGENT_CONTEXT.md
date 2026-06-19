# AI_AGENT_CONTEXT.md

## Purpose

Dokumen ini adalah entry point utama untuk seluruh AI Agent yang bekerja pada proyek Kitsune.

Baca dokumen ini terlebih dahulu sebelum membaca dokumen lainnya.

---

# Project Overview

Nama:

```text
Kitsune
```

Jenis aplikasi:

```text
Offline Manga Reader
Offline Comic Reader
Offline Media Library
```

Platform:

```text
Android (Min SDK 26)
```

---

# Core Philosophy

Kitsune adalah:

```text
Offline First
Filesystem First (via Hybrid SAF)
Reader Focused
Performance Oriented
```

Prioritas utama:

```text
Stability
Performance
Reader Experience
```

---

# MVP Scope

Fokus saat ini:

```text
Comic Reader
Comic Library
Bookmarks
Playlists
Settings
Progress Saving
```

Tidak termasuk:

```text
Video Support (Phase 7+)
Cloud Features
Online Manga
Accounts
Streaming
Plugin System
```

---

# Technology Stack

Language:

```text
Kotlin
```

UI:

```text
Jetpack Compose
```

Architecture:

```text
MVVM
```

Navigation:

```text
Navigation Compose
```

Database:

```text
Room Database
```

Images:

```text
Coil
```

Storage Access:

```text
Hybrid SAF (Storage Access Framework)
```

---

# Absolute Rules

ALWAYS:

```text
Use Kotlin
Use Compose
Use MVVM
Use StateFlow
Use Lazy Loading
Use Filesystem First
Use Natural Sorting
Use Persisted URI Permissions
```

NEVER:

```text
Use Java
Use XML
Use Fragments
Store Full URIs as primary identifier
Store Chapters In Database
Store Pages In Database
Scan CBZ At Startup
```

---

# Filesystem Summary

Root:

```text
/Kitsune (Selected via SAF Picker)
│
├── Comics
├── Videos
├── Backup
├── Cache
└── .nomedia
```

---

Comic Structure:

```text
One Piece
│
├── cover.[jpg|jpeg|png|webp]
├── Chapter 1.cbz
├── Chapter 2.cbz
└── .nomedia
```

---

Video Structure:

```text
Naruto
│
├── poster.[jpg|jpeg|png|webp]
├── Eps 1.mp4
├── Eps 2.mkv
└── .nomedia
```

---

CBZ Structure:

```text
image_1.[jpg|jpeg|png|webp]
image_2.[jpg|jpeg|png|webp]
image_3.[jpg|jpeg|png|webp]
...
image_n.[jpg|jpeg|png|webp]
```

Natural sorting wajib.

---

# Database Summary

Room hanya menyimpan:

```text
Settings
Bookmarks
Playlists
Reading Progress
```

Identifier: Gunakan **Relative Path** dari Root (bukan Full URI/Absolute Path).

---

# Reader Rules

Supported:

```text
Vertical
Left To Right
Right To Left
```

Mandatory:

```text
Progress Saving
Continue Reading
Page Slider
Auto Next Chapter
Auto Previous Chapter
Natural Sorting
```

---

# Scanner Rules

Startup hanya load:

```text
Title
Cover
Poster
```

Jangan load:

```text
Chapter List
Episode List
CBZ Content
```

Use `DocumentFile` for scanning.

---

# Navigation Summary

Main Flow:

```text
Splash
↓
Local
↓
Comic Library
↓
Comic Detail
↓
Reader
```

Bottom Navigation:

```text
Bookmark
Playlist
Local
Other
```

---

# Branding

Official Assets:

```text
assets/branding/
```

Files:

```text
kitsune-logo-app.png
kitsune-logo-splash.png
```

Rules:

```text
Use Official Assets
Do Not Create Placeholder Logos
Do Not Replace Branding
```

---

# Current Priority

Current Development Target:

```text
Comic Reader MVP
```

Priority Order:

```text
Infrastructure (SAF Setup)
↓
Library
↓
Reader
↓
Bookmarks
↓
Playlists
↓
Settings
```

---

# Important Documents

Read when needed:

```text
docs/foundation.md
docs/architecture.md
docs/filesystem.md
docs/database.md
docs/navigation.md
docs/ui-spec.md
docs/reader-engine.md
docs/scanner-engine.md
docs/settings-spec.md
docs/coding-standards.md
docs/project-rules.md
docs/task-roadmap.md
docs/testing-strategy.md
docs/future-features.md
docs/release-plan.md
docs/branding.md
```

---

# Decision Hierarchy

Highest Priority:

```text
AI_AGENT_CONTEXT.md
↓
project-rules.md
↓
foundation.md
↓
architecture.md
↓
other docs
```

---

# Definition Of Success

Kitsune dianggap berhasil jika:

```text
Reader Stabil
Library Cepat
Memory Rendah
UI Konsisten
No Critical Crashes
```

---

# First Instruction For Any AI Agent

Before making any change:

1. Read AI_AGENT_CONTEXT.md.
2. Read project-rules.md.
3. Read relevant specification documents.
4. Verify change matches MVP scope.
5. Implement minimal focused changes.
6. Verify no existing functionality is broken.

This instruction is mandatory.
