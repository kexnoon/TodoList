# Feature: ViewModel Delegates Refactor

## Objective
- Split oversized screen ViewModels into focused delegates to improve maintainability while preserving current behavior.
- Reduce broad screen state updates by introducing focused internal state slices while keeping existing UI-facing contracts stable during the refactor.

## Context
- `MainScreenViewModel` and `NoteScreenViewModel` currently combine state orchestration, data operations, dialog events, and navigation.
- This coupling makes incremental changes risky and increases testing effort per change.
- Current screen state models are broad enough that unrelated updates can cause avoidable recomposition.
- Existing `UiState`, `UiEvents`, and `UiErrors` contracts are consumed by the UI layer and must remain compatible during this refactor stage.
- For this feature you can ignore TDD since it's more about refactoring than adding new functionality.

## Business rules
- User-visible behavior must remain unchanged for both screens.
- Existing screen contracts (`UiState`, `UiEvents`, `UiErrors`) must remain functionally equivalent.
- Existing user flows for notes, tasks, folders, search, selection mode, move flow, and navigation must keep current behavior.
- ViewModel public APIs currently used by UI call sites must remain stable during this refactor stage.

## Functional requirements
- Introduce delegate boundaries for each screen and keep ViewModel as orchestrator.
- Keep existing public ViewModel APIs used by UI layer during this refactor stage.
- Main screen must be split into delegate responsibilities:
  - Notes flow (load/search/filter/selection/create/delete/move operations).
  - Folders flow (observe folders/select/create/rename/delete).
  - Dialog/event dispatch flow (show/dismiss event routing).
  - Navigation flow (screen transitions).
- Note screen must be split into delegate responsibilities:
  - Note content flow (observe note and build state).
  - Task flow (add/rename/delete/status change).
  - Folder assignment flow (select/create-and-assign/update folder state).
  - Dialog/event dispatch flow (show/dismiss event routing).
  - Navigation flow (back and post-delete behavior).
- Delegate boundaries must be explicit via contracts/interfaces between ViewModel and delegates.
- Delegates must not receive `ViewModel` instance directly; communication must use mutation-based contracts.
- Split oversized screen state inside ViewModels into focused internal state slices:
  - MainScreen: notes, folders, search, dialogs, and non-sticky effects.
  - NoteScreen: note content, tasks, folder assignment, dialogs, and non-sticky effects.
- Keep `UiState.Loading` and `UiState.Error` as root screen states for initial loading and fatal screen errors only.
- Build `UiState.Result` inside each ViewModel by combining internal state slices into the existing legacy screen state contract.
- Keep dialogs and one-off effects out of `UiState.Result` aggregation; expose them as dedicated ViewModel-owned flows for UI control and transient actions.
- Use non-sticky event delivery semantics for one-off effects to avoid replaying stale actions after recomposition or configuration changes.
- Add separate delegate documentation describing:
  - rationale for ViewModel splitting,
  - delegate ownership boundaries,
  - mutation contract rules,
  - DI/lifecycle rules,
  - testing approach for delegates.

## Non-functional requirements
- No new dependencies.
- No behavior changes outside the refactor scope.
- Preserve existing performance characteristics.
- Prefer deterministic, testable delegate logic with isolated responsibilities.
- Keep code readable and avoid overengineering.
- Keep docs/spec/task text in English.

## Acceptance criteria
- `MainScreenViewModel` and `NoteScreenViewModel` are reduced to orchestration-focused classes.
- Business logic previously inside those ViewModels is moved into delegate classes with clear ownership.
- Public APIs currently consumed by UI compile and behave the same without UI call-site changes.
- `UiState.Result` is built inside ViewModels from internal state slices.
- `UiState.Loading` and `UiState.Error` are used only as root states for initial loading and fatal screen errors.
- Dialogs and one-off effects are exposed through dedicated ViewModel-owned flows and are not part of `UiState.Result` aggregation.
- One-off effects use non-sticky delivery semantics and do not replay stale actions after recomposition or configuration changes.
- Existing tests continue to pass; new/updated tests cover delegate behavior boundaries and critical flows.
- A dedicated delegates document is added in project docs and linked from `README.md`.
- No regressions in:
  - MainScreen search behavior and folder chip visibility.
  - MainScreen selection mode and move-to-folder flow.
  - Folder create/rename/delete flows from MainScreen.
  - NoteScreen task operations and note status sync behavior.
  - NoteScreen folder assignment and create-folder-and-assign flow.

## Edge cases
- Selected folder removed while it is active.
- Search active state toggles while folder filter exists.
- Move operation triggered with empty selected notes set.
- Note/task/folder not found branches.
- Folder creation failures in move flow and note flow.
- Sync failure after task or note updates.
- Recomposition or configuration change after a one-off effect is emitted.
- Existing UI call sites still consuming legacy screen state during the migration.

## Out of scope
- New user features or UX changes.
- Navigation architecture redesign.
- Migration away from current `BaseViewModel` hierarchy.
- Reworking domain/data use case contracts unless required by refactor safety.
- Broad architectural migration unrelated to delegate split.
- Project module cleanup, launcher visibility fixes, and unrelated P1/P2 cleanup; these are covered by `specs/final-cleanup.md`.

# implementation-final

## Implementation steps
- Step 1 - Delegate boundaries and contracts:
  - Confirm and lock delegate boundaries for MainScreen and NoteScreen.
  - Define mutation-based contracts between ViewModels and delegates.
  - Keep delegates from receiving `ViewModel` instances directly.
  - Keep ViewModel public APIs stable for UI call sites.

- Step 2 - MainScreen internal state split:
  - Introduce focused internal state slices for notes, folders, search, dialogs, and non-sticky effects.
  - Build `UiState.Result<MainScreenState>` inside `MainScreenViewModel` by combining internal slices.
  - Keep `UiState.Loading` and `UiState.Error` as root screen states for initial loading and fatal screen errors only.
  - Keep dialogs and one-off effects out of `UiState.Result` aggregation.

- Step 3 - NoteScreen internal state split:
  - Introduce focused internal state slices for note content, tasks, folder assignment, dialogs, and non-sticky effects.
  - Build `UiState.Result<NoteScreenState>` inside `NoteScreenViewModel` by combining internal slices.
  - Keep `UiState.Loading` and `UiState.Error` as root screen states for initial loading and fatal screen errors only.
  - Keep dialogs and one-off effects out of `UiState.Result` aggregation.

- Step 4 - MainScreen delegate extraction:
  - Extract MainScreen notes flow into a focused delegate.
  - Extract MainScreen folders flow into a focused delegate.
  - Extract MainScreen dialog/event dispatch flow into a focused delegate.
  - Extract MainScreen navigation flow into a focused delegate.
  - Preserve behavior parity and existing UI-facing API signatures.

- Step 5 - NoteScreen delegate extraction:
  - Extract NoteScreen note content flow into a focused delegate.
  - Extract NoteScreen task flow into a focused delegate.
  - Extract NoteScreen folder assignment flow into a focused delegate.
  - Extract NoteScreen dialog/event dispatch flow into a focused delegate.
  - Extract NoteScreen navigation flow into a focused delegate.
  - Preserve behavior parity and existing UI-facing API signatures.

- Step 6 - Tests and verification:
  - Update or add tests for delegate boundaries and critical flows.
  - Verify UI-facing API parity so existing UI call sites compile without behavior changes.
  - Run relevant tests/build checks for touched areas.
  - Verify acceptance criteria and critical edge cases.
