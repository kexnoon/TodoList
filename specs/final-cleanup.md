# Feature: Final Project Cleanup

## Objective
- Remove legacy/example modules that are no longer part of the intended architecture.
- Fix the launcher visibility issue so installed app builds are discoverable and openable from launcher/home screen.
- Apply remaining P1/P2 stability and cleanup fixes outside the ViewModel delegates refactor.
- Update project documentation to reflect the current project state and workflow.

## Context
- The repository still contains legacy/example modules (`core-data`, `feature-example`, `app-example`) that are no longer part of the intended architecture.
- There is a blocking app startup issue: after installation, the app does not appear in launcher/home screen.
- `README.md` does not fully reflect the current project state and architecture decisions.
- The ViewModel delegates refactor is tracked separately in `specs/viewmodel-delegates.md`.
- For this feature you can ignore TDD since it's more about cleanup and stabilization than adding new functionality.

## Business rules
- User-visible behavior must remain unchanged.
- Existing user flows for notes, tasks, folders, search, selection mode, move flow, and navigation must keep current behavior.
- A freshly installed app build must be visible in the launcher and openable from launcher/home screen.
- Project cleanup must not remove runtime-critical modules or break existing feature behavior.
- Documentation must describe the actual project structure and current development workflow.

## Functional requirements
- Remove `core-data`, `feature-example`, and `app-example` modules from project configuration and module dependency graph.
- Fix app launcher visibility by correcting Android app entry configuration (manifest/build configuration) so one valid launcher entry point is exposed.
- Fix nav event collection lifecycle in `MainActivity` to avoid duplicate collectors from recomposition.
- Split launcher and deep-link intent filters so launcher visibility is independent and deterministic.
- Resolve release database initialization mismatch between configured DB name and packaged asset strategy.
- Remove duplicate `Clock` DI bindings and keep one explicit source of truth.
- Align NoteScreen folder-flow failures with dedicated error types instead of note-rename errors.
- Remove unnecessary `async/await` wrappers where no parallel work exists.
- Update `README.md` to match actual modules, build/run instructions, architecture state, and current development workflow.
- Add or update project documentation for cleanup decisions that need to remain discoverable.

## Non-functional requirements
- No new dependencies.
- No behavior changes outside the cleanup and stability scope.
- Preserve existing performance characteristics.
- Keep code readable and avoid overengineering.
- Keep docs/spec/task text in English.

## Acceptance criteria
- `core-data`, `feature-example`, and `app-example` are removed from active project/module setup and no longer required for build.
- App icon/entry is visible after install in launcher and app can be opened from launcher.
- `MainActivity` nav event collection does not create duplicate collectors from recomposition.
- Launcher and deep-link intent filters are separated so launcher visibility is independent and deterministic.
- Release database initialization uses a consistent configured DB name and packaged asset strategy.
- Only one explicit `Clock` DI binding source of truth remains.
- NoteScreen folder-flow failures use dedicated folder-related errors instead of note-rename errors.
- Redundant `async/await` wrappers are removed where no parallel work exists.
- `README.md` is updated and consistent with current repository structure and run/build flow.
- Existing tests continue to pass.
- No regressions in existing notes, tasks, folders, search, selection mode, move flow, and navigation behavior.

## Edge cases
- Multiple launcher candidates in manifest merge output.
- Build variants/flavors producing no launcher activity.
- Stale module references in Gradle settings/dependencies after module removal.
- Release build starts with packaged database asset strategy.
- Navigation events emitted during recomposition-heavy screen transitions.
- Folder creation or assignment failures in NoteScreen folder flow.
- Sync failure after task or note updates.

## Out of scope
- ViewModel delegates refactor; it is covered by `specs/viewmodel-delegates.md`.
- New user features or UX changes.
- Navigation architecture redesign.
- Migration away from current `BaseViewModel` hierarchy unless required by a listed stability fix.
- Reworking domain/data use case contracts unless required by cleanup safety.
- Broad architectural migration unrelated to module cleanup, launcher visibility, stability fixes, and documentation.

# implementation-final

## Implementation steps
- Step 1 - Module cleanup:
  - Remove `core-data`, `feature-example`, and `app-example` from `settings.gradle.kts`.
  - Remove related module dependencies/usages from Gradle module configuration.
  - Ensure project sync/build succeeds after cleanup.

- Step 2 - Launcher visibility bug fix:
  - Identify launcher entrypoint issue in merged manifest/build setup.
  - Apply minimal fix so app is discoverable in launcher/home screen after install.
  - Split launcher and deep-link intent filters so launcher visibility is independent and deterministic.
  - Verify app opens correctly from launcher.

- Step 3 - P1/P2 stability and cleanup fixes:
  - Fix nav event collection lifecycle in `MainActivity` to avoid duplicate collectors from recomposition.
  - Resolve release database initialization mismatch between configured DB name and packaged asset strategy.
  - Remove duplicate `Clock` DI bindings and keep one explicit source of truth.
  - Align NoteScreen folder-flow failures with dedicated error types instead of note-rename errors.
  - Remove unnecessary `async/await` wrappers where no parallel work exists.

- Step 4 - Documentation update:
  - Update `README.md` to reflect current module graph and setup.
  - Document cleanup and stability decisions that need to remain discoverable.
  - Link the separate ViewModel delegates spec/documentation where architecture context references delegate splitting.

- Step 5 - Verification:
  - Run relevant tests/build checks for touched areas.
  - Verify acceptance criteria and critical edge cases.
