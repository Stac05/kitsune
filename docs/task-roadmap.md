# Kitsune - Task Roadmap (v1.0 Stable Candidate)

## Purpose

Dokumen ini mendefinisikan urutan pengerjaan resmi Kitsune berdasarkan arsitektur Hybrid SAF dan Relative Path.

---

# Completed Phases

## Phase 0: Project Setup [DONE]
- Setup Min SDK 26, Target SDK 35.
- Package structure: core, data, domain, ui, reader, scanner.

## Phase 1: Core Infrastructure [DONE]
- Storage Helper: SAF Picker, URI Persistence.
- Settings System: `SettingsEntity` & `SettingsRepository`.
- Initialization: Auto-create `/Kitsune` subfolders.
- Navigation: Navigation Compose with URL Encoding support.
- Splash Screen: Permission check flow.

## Phase 2: Library System [DONE]
- Scanner Engine: `DocumentFile` scanning with Relative Path mapping.
- Natural Sorting: `NaturalOrderComparator` implementation.
- Incremental Scan: `lastModified` check logic.
- UI: Comic Library Grid with cover detection.

## Phase 3: Reader Engine [DONE]
- Comic Detail: Chapter list with Natural Sorting.
- CBZ Parser: Streaming image via `ZipInputStream`.
- Reader UI: Vertical, LTR, RTL modes.
- Progress System: Save progress via `ReadingProgressRepository`.
- Auto Transition: Navigate chapters using Natural Sorting.

## Phase 4: Bookmark & Playlist [DONE]
- Data Layer: Room entities with `comicRelativePath` foreign keys.
- UI: CRUD Bookmark & Playlist with selection mode.

## Phase 5: Settings & Polish [DONE]
- Reading Settings: Mode, Grid Size, Theme (OLED).
- Appearance: Dark Mode support.
- UX: Consisten naming "Settings" and descriptive icons.

## Phase 6: Optimization & Stability [DONE]
- **6.2A Room Write Optimization:** Filter redundant database writes.
- **6.2B1 Folder Cache:** Memory cache for frequently accessed `DocumentFile`.
- **6.3B1 Reader Metadata Cache:** `pageCache` for CBZ page lists.
- **6.5.1 Stability Fixes:** 
    - Library Wipe-out Protection.
    - `SecurityException` handling in Reader.
- **6.5.3A Cleanup:** Dead code & Placeholder removal.

---

# Current Milestone: Release v1.0 Stable

## Tasks
- [ ] R8/ProGuard configuration (minifyEnabled).
- [ ] Final APK size validation.
- [ ] Documentation finalization.

---

# Future Roadmap

## Phase 7: Video Support
- Video Scanner: Detection of video files and posters.
- Video Library UI: Grid for video content.
- Video Player: Media3 integration.
- Video Progress: Resume playback support.

## Phase 8: Backup & Restore
- Database Export: Export Room data to `/Backup` folder.
- Database Import: Restore metadata from backup file.

## Phase 9: Metadata & Polish
- External Metadata: Support for `info.json` or `comic.json`.
- UI Enhancements: Advanced animations and custom accent colors.
