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

### Planning update: use dropdown menu for folder chip actions
- Decision:
  - after long-click on a folder chip, show a dropdown menu with actions `Rename` and `Delete` instead of a two-button dialog;
  - implement this menu as a separate private composable inside `feature-main/src/main/java/de/telma/todolist/feature_main/main_screen/MainScreen.kt`;
  - each action item must include a corresponding icon from `AppIcons`.
- Reasoning:
  - better UX.
- What will be done instead:
  - render a contextual dropdown menu on long-click action event;
  - keep existing rename/delete flows, but trigger them from dropdown menu actions;
  - use outlined Material icons via `AppIcons` for both menu items.

### Planning update: remove folder chips mapper and hardcode synthetic chips in FolderChipRow
- Decision:
  - remove folder chips mapper from feature-main flow;
  - hardcode `All Notes` and `New Folder` chips directly inside `feature-main/src/main/java/de/telma/todolist/feature_main/composables/FolderChipRow.kt`;
  - add dedicated callback `onNewFolderPressed` to avoid callback multiplexing through synthetic chip model flags.
- Reasoning:
  - issues with passing callback to `New Folder` chip.
- What will be done instead:
  - `FolderChipRow` will receive raw `folders` and `selectedFolderId`;
  - `All Notes` chip will select `null` folder, `New Folder` chip will call `onNewFolderPressed`;
  - remove `FolderChipUiModel`, `Mappers.kt`, and mapper tests.


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
- Do code review against the base branch (`feature/folders`)
