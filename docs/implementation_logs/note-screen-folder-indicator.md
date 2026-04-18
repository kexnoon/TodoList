# NoteScreen Folder Indicator - Implementation Log

## Scope and sources
- Feature scope: Step 2 from `specs/folders.md` (NoteScreen folder indicator flow).
- Planning/task source: `tasks/folders/note-screen-folder-indicator.md`.
- Commit range reviewed: `1004fea24cbf0cc4e010dde9c4d740d78008bb8f..HEAD` (inclusive from the start commit).
- This log also captures implementation updates introduced during execution (based on task/spec updates and commit diffs).

## Chronological implementation timeline

### 1) 2026-04-16 21:41 (+02:00)
Commit: `1004fea24cbf0cc4e010dde9c4d740d78008bb8f`  
Message: `TD-89: Note Screen tasks defined`

- Created the Step 2 task file `tasks/folders/note-screen-folder-indicator.md`.
- Fixed TDD task ordering for Step 2 around:
  - contracts;
  - red tests for domain + existing NoteScreen edit use cases;
  - green domain;
  - red ViewModel tests;
  - green ViewModel;
  - UI implementation;
  - refactor/verification.
- Locked key Step 2 rules in tasks:
  - unassigning note folder to `null` still requires source folder timestamp update;
  - no-op folder selection should be filtered before use case invocation.

### 2) 2026-04-16 21:53 (+02:00)
Commit: `524ef2644cc5c519f2d91ffdb30a4e3ddbc3133c`  
Message: `TD-89: Contracts for folder change in Note screen`

Contracts and DI scaffolding:
- `SetNoteFolderUseCase` switched from stub without dependencies to contract with real dependencies:
  - `NoteRepository`, `FolderRepository`, `Clock`.
- `SetNoteFolderUseCase` contract fixed to `invoke(noteId: Long, targetFolderId: Long?)`.
- Result contract simplified to `SUCCESS` / `FAILURE` (without `NOTE_NOT_FOUND` and `NO_CHANGES`).
- `component-notes` DI updated to construct `SetNoteFolderUseCase(get(), get(), get())`.
- `feature-main` DI updated to pass folder use cases to NoteScreen flow:
  - `GetFoldersUseCase`, `CreateFolderUseCase`, `SetNoteFolderUseCase`.

NoteScreen contract scaffolding in ViewModel/state/events:
- Added ViewModel deps for folder flow.
- Added events for create-folder dialog open/dismiss.
- Extended NoteScreen state/app bar contracts to carry folder-related data.

Spec/task updates captured in same commit:
- `specs/folders.md`:
  - explicit DI setup in contracts step;
  - explicit rule that unassign to `No folder` updates source folder timestamp.
- `tasks/folders/note-screen-folder-indicator.md`:
  - removed `note not found` and `same folder selected` from `SetNoteFolderUseCase` responsibility;
  - documented no-op prevention at ViewModel/UI level.

### 3) 2026-04-16 23:02 (+02:00)
Commit: `de1bff243449966bacc7bdcaebe458cdb1f7a25a`  
Message: `TD-89: red tets for domain logic`

Red tests and constructor propagation for timestamp policy:
- Added new red test suite: `SetNoteFolderUseCaseTest`.
- Added/extended tests for NoteScreen-related edit use cases to enforce folder timestamp behavior:
  - `RenameNoteUseCaseTest`;
  - `SyncNoteStatusUseCaseTest`;
  - `CreateNewTaskUseCaseTest` (new file with full behavior, including `SUCCESS` path);
  - `RenameTaskUseCaseTest`;
  - `UpdateTaskUseCaseTest`;
  - `DeleteTaskUseCaseTest`.
- Propagated `FolderRepository` (and where needed `Clock`/`NoteRepository`) into use case constructors in DI and source files, preparing green step.
- Updated `DeleteTaskUseCase` contract to accept `noteId` (`invoke(noteId, task)`) so it can resolve note folder and apply timestamp policy.
- `NoteScreenViewModel` adjusted to call updated delete-task signature.

### 4) 2026-04-17 00:22 (+02:00)
Commit: `9b7fb7371e4311ea647b0fc37a2428e5ce357e0c`  
Message: `TD-89: green logic for domain layer`

Green implementation for domain logic:
- Implemented `SetNoteFolderUseCase`:
  - loads note by id;
  - updates note folder (`updateNotesFolder`);
  - computes affected folders (source/destination);
  - updates folder timestamps;
  - returns `SUCCESS`/`FAILURE` deterministically.

Batch timestamp update update introduced mid-implementation:
- Extended `FolderRepository` with overload:
  - `updateFolderTimestamp(folderIds: List<Long>, timestamp: String)`.
- Implemented overload in `FolderRepositoryImpl` with deduplication and fail-fast behavior.
- `SetNoteFolderUseCase` updated to use:
  - single-id overload when exactly one folder id is affected;
  - list overload when both source/destination folders are affected.

Green updates across NoteScreen edit use cases:
- Implemented folder timestamp updates after successful note/task edits in:
  - `RenameNoteUseCase`;
  - `SyncNoteStatusUseCase`;
  - `CreateNewTaskUseCase`;
  - `RenameTaskUseCase`;
  - `UpdateTaskStatusUseCase`;
  - `DeleteTaskUseCase`.
- All these flows skip folder timestamp update when note has no folder.

Test alignment:
- Updated existing tests for strict type-safe verification of overloaded methods.
- Updated `SetNoteFolderUseCaseTest` for batch overload path.
- `tasks/folders/note-screen-folder-indicator.md` updated with planning note about batch timestamp updates.

Process update captured in repo:
- `AGENTS.md` updated with stricter task sequencing: implement tasks one by one and wait for explicit user command before next task.

### 5) 2026-04-17 11:55 (+02:00)
Commit: `11de4e724457e6a3a1b07879a816a58741c9a3cb`  
Message: `TD-89: test for note screen ui logic`

Red tests for NoteScreenViewModel were expanded to full coverage baseline:
- Added shared base: `NoteScreenViewModelTestBase.kt`.
- Added focused test classes:
  - `NoteScreenViewModelLoadAndRecoveryTest`;
  - `NoteScreenViewModelNoteActionsTest`;
  - `NoteScreenViewModelTaskActionsTest`;
  - `NoteScreenViewModelUiEventsAndNavigationTest`;
  - `NoteScreenViewModelFolderFlowTest`.
- Coverage now includes both folder flows and all pre-existing NoteScreen behaviors (load/recovery, note actions, task actions, dialogs, sync, navigation, error paths).

Process/testing policy updates:
- `AGENTS.md` updated with explicit rule:
  - when test file for previously untested functionality is created, cover full behavior, even outside current feature scope.
- `tasks/folders/note-screen-folder-indicator.md` updated with matching planning update for full ViewModel coverage.

### 6) 2026-04-17 16:23 (+02:00)
Commit: `6d65c885f6fff5ec898800d40eb4c5cb6bdcf42c`  
Message: `TD-89: folders ui logic for NoteScreen`

Green implementation for NoteScreen ViewModel and folder model mapping:

ViewModel/state logic:
- `NoteScreenViewModel` now:
  - stores current selected folder model + available folders;
  - loads folders and selected folder on note load;
  - handles folder selection with no-op guard (`current == target` => skip use case call);
  - supports create-folder-and-assign flow;
  - updates state via dedicated private functions (`updateCurrentState`, folder update helper).
- `NoteScreenState` converted to explicit folder fields:
  - `currentFolder: CurrentFolderModel`;
  - `availableFolders: List<Folder>`.
- `NoteScreenAppBarModel` migrated from primitive folder label/id fields to `CurrentFolderModel`.

UI model/mapping updates introduced during implementation:
- `core-ui` `FilterChip` API migrated to model-based call:
  - added `FilterChipModel`;
  - all chip call sites adapted.
- Added `CurrentFolderModel` in NoteScreen models.
- Added `feature-main/utils/FolderMapper.kt`:
  - `Folder -> FilterChipModel`;
  - `Folder -> CurrentFolderModel`.
- Added `FolderMapperTest` to validate mapper behavior.

Test updates:
- Updated `NoteScreenViewModelFolderFlowTest` for the new state model (`currentFolder`, `availableFolders`).

### 7) 2026-04-18 14:51 (+02:00)
Commit: `f7f09403b94facdd6c6d77c1e7a98bbbcd16e414`  
Message: `TD-89: added folder indicator to Note screen`

UI implementation in NoteScreen:
- Added folder indicator under NoteScreen app bar.
- Reused `FilterChip` (via `FilterChipModel`) for indicator.
- Implemented menu with Material `DropdownMenu` (not separate custom component).
- Implemented dropdown order and actions:
  - `No folder` first;
  - existing folders;
  - `New folder` last.
- Wired actions to ViewModel:
  - folder selection/unselection;
  - create-folder dialog open;
  - create-folder confirm => create and assign.
- Added NoteScreen strings for folder indicator/dropdown/dialog.

Post-implementation correction reflected in final structure:
- Dropdown menu logic extracted into separate private composable `FolderIndicatorDropdownMenu`.
- This aligns with follow-up correction to avoid duplicated menu behavior in the indicator flow.

## Key implementation updates introduced during execution

1. `SetNoteFolderUseCase` result model was narrowed to `SUCCESS`/`FAILURE`.
- `NOTE_NOT_FOUND` and `NO_CHANGES` were intentionally removed.
- No-op case moved to ViewModel/UI guard (same folder selection must not call use case).

2. Unassigning note folder to `null` still updates source folder timestamp.
- Locked in spec/tasks and implemented in domain logic.

3. DI setup moved into contracts task (final shape, no temporary wiring).
- Explicitly implemented before red tests.

4. Batch timestamp update was introduced for move/unassign flow.
- Added overload `updateFolderTimestamp(List<Long>, timestamp)`.
- `SetNoteFolderUseCase` uses list overload for 2 folders and single-id overload for 1 folder.

5. ViewModel test strategy was expanded from folder-only to full ViewModel behavior.
- Enforced by AGENTS/testing rule and task planning update.
- Test suite split into multiple focused files for maintainability.

6. UI integration adapted to model-driven chips and folder models introduced during work.
- `FilterChipModel` adoption;
- `CurrentFolderModel` + mapper coverage.

7. Folder indicator menu implementation was corrected after behavioral issue.
- Final UI uses a single extracted dropdown function in NoteScreen flow.

## Final Step 2 outcome snapshot
- Domain contracts/DI for NoteScreen folder flow are in place.
- Domain use cases and timestamp policy for folder-related NoteScreen edits are implemented.
- Full baseline unit coverage for `NoteScreenViewModel` exists (not only folder scenarios).
- NoteScreen UI contains folder indicator + dropdown + create-folder dialog wiring.
- Task/spec planning updates made during implementation are reflected in tracked markdown files.

## Note on repository integration coverage
- In the reviewed commit range there is no dedicated `FolderRepositoryImpl` integration test file added for the new list-overload method.
- Coverage in this range is primarily unit-level (use cases + ViewModel + mapper), with overload behavior validated through use case tests and repository implementation changes.