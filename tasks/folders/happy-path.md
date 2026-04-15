# Folder Happy Path tasks

## 1. Contracts and decision logging in spec
- Record in `specs/folders.md` (Locked decisions) that when creating a note from MainScreen in a selected folder, `folder.lastUpdatedTimestamp` is updated.
- Clarify/update Step 1 contracts:
  - folder use cases (`CreateFolderUseCase`, `RenameFolderUseCase`, `DeleteFolderUseCase`, `GetFoldersUseCase`) with explicit inputs and results (success/validation/failure);
  - `CreateNewNoteUseCase` with `folderId: Long?` support;
  - notes retrieval contract with folder filter + current `SearchModel` (sort/filters must be preserved inside a folder);
  - `MainScreenState`/`MainScreenUiEvents` for folder chip row and folder dialogs.

## 2. Unit tests (red): folder use cases
- Write tests for create/rename/delete/get folders.
- Cover:
  - name trimming;
  - reject empty/whitespace-only names;
  - correct repository calls;
  - timestamp behavior with fixed clock;
  - success/failure/validation results.
- For `GetFoldersUseCase`, verify correct ordering by `lastUpdatedTimestamp`.

## 3. Unit tests (red): MainScreenViewModel folder flow
- Initialization: load notes + folders, chip order `All` first, folders by timestamp, `New folder` last.
- Folder selection with empty query: request notes in selected folder while preserving sort/filters.
- Active search: global search, chip row hidden.
- Clearing search: return to previously selected folder.
- Creating folder via `New folder`: successful creation and auto-select of the new folder.
- Rename/delete via long-press: deleting selected folder switches filter to `All`.
- Creating note: pass current `selectedFolderId`; for `All` pass `null`.

## 4. Green: data/domain implementation
- Implement folder use cases via `FolderRepository` with name validation and timestamp logic.
- Add folder filter to notes retrieval path so it works together with `SearchModel`.
- Implement `CreateNewNoteUseCase(title, folderId)`:
  - create note with `folderId`;
  - update folder timestamp with the same `Clock` timestamp as a regular operation (if `folderId != null`), without special fallback handling.
- Update DI for new dependencies and signatures.

### Planning update: remove best-effort timestamp handling
- Decision: do not use a best-effort strategy for folder timestamp updates.
- Reasoning:
  - timestamp is generated from local `Clock`, so there is no external time dependency;
  - best-effort logic adds extra branching and testing without meaningful UX value.
- What will be done instead:
  - use a single timestamp value from `Clock` in note creation flow;
  - apply that timestamp directly for related folder update as a regular operation, without a special fallback policy.


## 5. Green: MainScreenViewModel
- Add selected folder state and chip row data.
- Implement loading branching:
  - `query != blank` -> global search;
  - `query == blank` -> filter by selected folder + current `SearchModel`.
- Add handlers for folder dialog flow: create/rename/delete/select.
- Integrate note creation with current selected folder.

### Planning update: extract folder chips mapper from ViewModel
- Decision: move folder chips building logic out of `MainScreenViewModel` into `feature_main/utils/Mappers.kt`.
- Reasoning:
  - `MainScreenViewModel` already has high responsibility and should stay focused on state orchestration;
  - folder chips mapping is a pure transformation and must be covered by focused unit tests.
- What will be done instead:
  - use `buildFolderChips(...)` from `feature_main/utils/Mappers.kt` inside ViewModel;
  - add dedicated mapper unit tests in `feature-main/src/test/.../utils/MappersTest.kt` for chip ordering and selection rules.



## 6. Green: MainScreen UI
- Add chip row in MainScreen:
  - visible when search is inactive (including empty notes list);
  - order: `All`, folders, `New folder`.
- Add dialogs:
  - `New folder` -> input dialog;
  - long-press folder chip -> rename/delete dialog.
- Bind UI callbacks to ViewModel events/handlers.

### Planning update: move All/New folders mapping to StateResult with string resources
- Decision:
  - `MainScreenViewModel` should expose only folders received from repository (without injecting synthetic `All Notes` and `New Folder` chips).
  - Mapping that adds `All Notes` and `New Folder` must be called only in `feature-main/src/main/java/de/telma/todolist/feature_main/main_screen/states/StateResult.kt`.
  - Mapper must receive `allNotesChipTitle` and `newFolderChipTitle` as `@StringRes Int` arguments.
- Reasoning:
  - titles must come from string resources so chip labels work correctly with translations;
  - avoid accessing `Context` in ViewModel to prevent lifecycle/memory-leak risks.
- What will be done instead:
  - keep ViewModel focused on state and raw folders data only;
  - resolve chip titles in `StateResult` via `stringResource(...)`;
  - call mapper from `StateResult` with resource IDs and render resulting chips.


## 7. Refactor + verify
- Minimal refactor without behavior changes.
- Run unit tests for `component-notes` and `feature-main` modules.
- Verify Step 1 acceptance:
  - chip visibility/order;
  - folder create/rename/delete flow;
  - new note folder assignment;
  - global search behavior;
  - timestamp update when creating a new note in a folder.
- Check all IDE warnings
