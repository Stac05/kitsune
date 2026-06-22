# Kitsune Documentation Index

Welcome to the official documentation for **Kitsune**, an offline-first manga and comic reader for Android. This repository contains the source code and technical specifications for the project.

## 1. Project Overview
Kitsune is a high-performance, reader-focused application designed to manage and read local comic libraries.
- **Goal:** To provide a seamless, stable, and offline reading experience for manga and comic enthusiasts.
- **Core Philosophy:**
    - **Offline First:** No internet connection required. All media is stored locally.
    - **Filesystem First:** The application treats the user's storage as the single source of truth, accessed via Android's Storage Access Framework (SAF).
    - **Performance Oriented:** Optimized I/O operations and memory management for handling large libraries.

## 2. Current Status
- **Current Version:** v1.0.0
- **Status:** Stable
- **Next Milestone:** Phase 7 - Video Support (Media3 integration and Video Library).

## 3. Core Features
- **Library Scanner:** Incremental scanning that only updates modified folders.
- **Manga Reader:** Supports Vertical (webtoon style), Left-to-Right (LTR), and Right-to-Left (RTL/Manga) reading modes.
- **Reading Progress:** Automatically saves and resumes from the last read page and chapter.
- **Bookmark System:** Organize comics into custom categories with bulk management support.
- **Playlist System:** Create custom reading orders for specific series or collections.
- **Search:** Real-time title filtering with visual feedback.
- **Continue Reading:** Quick access to the last read title via the home screen.

## 4. Architecture Overview
Kitsune is built with modern Android development practices:
- **MVVM:** Clear separation of concerns between UI, Logic, and Data.
- **Jetpack Compose:** Fully declarative UI built with Material 3.
- **Room Database:** Used for persisting application metadata, settings, and progress.
- **Hybrid SAF:** Uses `DocumentFile` for directory navigation and `ContentResolver` for high-performance file streaming.
- **Relative Path Identifier:** Uses paths relative to the root folder (e.g., `Comics/One Piece`) as primary IDs to ensure library portability.
- **Natural Sorting:** Alphanumeric sorting (e.g., Chapter 2 comes before Chapter 10) applied across all lists.

## 5. Project Structure
- `com.kitsune.app.core`: Core utilities like `StorageHelper` and `NaturalOrderComparator`.
- `com.kitsune.app.data`: Repositories that orchestrate data flow between SAF and Room.
- `com.kitsune.app.database`: Room database definition, entities, and DAOs.
- `com.kitsune.app.domain`: Business models (Comic, Chapter, Page).
- `com.kitsune.app.navigation`: Navigation graph and URL-encoded route management.
- `com.kitsune.app.reader`: CBZ parsing engine and Coil image fetcher implementation.
- `com.kitsune.app.scanner`: Library scanning engine and incremental logic.
- `com.kitsune.app.ui`: Compose-based screens and ViewModels.

## 6. Storage Structure
Users select a root folder (e.g., `/Kitsune`) which the app initializes with the following structure:
```text
/Kitsune
├── Comics/      # Comic folders containing CBZ files
├── Videos/      # Placeholder for future video support
├── Backup/      # Target for future database exports
├── Cache/       # Temporary data storage
└── .nomedia     # Prevents media from appearing in system gallery
```
- **Comic Rules:** A folder inside `/Comics` represents a title.
- **Cover:** A file named `cover.[jpg|jpeg|png|webp]` inside the title folder.
- **Chapters:** Files must be in `.cbz` format (ZIP).

## 7. Database Overview
The Room database (`kitsune.db`) stores:
- **Settings:** Global app configuration and the Root Folder URI.
- **Comic Cache:** Metadata of scanned titles to speed up library browsing.
- **Reading Progress:** Last read chapter and page for each comic.
- **Collections:** User-defined Bookmark and Playlist categories.
**Primary Key:** Most relations use the `comicRelativePath` to remain valid even if the Root Folder is moved.

## 8. Reader Engine
The reader extracts images directly from `.cbz` files using `ZipInputStream`.
- **Metadata Cache:** Page lists are cached in memory (`pageCache`) to avoid re-parsing ZIP files during navigation.
- **Stability:** Includes `SecurityException` handling if SAF permissions are revoked at runtime.
- **Known Limitation:** `ZipInputStream` requires linear iteration; jumping to the very end of extremely large CBZ files (>500 pages) may have a slight I/O delay.

## 9. Scanner Engine
- **Incremental Scan:** Uses the filesystem's `lastModified` attribute to skip unchanged folders.
- **Folder Cache:** Caches `DocumentFile` references to avoid expensive SAF tree traversals.
- **Room Write Optimization:** Only updates the database if actual changes (new folders, renamed covers) are detected.
- **Wipe-out Protection:** Validates the category folder's existence before performing deletions to prevent library loss on storage errors.

## 10. Technology Stack
- **Kotlin**: 100%
- **Jetpack Compose**: UI Framework
- **Room**: Persistence
- **Coil**: Image Loading (Custom CBZ Fetcher)
- **Navigation Compose**: App Navigation
- **KSP**: Annotation Processing

## 11. Known Limitations
- **Manual Rename:** Renaming folders via external file managers will break the link to reading progress (treated as a new comic).
- **Linear ZIP:** Linear access pattern for CBZ content as mentioned in the Reader section.

## 12. Future Roadmap
- **Phase 7 - Video Support:** Integration with Media3 for local video playback.
- **Phase 8 - Backup & Restore:** Manual and automatic metadata export/import.
- **Phase 9 - Metadata Support:** Support for external metadata files (`info.json`).

## 13. Development Rules
1. **Relative Path First:** Always use relative paths from the root as the primary identifier in the database.
2. **Natural Sorting Mandatory:** Every list (Chapters, Pages, Titles) must use `NaturalOrderComparator`.
3. **Filesystem is Truth:** Do not store chapter or page lists in the database; read them from the filesystem lazily.
4. **SAF Stewardship:** Always handle URI persistence and `SecurityException` gracefully.

## 14. Documentation Index
- [AI_AGENT_CONTEXT.md](AI_AGENT_CONTEXT.md): Main entry point for AI agents. Core rules and MVP scope.
- [PROJECT_STATUS.md](PROJECT_STATUS.md): Current development state and optimization history.
- [architecture.md](architecture.md): Technical architecture and module responsibilities.
- [filesystem.md](filesystem.md): Specification for folder and file structures.
- [database.md](database.md): Room database schema and entity relations.
- [navigation.md](navigation.md): Navigation routes and URL encoding strategy.
- [ui-spec.md](ui-spec.md): Design principles, icons, and UI component behavior.
- [reader-engine.md](reader-engine.md): Detailed logic of the CBZ parsing and reading flow.
- [scanner-engine.md](scanner-engine.md): Incremental scanning and storage optimization logic.
- [project-rules.md](project-rules.md): Absolute project rules and coding standards.
- [task-roadmap.md](task-roadmap.md): Historical progress and upcoming milestones.
- [branding.md](branding.md): Official assets and visual identity rules.
