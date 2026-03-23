# Search Flow Documentation

## Business requirements
- Provide a functional search across notes that accepts free-text queries, with optional filters by status and date ranges (created and last updated).
- Allow users to sort the results by title, status, created time, or last updated time; default ordering is newest update first.

## Technical requirements
- The Room database is the single source of truth; all search, filter, and sort operations are executed via SQL over the `notes` table.

## Decisions, reasoning, and trade-offs

### Domain layer
- `SearchModel`/`Filters` centralize all search inputs (query, status, created/updated ranges, sort) with normalization that trims empty values to avoid accidental wildcards (`component-notes/src/main/java/de/telma/todolist/component_notes/model/SearchModels.kt`).
- `GetNotesUseCase` is a thin pass-through to keep the domain boundary clean while enabling future orchestration or validation (`component-notes/src/main/java/de/telma/todolist/component_notes/useCase/note/GetNotesUseCase.kt`).
- Default sort is `SortBy.UPDATED_AT` plus `SortOrder.DESC`, matching the business need to surface most recently edited notes first.

### Data layer
- `SqlHelper` builds raw SQL with optional clauses for title (case-insensitive `LIKE`), status, created range, and updated range, plus dynamic `ORDER BY` (`component-notes/src/main/java/de/telma/todolist/component_notes/utils/SqlHelper.kt`). The helper keeps argument order aligned with clause order to simplify tests and debugging.
- Rationale for raw queries: flexibility to combine filters and sorts without proliferating DAO methods. Trade-off: reduced compile-time safety and the need for careful SQL construction.
- `NoteRepositoryImpl` turns each search request into a `SimpleSQLiteQuery`, executes it via `NoteDao.getNotesWithTasks`, maps to domain, and applies `distinctUntilChanged` plus `flowOn(Dispatchers.IO)` to keep UI updates efficient (`component-notes/src/main/java/de/telma/todolist/component_notes/repository/NoteRepositoryImpl.kt`).
- Tests cover query correctness: case-insensitive title search, status filter, created/updated ranges, and multiple sort permutations (`component-notes/src/androidTest/java/de/telma/todolist/component_notes/repository/note/GetAllNotesTest.kt`), plus unit coverage of SQL generation (`component-notes/src/test/java/de/telma/todolist/component_notes/utils/SqlHelperTest.kt`).
- Known gap: `SqlHelperTest` expects an exception when created and updated ranges are both set, but the current helper allows both; this is a documented tech debt to resolve.

### Storage
- `NoteEntity` now persists both `createdTimestamp` and `lastUpdatedTimestamp` to support dual-date filtering; schema bumped to Room DB version 9 (`storage/src/main/java/de/telma/todolist/storage/database/entity/NoteEntity.kt`, `storage/src/main/java/de/telma/todolist/storage/database/Database.kt`).
- `NoteDao` switched from a fixed `getAllNotesWithTasks()` query to a raw, observable query endpoint `getNotesWithTasks(query)` enabling dynamic search while keeping the database the SSOT (`storage/src/main/java/de/telma/todolist/storage/database/NoteDao.kt`).
- Trade-offs: raw queries bypass Room's query validation and require migrations for existing installations because of the new `createdTimestamp` column and version bump.

### UI
- `MainScreenViewModel` stores `SearchModel` in state, debounces changes by 300 ms, and triggers `GetNotesUseCase` on each distinct update to avoid spamming the database while keeping typing responsive (`feature-main/src/main/java/de/telma/todolist/feature_main/main_screen/MainScreenViewModel.kt`).
- `SearchBar` shows filter and clear actions when active; `FilterDialog` collects query, status, and created/updated ranges with date-time pickers; `SortBar` exposes sort-by and sort-order controls (`feature-main/src/main/java/de/telma/todolist/feature_main/main_screen/composables/*.kt`).
- `StateResult` wires search UI into the list, displays a hit counter in the app bar when searching, and uses a search-specific empty state message to acknowledge zero results (`feature-main/src/main/java/de/telma/todolist/feature_main/main_screen/states/StateResult.kt`).
- UX trade-offs: 300 ms debounce slightly delays fetch after typing but reduces redundant DB hits; raw query execution keeps UI consistent with DB at the cost of more SQL surface in code.

### Implementation timeline
- TD-77 series: introduced `SortBy/SortOrder`, added search use case, split created vs. updated filters, and guarded filter combinations; initial SQL helper and model fixes.
- TD-79: added `SearchBar` composable and end-to-end search flow; later refactored search logic and added ViewModel unit tests.
- TD-83/85/86: added filter dialog, sorting controls, and UI refinements on `MainScreen`.
- TD-84: added comprehensive search unit tests to validate query behavior.

