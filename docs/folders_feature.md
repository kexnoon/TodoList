# Folders Feature

## 1. What this feature is
Folders let users organize notes by topic or context and switch between grouped note lists quickly.
The feature is implemented across MainScreen, NoteScreen, and multi-select actions, with the database as the single source of truth.

## 2. What users can do

### MainScreen
- See folder chips in the sort bar: `All Notes`, folders, `New Folder`.
- Select a folder chip to filter notes by that folder.
- Create a folder from `New Folder`.
- Rename or delete a folder from folder actions.
- Create a new note directly in the currently selected folder.

### NoteScreen
- See the current folder indicator under title/status.
- Open folder dropdown with options in this order: `No folder`, existing folders, `New folder`.
- Assign/unassign the note folder.
- Create a new folder from NoteScreen and assign it immediately.

### Multi-select flow
- In selection mode, use `Move to folder`.
- Move selected notes to `No folder`, an existing folder, or a newly created folder.

## 3. Core behavior rules
- Folder chips order is always: `All Notes` first, folders sorted by `lastUpdatedTimestamp`, `New Folder` last.
- During active search, folder chips are hidden and search works globally across all notes.
- Deleting a folder does not delete notes: affected notes are moved to `No folder` (`folderId = null`).
- If a deleted folder was selected, current filter switches to `All Notes`.
- Folder names are trimmed before save; empty or whitespace-only names are rejected.
- Folder names are not required to be unique.
- `folder.lastUpdatedTimestamp` is updated on:
  - folder create/rename;
  - note edits inside that folder (including task add/rename/status/delete and note status sync path);
  - note moves into or out of the folder.

## 4. Implementation snapshot

### Data layer
- `folders` table added.
- `notes.folderId` is a nullable FK to `folders.id` with `ON DELETE SET NULL`.
- Folder model includes `id`, `name`, `lastUpdatedTimestamp`.

```kotlin
@Entity(tableName = "folders")
data class FolderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val lastUpdatedTimestamp: String
)

@Entity(
    tableName = "notes",
    foreignKeys = [ForeignKey(
        entity = FolderEntity::class,
        parentColumns = ["id"],
        childColumns = ["folderId"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index(value = ["folderId"])]
)
data class NoteEntity(
    val id: Long,
    val title: String,
    val status: String,
    val folderId: Long? = null,
    val createdTimestamp: String,
    val lastUpdatedTimestamp: String
)
```

### Domain layer
- Folder use cases: create, rename, delete, get folders.
- Note-folder use cases:
  - set single note folder (`SetNoteFolderUseCase`);
  - move selected notes (`MoveNotesToFolderUseCase`).
- Main notes retrieval supports folder filtering together with existing `SearchModel` sort/filter settings.

### UI layer
- MainScreen folder chips are integrated into `SortBar`.
- NoteScreen folder indicator is implemented with dropdown actions and create-folder dialog.
- Multi-select app bar includes `Move to folder` action and target selection dialog.

## 5. Testing and verification
- Unit tests cover folder use cases (validation, ordering, result paths).
- Unit tests cover `SetNoteFolderUseCase` and `MoveNotesToFolderUseCase` (success/failure and timestamp behavior).
- MainScreen ViewModel tests cover folder selection/search interplay, create/rename/delete, and move flow.
- NoteScreen ViewModel now has full baseline coverage including folder flow.
- Focused module test runs were executed for:
  - `:component-notes:testDebugUnitTest`
  - `:feature-main:testDebugUnitTest`

## 6. Scope limits and accepted decisions
- No UI tests were added in these steps (project testing strategy at this stage).
- Move dialog always shows all targets (`No folder`, existing folders, `New folder`), without source-folder exclusion.

## 7. Sources
- `specs/folders.md`
- `tasks/folders/happy-path.md`
- `docs/implementation_logs/folders-happy-path.md`
- `tasks/folders/note-screen-folder-indicator.md`
- `docs/implementation_logs/note-screen-folder-indicator.md`
- `tasks/folders/multi-select-move.md`
- `docs/implementation_logs/multi-select-move-flow.md`
