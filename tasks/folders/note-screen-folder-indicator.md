                                              # NoteScreen Folder Indicator tasks

## 1. Contracts and decision logging in spec
- Record Step 2 contracts in `specs/folders.md` for NoteScreen folder indicator flow.
- Define `SetNoteFolderUseCase(noteId: Long, targetFolderId: Long?)` contract:
    - assign note to folder;
    - unassign note to `No folder` (`folderId = null`) with source folder timestamp update;
    - move between folders with source/destination timestamp updates;
    - return explicit result states for success/failure.
- Define NoteScreen contracts for state/events:
    - folder indicator data under title/status;
    - dropdown actions (`No folder`, existing folders, `New folder`);
    - create-folder dialog flow from NoteScreen.
- Define DI setup in contracts step:
    - specify required bindings/constructor dependencies for Step 2 (`SetNoteFolderUseCase`, `GetFoldersUseCase`, `CreateFolderUseCase` in NoteScreen flow);
    - no temporary constructor wiring; final DI shape must be captured before red tests.
- Lock Step 2 cross-flow rule: note edits on NoteScreen update folder timestamp when note has a folder.

## 2. Unit tests (red): domain logic and existing NoteScreen edit use cases
### `SetNoteFolderUseCase`
- Add unit tests for:
    - assign from `No folder` to folder;
    - unassign folder -> `No folder` and source folder timestamp is updated;
    - move folder A -> B (both timestamps updated);
    - repository update failure;
    - folder timestamp update failure.
- Verify timestamp generation with fixed `Clock`.

### NoteScreen-related edit use cases timestamp behavior
- Extend existing use-case tests to verify folder timestamp updates for NoteScreen edits:
    - `RenameNoteUseCase`;
    - `CreateNewTaskUseCase`;
    - `RenameTaskUseCase`;
    - `UpdateTaskStatusUseCase`;
    - delete task flow used by NoteScreen;
    - note status sync path where note status changes.
- Cover both cases:
    - note has `folderId` -> folder timestamp update is required;
    - note has no folder -> no folder timestamp update call.

## 3. Green: data/domain implementation
- Implement `SetNoteFolderUseCase` (replace stub with real logic).
- Use existing repository APIs (`getNoteById`, `updateNotesFolder`, `updateFolderTimestamp`) for single-note folder assignment.
- Update DI registrations in `component-notes` so `SetNoteFolderUseCase` has required dependencies.
- Apply folder timestamp update logic across NoteScreen edit use cases per Step 2 rules.

### Planning update: batch folder timestamp update for folder move flow
- Decision:
    - overload `FolderRepository.updateFolderTimestamp` with `updateFolderTimestamp(folderIds: List<Long>, timestamp: String)`.
    - use this overload in `SetNoteFolderUseCase` instead of per-folder loop calls.
- Reasoning:
    - folder move/unassign flow already computes all affected folders before update;
    - one repository call for the full set keeps use-case flow simpler and deterministic.
- What will be done instead:
    - keep existing single-id overload for other use cases;
    - call the new list overload from `SetNoteFolderUseCase` with deduplicated source/destination folder ids.

## 4. Unit tests (red): `NoteScreenViewModel` folder flow
- Add dedicated ViewModel tests for:
    - initial load includes folder indicator state;
    - dropdown ordering: `No folder`, folders, `New folder`;
    - selecting `No folder` unassigns current note folder;
    - selecting existing folder assigns current note folder;
    - selecting `New folder` opens dialog and on success creates+assigns folder;
    - invalid folder name handling for create flow;
    - assignment/create failures mapped to UI error state/events.

## 5. Green: `NoteScreenViewModel`
- Inject and wire folder use cases:
    - `GetFoldersUseCase`;
    - `CreateFolderUseCase`;
    - `SetNoteFolderUseCase`.
- Extend NoteScreen state/events to support:
    - current folder indicator;
    - dropdown visibility/selection handling;
    - new-folder dialog open/confirm/dismiss.
- Keep existing note/task flows intact outside folder-related behavior.

### Planning update: prevent no-op `SetNoteFolderUseCase` calls
- Decision:
    - when user selects the same folder as currently assigned, `SetNoteFolderUseCase` must not be called.
- Reasoning:
    - same-folder selection is a pure no-op in UX flow and should be filtered in ViewModel/UI layer.
- What will be done instead:
    - compare target folder with current note folder before invoking folder-change flow;
    - skip use-case call when IDs are equal (including `null == null`).

## 6. Green: `NoteScreen` UI
- Render folder indicator under title/status with dropdown actions.
- Reuse `FilterChip` from `core-ui/src/main/java/de/telma/todolist/core_ui/composables/Chips.kt` for the folder indicator.
- Implement dropdown options order:
    - `No folder` first;
    - existing folders;
    - `New folder` last.
- Implement indicator actions menu with Material `DropdownMenu` directly in NoteScreen UI flow (no separate menu component extraction).
- Implement `New folder` dialog from NoteScreen and bind to ViewModel.
- Add required string resources for new folder indicator/dropdown/dialog texts.

## 7. Refactor + verify
- Minimal refactor only after tests pass.
- Run focused unit tests:
    - `component-notes` module use cases;
    - `feature-main` NoteScreen ViewModel tests.
- Verify Step 2 acceptance:
    - folder indicator appears under title/status;
    - folder assignment/unassignment works immediately;
    - `New folder` from dropdown creates folder and assigns it to the note;
    - folder timestamps update for NoteScreen-driven note edits.
- No UI tests in this step (per project testing strategy).
