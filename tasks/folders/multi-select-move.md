# Multi-select Move Flow tasks

## Task 3.1 - Contracts for Step 3 (MainScreen + Domain)
- Lock the Step 3 contracts:
  - `MoveNotesToFolderUseCase(selectedNotes: List<Note>, targetFolderId: Long?): Result`
  - `Result = SUCCESS | FAILURE`
  - source folders are derived from `selectedNotes`.
- Lock UI flow contract for `Move to folder` and `New folder`.

## Task 3.2 - MainScreen contracts for state/events/errors
- Define state/events/errors for:
  - show/dismiss move dialog;
  - show/dismiss create-folder-from-move dialog;
  - move failure handling.
- Lock the single-source exclusion rule:
  - if selected notes have exactly one source folder, exclude that folder from move targets.

## Task 3.3 - Unit tests (red) for `MoveNotesToFolderUseCase`
- Add tests for:
  - move to existing folder;
  - move to `No folder`;
  - mixed-source move with timestamp update for all affected folders;
  - failure on notes-folder update;
  - failure on folder timestamp update.

## Task 3.4 - Green implementation of `MoveNotesToFolderUseCase`
- Replace current stub with real move logic.
- Use existing repository APIs only:
  - `NoteRepository.updateNotesFolder(...)`
  - `FolderRepository.updateFolderTimestamp(...)`
- Update affected folder timestamps based on:
  - `selectedNotes.mapNotNull { it.folderId }.distinct() + targetFolderId` (dedupe, ignore null).

## Task 3.5 - Unit tests (red) for `MainScreenViewModel` Step 3 flow
- Add tests for:
  - open/close move dialog;
  - move to `No folder`;
  - move to existing folder;
  - `New folder` success path (`create -> move -> exit selection mode`);
  - `New folder` invalid name/create failure path (selection mode stays active);
  - generic move failure path (selection mode stays active);
  - single-source exclusion behavior in move-target options.

## Task 3.6 - Green implementation in `MainScreenViewModel`
- Inject and wire `MoveNotesToFolderUseCase`.
- Implement move flow handlers, including `New folder -> create -> move`.
- Exit selection mode only on `SUCCESS`; keep selection mode on `FAILURE`.

## Task 3.7 - MainScreen UI changes (app bar + dialogs + strings)
- Add `Move to folder` action to selection app bar.
- Add move-target dialog with options in order:
  - `No folder`, existing folders, `New folder`.
- Apply single-source exclusion when building target options.
- Reuse existing `InputDialog` for `New folder` in move flow.
- Add required string resources for action/dialog/error labels.

## Task 3.8 - Verification for Step 3
- Run targeted unit tests for `component-notes` and `feature-main`.
- Verify Step 3 acceptance criteria:
  - move flow works from multi-select;
  - `New folder` path works;
  - selection mode exits only after successful move.
