# Feature: ViewModel Delegates Refactor

## Objective
- Split oversized screen ViewModels into focused delegates to improve maintainability while preserving current behavior.
- Expand the effort to include project-level cleanup, launcher visibility bug fix, and documentation updates.

## Context
- `MainScreenViewModel` and `NoteScreenViewModel` currently combine state orchestration, data operations, dialog events, and navigation.
- This coupling makes incremental changes risky and increases testing effort per change.
- The repository still contains legacy/example modules (`core-data`, `feature-example`, `app-example`) that are no longer part of the intended architecture.
- There is a blocking app startup issue: after installation, the app does not appear in launcher/home screen.
- `README.md` does not fully reflect the current project state and architecture decisions.
- For this feature you can ignore TDD since it's more about refactoring than adding new functionality

## Business rules
- User-visible behavior must remain unchanged for both screens.
- Existing screen contracts (`UiState`, `UiEvents`, `UiErrors`) must remain functionally equivalent.
- Existing user flows for notes, tasks, folders, search, selection mode, move flow, and navigation must keep current behavior.
- A freshly installed app build must be visible in the launcher and openable from launcher/home screen.
- Project cleanup must not remove runtime-critical modules or break existing feature behavior.

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
- Remove `core-data`, `feature-example`, and `app-example` modules from project configuration and module dependency graph.
- Fix app launcher visibility by correcting Android app entry configuration (manifest/build configuration) so one valid launcher entry point is exposed.
- Update `README.md` to match actual modules, build/run instructions, architecture state, and current development workflow.
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
- Existing tests continue to pass; new/updated tests cover delegate behavior boundaries and critical flows.
- `core-data`, `feature-example`, and `app-example` are removed from active project/module setup and no longer required for build.
- App icon/entry is visible after install in launcher and app can be opened from launcher.
- `README.md` is updated and consistent with current repository structure and run/build flow.
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
- Multiple launcher candidates in manifest merge output.
- Stale module references in Gradle settings/dependencies after module removal.
- Build variants/flavors producing no launcher activity.

## Out of scope
- New user features or UX changes.
- Navigation architecture redesign.
- Migration away from current `BaseViewModel` hierarchy.
- Reworking domain/data use case contracts unless required by refactor safety.
- Broad architectural migration unrelated to delegate split and cleanup scope.

# implementation-final

## Implementation steps
- Step 1 - Delegate split:
  - Confirm and lock delegate boundaries for MainScreen and NoteScreen.
  - Keep ViewModel-to-delegate communication mutation-based only (no ViewModel instance passing).
  - Keep ViewModel public APIs stable for UI call sites.
  - Split oversized screen state inside ViewModels into focused internal state slices:
    - MainScreen: notes, folders, search, dialogs, and non-sticky effects.
    - NoteScreen: note content, tasks, folder assignment, dialogs, and non-sticky effects.
  - Keep `UiState.Loading` and `UiState.Error` as root screen states for initial loading and fatal screen errors only.
  - Build `UiState.Result` inside each ViewModel by combining internal state slices into the existing legacy screen state contract.
  - Keep dialogs and one-off effects out of `UiState.Result` aggregation; expose them as dedicated ViewModel-owned flows for UI control and transient actions.
  - Use non-sticky event delivery semantics for one-off effects to avoid replaying stale actions after recomposition or configuration changes.
  - Extract MainScreen responsibilities into delegates (notes/folders/dialog-navigation handlers as defined).
  - Extract NoteScreen responsibilities into delegates (note content/tasks/folder/dialog-navigation handlers as defined).
  - Maintain behavior parity and existing UI-facing API signatures.

- Step 2 - Module cleanup:
  - Remove `core-data`, `feature-example`, and `app-example` from `settings.gradle.kts`.
  - Remove related module dependencies/usages from Gradle module configuration.
  - Ensure project sync/build succeeds after cleanup.

- Step 3 - Launcher visibility bug fix:
  - Identify launcher entrypoint issue in merged manifest/build setup.
  - Apply minimal fix so app is discoverable in launcher/home screen after install.
  - Verify app opens correctly from launcher.

- Step 4 - P1/P2 stability and cleanup fixes:
  - Fix nav event collection lifecycle in `MainActivity` to avoid duplicate collectors from recomposition.
  - Ensure `NoteScreenViewModel` note-observation flow is single-source and cancels/replaces previous observer jobs safely.
  - Split launcher and deep-link intent filters so launcher visibility is independent and deterministic.
  - Resolve release database initialization mismatch between configured DB name and packaged asset strategy.
  - Remove duplicate `Clock` DI bindings and keep one explicit source of truth.
  - Replace sticky one-off UI event handling approach with non-sticky event delivery semantics.
  - Align NoteScreen folder-flow failures with dedicated error types instead of note-rename errors.
  - Remove unnecessary `async/await` wrappers where no parallel work exists.

- Step 5 - Documentation update:
  - Update `README.md` to reflect current module graph and setup.
  - Create separate delegates documentation in `docs` and link it from `README.md`.

- Step 6 - Verification:
  - Run relevant tests/build checks for touched areas.
  - Verify acceptance criteria and critical edge cases.
