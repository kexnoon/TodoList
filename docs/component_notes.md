# Notes component

## Goals and Requirements

- **Goal:**
    - Provide a maintainable, testable, and MVP-focused business logic layer for notes and note tasks management.
- **Requirements:**
    - Fast iteration and minimal over-engineering: repositories-first, use cases only where clearly needed.
    - Support for reactive data flows with Kotlin `Flow` for seamless UI updates.
    - All critical logic should be covered by unit and integration tests.
    - Keep the design easily refactorable for future improvements (expanding use-case layer, improved error handling, etc.).
    - Avoid project stagnation: the module should enable fast delivery and not get stuck.

## Overview

The `:component-notes` module implements all core business logic for `Note` and `NoteTask`, abstracting away storage details and enabling a clean, reactive interface for UI and feature modules.

- **Repositories:**
    - `NoteRepository` — CRUD for `Note`, exposes flows for list/detail.
    - `TaskRepository` — CRUD for `NoteTask`.
- **Mappers:** Transform Room entities (`NoteEntity`, `NoteTaskEntity`, `NoteWithTasks`) to domain models and vice versa.
- **Use Cases:** Only for operations that require value transformation or extra logic (rename/update status for notes and tasks).
- **Reactive flows:** Reading methods (`getAllNotes`, `getNoteById`) return `Flow`, so the UI always observes up-to-date data. Note that these methods are located in repositories and have no specific UseCases to call them.
- **Pragmatic approach:** Some best practices (like a generic `BaseUseCase` or advanced error handling) are intentionally skipped in MVP phase for simplicity.

## Testing strategy

### Test layers:

1. **Unit tests**
    - Use cases (e.g., `RenameNoteUseCase`, `UpdateNoteStatusUseCase`, etc.)
    - Mappers (e.g., `NoteWithTasks` → `Note`)
    - Only happy paths and a few edge cases.
    - Dependencies are mocked with `mockk` or `Mockito`.
2. **Integration tests**
    - Repositories are tested with in-memory Room DB.
    - All CRUD methods are verified for correct DB interaction and flow emissions.
    - Edge cases: missing IDs, empty DB, notes without tasks, etc.
    - Typical scenarios: create, read, update, delete, and flow/reactivity.
3. **End-to-end tests**
    - Not a priority for MVP, but the design allows for future extension.

## Known issues

- `update`/`delete` methods in repositories are not TDD-friendly and are tricky to mock or isolate from storage.
- Returning `Boolean` instead of rich error results is a compromise for MVP speed; future refactoring may use `Result`/sealed classes.
- Redundant use of `noteId` in `TaskRepository` methods should be eliminated during refactoring.
- Current integration tests for repositories cover both repository and database/DAO logic; with more unit tests for DAOs in `:storage`, some of these may be redundant.
- The public interface of repositories is broader than strictly needed (some should be internal/glue-only).

---

## Possible improvements

- Expand the use-case layer for better separation and easier testing.
- Replace `Boolean` return types in repositories with sealed error/result types for clarity.
- Move to TDD when adding new features or refactoring.
- Eliminate unnecessary propagation of `noteId` in task-related methods.
- Add full error handling at the business logic layer.
- Write unit tests for DAOs in `:storage` and migrate some repository integration tests to unit tests.
- Add end-to-end and UI tests for feature completeness.
- Refactor repository interfaces to minimize public API surface.

## Conclusion

`:component-notes` is a pragmatic and MVP-focused business logic module. All major architectural decisions are aimed at delivering value quickly while maintaining a clear upgrade path for future expansion and improvements.

Testing is focused on value: use cases and mappers are covered by unit tests, repositories by integration tests.

**As of 17.07.2025,** the design prioritizes getting things done and avoiding the "architecture for architecture’s sake" trap.

## References

- [Android Testing Fundamentals](https://developer.android.com/training/testing/fundamentals)
- [Domain Layer Guidance (Android Developers)](https://developer.android.com/topic/architecture/domain-layer)
- ["How to write clean use cases" (YouTube)](https://www.youtube.com/watch?v=8kGIlALKO-s)
- ["5 mistakes with use cases" (YouTube)](https://www.youtube.com/watch?v=ufq3LJ-5P6s)