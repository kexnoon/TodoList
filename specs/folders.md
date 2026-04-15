# Feature: Folders

## Goal
- Create folders for Notes.

## Business requirements
- Chip group in SortBar ordered by folder.lastUpdatedTimestamp;
    - always "All" first (shows every note), then folders, "New folder" last;
    - hide chip group during search.
    - Chip should contain an icon (optional) and a title
- Tapping a folder chip filters notes to that folder.
- "New folder" chip opens input dialog (title, trimmed name input, Dismiss/Confirm)
    - names cannot be empty or whitespace-only.
    - Confirm creates the folder and selects it.
- Long-press on a folder chip opens rename/delete dialog;
    - Deleting keeps notes, resets their folder to "No folder," and if the folder was selected, switches selection to "All."
- Adding a note uses the currently selected folder
    - when "All" is selected the note is created with no folder.
- Notes screen folder indicator under title/status with dropdown:
    - "No folder" first, existing folders, "New folder" (creates folder, assigns to note, updates indicator).
- Multi-select app bar has "Move to folder":
    - Opens a dialog with these options: "No folder", existing folders, "New folder" (creates folder, selects it, moves selected notes).

## Technical requirements
- Folder model:
    - id
    - name
    - lastUpdatedTimestamp
    - names need not be unique
    - trim before save.
- Database is SSOT:
    - folders table;
    - notes table has folderId FK.
    - Deleting a folder nulls folderId on its notes.
- Update folder.lastUpdatedTimestamp on folder create/rename, any note edit within it, and when moving notes into or out of it (both source and destination).
- After create/rename/move, resort chips (All first, New folder last).

# implementation-final

## Implementation steps
- Step 0 (already done in commit `f2dfcc0c758860d0230a8d35e860afe0617deab6`) - foundations:
    - Added folder data model in component layer: `Folder(id, name, lastUpdatedTimestamp)`.
    - Extended `Note`/`NoteEntity` with nullable `folderId`.
    - Added Room `FolderEntity` and `folders` table support in `AppDatabase` (DB version `10`).
    - Added FK `notes.folderId -> folders.id` with `ON DELETE SET NULL` and index on `folderId`.
    - Added `FolderDao` (`getAll`, `insert`, `renameById`, `updateFolderTimestampById`, `deleteById`).
    - Extended `NoteDao` with folder-related methods: `getNotesWithTasksInFolder`, `updateNoteTimestamp`, `updateNotesFolder`.
    - Added folder mappings in `Mappers.kt` and wired `folderId` mapping for notes.
    - Added `FolderRepository` + `FolderRepositoryImpl`.
    - Extended `NoteRepository` + `NoteRepositoryImpl` with folder-aware APIs.
    - Added use case shells for folders (`Create/Rename/Delete/GetFolders`) and note-folder actions (`MoveNotesToFolder`, `SetNoteFolder`); implemented `GetNotesInFolderUseCase`.
    - Registered new repositories/use cases in DI module.
    - Adjusted `GetNotesUseCase` signature and call site in `MainScreenViewModel`.
    - Non-feature infra change in same commit: `.gitignore` now ignores `AGENTS.md` instead of `agent_setup.md`.

- Step 1 — MainScreen Folder Flow (Happy Path + Search):
    - User opens notes by tapping folder chip.
    - User creates folder via `New Folder` chip.
    - User renames/deletes folder via long-press on folder chip.
    - Search behavior is included in this flow: during active query chip row is hidden and search is global.
    - Creating a new note from MainScreen uses currently selected folder (`All Notes` => `No folder`).

- Step 2 — NoteScreen Folder Indicator Flow:
    - Folder indicator is shown under title/status.
    - Dropdown options: `No folder` first, then existing folders, then `New Folder`.
    - `New Folder` from dropdown creates folder and assigns it to current note.
    - Folder selection/unselection updates current note folder immediately.

- Step 3 — Multi-select Move Flow (MainScreen):
    - In selection mode user opens `Move to folder`.
    - Dialog options: `No folder`, existing folders, `New Folder`.
    - If `New Folder` is chosen, it is created and selected as move target.
    - After successful move, selection mode is exited.

- Cross-flow rules (mandatory for Steps 1–3):
    - Chip order is always `All Notes` first, folders by `lastUpdatedTimestamp`, `New Folder` last.
    - Chip row is visible when search is inactive, even if notes list is empty.
    - Deleting selected folder switches current filter to `All Notes`.
    - `folder.lastUpdatedTimestamp` updates on folder create/rename and on note edits in folder, including task add/rename/status/delete.
    - On moving notes between folders, both source and destination folder timestamps are updated (when applicable).

## TDD policy for each step
- Contracts first.
- Unit tests (red).
- Implementation (green).
- Refactor if needed.

## Locked decisions
- During active search (`query` is not blank), results are always global (`All Notes`) and folder chip row is hidden.
- Chip row is visible when search is inactive even if notes list is empty.
- Chip row always includes:
    - `All Notes` chip first (without icon),
    - `New Folder` chip last (with add icon from `core-ui/theme/AppIcons`).
- After successful "Move to folder" from multi-select mode, selection mode is exited.
- "Any note edit" for folder timestamp update includes task edits (add/rename/status/delete task) as well.
- When search is inactive and a folder is selected, existing `SearchModel` sort/filters are still applied inside that folder.
- Creating a new note from MainScreen in a selected folder updates `folder.lastUpdatedTimestamp` for that folder.
