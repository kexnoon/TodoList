# Folders Happy Path Implementation Log

## Scope
- Feature: `Folders`
- Scope: Step 1 (`MainScreen Folder Flow (Happy Path + Search)`) from `specs/folders.md`
- Branch: `task/TD-88-folders-happy-path`
- Commit range reviewed: `9d30551d31e46a0da11706535e20c9332095497c..HEAD` (inclusive of `9d30551`)
- Main sources:
  - `specs/folders.md`
  - `tasks/folders/happy-path.md`
  - conversation history and accepted in-flight planning updates

## Commit Timeline (from `9d30551`)
1. `9d30551` - **TD-88: split folders step 1 spec into tasks**
- Split Step 1 into a dedicated task plan file: `tasks/folders/happy-path.md`.
- Synced spec/task planning structure for TDD flow.

2. `1154289` - **TD-88: contracts for happy path**
- Updated contracts for:
  - folder use cases (`CreateFolderUseCase`, `RenameFolderUseCase`, `DeleteFolderUseCase`, `GetFoldersUseCase`);
  - note retrieval with folder filter + `SearchModel`;
  - `CreateNewNoteUseCase(title, folderId)`;
  - MainScreen state/events for folder chips and dialogs.
- Added/updated locked decision in `specs/folders.md`:
  - creating a note in selected folder updates `folder.lastUpdatedTimestamp`.

3. `87807c5` - **TD-88: use cases tests (red)**
- Added red tests for folder use cases:
  - create/rename/delete/get;
  - trim + blank validation;
  - timestamp/repository interaction checks.
- Started DI migration for folder use cases (constructor injection).

4. `b43d000` - **TD-88: Folder-related tests for MainScreenViewModelTest.kt**
- Added MainScreenViewModel tests for folder flow:
  - folder-aware note loading;
  - global search behavior;
  - restore selected folder after clearing search;
  - new note folderId propagation.

5. `8a10d78` - **TD-88: green implementation for data and domain**
- Implemented green for folder use cases + `CreateNewNoteUseCase` folder-aware behavior.
- Completed DI wiring for new dependencies (`FolderRepository`, `Clock`).
- Updated `CreateNewNoteUseCaseTest` for new flow.

6. `e01afaa` - **TD-88: UI logic implementation**
- Implemented MainScreen UI logic in ViewModel:
  - selected folder state;
  - folder events/handlers (create/rename/delete/select);
  - search branching (global when query active).
- Added first mapper-based approach for folder chips (`feature_main/utils/Mappers.kt`) and mapper tests.

7. `596f8fd` - **TD-88: a small update to happy-path.md**
- Updated task documentation according to planning changes.

8. `bbf5fe5` - **TD-88: folders happy path ui**
- Implemented MainScreen UI and composables:
  - `FolderChipRow` + integration into `SortBar`/`StateResult`/`MainScreen`;
  - folder dialogs and folder actions flow;
  - `Chips.kt` in core-ui and icon updates in `AppIcons`;
  - updated strings and ViewModel tests.
- Removed mapper and mapper tests in final UI iteration.

9. `cd7c3d8` - **TD-88: some additional refactoring**
- Restored `SqlHelper` validation for mutually exclusive created/updated ranges.
- Removed obsolete folder-actions event path from `MainScreenViewModel`.

## Task-by-Task Implementation Log

### Task 1 - Contracts and spec alignment
- Step 1 contracts were explicitly aligned with real implementation points:
  - `GetNotesUseCase(search, folderId)` and repository-level folder filtering.
  - Folder use case result models now represent success/validation/failure where needed.
  - MainScreen folder state/events were formalized for chip row and folder dialogs.
- `specs/folders.md` locked decision updated with folder timestamp behavior on note creation in selected folder.

### Task 2 - Red tests for folder use cases
- Added isolated unit tests for:
  - `CreateFolderUseCase`;
  - `RenameFolderUseCase`;
  - `DeleteFolderUseCase`;
  - `GetFoldersUseCase`.
- Coverage includes:
  - `trim()` and blank-input rejection;
  - result branches;
  - repository calls;
  - sort behavior by `lastUpdatedTimestamp`.
- Additional case added later:
  - deterministic ordering when timestamps are equal.

### Task 3 - Red tests for MainScreenViewModel folder flow
- Added tests for:
  - loading folders and notes;
  - folder-based note retrieval when search is inactive;
  - global search when query is active + hidden chip row;
  - restoring selected folder scope after clearing query;
  - propagation of selected `folderId` into `CreateNewNoteUseCase`.

### Task 4 - Data/domain green implementation
- Implemented folder use cases using repository + `Clock` DI.
- Implemented note retrieval with folder filter while preserving active `SearchModel`.
- Implemented folder-aware note creation:
  - note persists with `folderId`;
  - folder timestamp update uses the same `Clock` timestamp.

### Task 5 - MainScreenViewModel green implementation
- Added selected-folder state to `MainScreenState`.
- Implemented branching:
  - query active -> global scope;
  - query inactive -> selected folder scope + current sort/filter model.
- Added event handlers for folder create/rename/delete/select flow.

### Task 6 - MainScreen UI green implementation
- Implemented folder chips row in MainScreen UI flow:
  - visible when search inactive, including empty notes state;
  - order: `All Notes`, folders, `New Folder`.
- Added folder dialogs and long-press actions flow.
- Integrated folder callbacks end-to-end from composables to ViewModel.

### Task 7 - Refactor + verify
- Refactoring performed without changing intended behavior:
  - removed obsolete event path in ViewModel;
  - restored SQL validation contract.
- Verification executed:
  - `:component-notes:testDebugUnitTest`;
  - `:feature-main:testDebugUnitTest`;
  - compile checks for both modules.
- Code review against `feature/folders` completed.

## Planning Updates Applied During Implementation

### 1) Remove best-effort timestamp handling (moved to Task 4)
- Initial idea discussed: best-effort timestamp update.
- Final decision: do not use best-effort behavior.
- Reasoning:
  - local `Clock` time source is deterministic in this context;
  - best-effort added extra branching/tests without UX value.
- Applied result:
  - regular single-path timestamp assignment via `Clock`.

### 2) Mapper extraction from ViewModel (intermediate)
- Intermediate decision: move chip-building logic from ViewModel into mapper for separation and testability.
- Implemented temporarily with dedicated mapper test.

### 3) Move synthetic chips mapping to UI layer with resources (intermediate)
- Intermediate decision: keep ViewModel free of synthetic chips and context access.
- Mapping of `All Notes`/`New Folder` moved toward UI layer with string resources.

### 4) Replace folder action dialog with dropdown (UX update)
- Decision applied: long-press folder chip opens contextual dropdown with `Rename`/`Delete` and icons.

### 5) Final decision: remove mapper entirely for folder chips
- Final approach replaced intermediate mapper plans:
  - synthetic chips are hardcoded in `FolderChipRow`;
  - direct callbacks are used (`onNewFolderPressed`, folder select/rename/delete).
- Reasoning:
  - callback routing for synthetic `New Folder` chip was cleaner without mapper indirection.

## Additional In-Flow UI/Interaction Fixes (from implementation cycle)
- `New Folder` click path was fixed after identifying click-handler conflict.
- `FilterChip` was converted to a custom composable in `core-ui` with:
  - reliable click + long-click handling;
  - optional icon support;
  - dedicated previews.
- Added outlined folder icon into `AppIcons`.
- `FolderChipRow` made part of `SortBar` in one horizontal row with sort actions.
- Folder actions dropdown anchored to the chip level and dismisses on outside click.

## Final Technical Snapshot (Happy Path)

### Data/domain
- Folder use cases are DI-driven and validation-aware.
- Notes retrieval supports folder filter + current search/sort/filter model.
- New note creation receives current selected folder and performs timestamp update flow.

### ViewModel/UI logic
- MainScreenViewModel owns selected folder state and search-aware visibility/scope logic.
- Folder create/rename/delete/select handlers are integrated with UI events/dialogs.

### UI
- `SortBar` hosts both folder chips and sorting controls.
- `FolderChipRow` owns synthetic chips (`All Notes`, `New Folder`) and folder action dropdown.
- Core reusable `FilterChip` is implemented in design system (`core-ui`).

### Tests
- Folder use case tests (red/green cycle completed).
- MainScreenViewModel tests extended for folder happy path behavior.
- Existing tests updated for new signatures/contracts.

## Review Outcome and Accepted Decisions
- Two review findings were discussed and explicitly accepted as non-blocking for current business logic:
  - note creation result semantics when folder timestamp update fails;
  - `getNotesInFolder(null)` behavior divergence from legacy DAO semantics.
- Decision for this scope: keep implementation as-is.

## Status
- Happy Path (Step 1) implementation is completed on this branch with iterative planning updates incorporated.

