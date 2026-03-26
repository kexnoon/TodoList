# Search Flow Documentation

## Business requirements
- Provide a functional search across notes that accepts free-text queries, with optional filters by status and date ranges (created and last updated).
- Allow users to sort the results by title, status, created time, or last updated time; default ordering is newest update first.

## Technical requirements
- The Room database is the single source of truth; all search, filter, and sort operations are executed via SQL over the `notes` table.

## Decisions, reasoning, and trade-offs

### Domain layer
- `SearchModel`/`Filters` centralize all search inputs (query, status, created/updated ranges, sort) with normalization that trims empty values to avoid accidental wildcards 
````kotlin
data class Filters(
    val createdFrom: String? = null,
    val createdTo: String? = null,
    val updatedFrom: String? = null,
    val updatedTo: String? = null,
    val status: NoteStatus? = null
)

data class SearchModel(
    val query: String? = null,
    val sortBy: SortBy = SortBy.UPDATED_AT,
    val sortOrder: SortOrder = SortOrder.DESC,
    val filters: Filters = Filters()
)
````

- Added `GetNotesUseCase` as a single point of access for all Notes retrival operations
````kotlin
class GetNotesUseCase(
    private val repository: NoteRepository
) {
    operator suspend fun invoke(search: SearchModel?): Flow<List<Note>> {
        return repository.getNotes(search)
    }
}
````

- Default sort is `SortBy.UPDATED_AT` plus `SortOrder.DESC`
```kotlin
enum class SortBy { TITLE, STATUS, CREATED_AT, UPDATED_AT }
enum class SortOrder { ASC, DESC }
```

### Data layer
- `SqlHelper` builds raw SQL with optional clauses for title (case-insensitive `LIKE`), status, created range, and updated range, plus dynamic `ORDER BY`. The helper keeps argument order aligned with clause order to simplify tests and debugging.
- `NoteRepositoryImpl` turns each search request into a `SimpleSQLiteQuery`, executes it via `NoteDao.getNotesWithTasks`, maps to domain, and applies `distinctUntilChanged` plus `flowOn(Dispatchers.IO)` to keep UI updates efficient
````kotlin
    override suspend fun getNotes(search: SearchModel?): Flow<List<Note>> {
        val queryModel = SqlHelper().getNotesQueryModel(search ?: SearchModel())
        val query = SimpleSQLiteQuery(queryModel.query, queryModel.args.toTypedArray())

        return database.noteDao()
            .getNotesWithTasks(query)
            .map(List<NoteWithTasks>::toNotesList)
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }
````
- Tests cover query correctness: case-insensitive title search, status filter, created/updated ranges, and multiple sort permutations, plus unit coverage of SQL generation.
```kotlin
    @Test
    fun search_by_query_should_be_case_insensitive()

    @Test
    fun filter_by_status_returns_only_matching_status()

    @Test
    fun sort_by_title_asc_returns_ordered_list()

    @Test
    fun filter_by_created_range_returns_only_in_interval()

    @Test
    fun filter_by_updated_range_returns_only_in_interval()

    @Test
    fun sort_by_created_desc_returns_correct_order()

    @Test
    fun sort_by_updated_asc_returns_correct_order()

```

### Storage
- `NoteEntity` now persists both `createdTimestamp` and `lastUpdatedTimestamp` to support dual-date filtering; schema bumped to Room DB version 9.
````kotlin
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(defaultValue = "Untitled")
    val title: String = "Untitled",
    val status: String,
    val createdTimestamp: String,
    val lastUpdatedTimestamp: String
)
````
- `NoteDao` switched from a fixed `getAllNotesWithTasks()` query to a raw, observable query endpoint `getNotesWithTasks(query)` enabling dynamic search while keeping the database the SSOT.
````kotlin
    @Transaction
    @RawQuery(observedEntities = [NoteEntity::class])
    fun getNotesWithTasks(query: SupportSQLiteQuery): Flow<List<NoteWithTasks>>

````

### UI
- `MainScreenViewModel` stores `SearchModel` in state, debounces changes by 300 ms, and triggers `GetNotesUseCase` on each distinct update to avoid spamming the database while keeping typing responsive.
````kotlin
    private var _search: MutableStateFlow<SearchModel> = MutableStateFlow(SearchModel())
    var search: StateFlow<SearchModel> = _search

    init {
        observeSearch()
        getAllNotes()
    }
````

````kotlin
    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch {
            search
                .debounce(timeoutMillis = 300L)
                .distinctUntilChanged()
                .collectLatest {
                    getAllNotes()
                }
        }
    }
````

````kotlin
    fun getAllNotes() {
        getNotesJob?.cancel()
        getNotesJob = viewModelScope.launch {
            getNotesUseCase(search = search.value)
                .collect { collectedNotes ->
                    notes = collectedNotes
                    updateScreenState { state ->
                        state.copy(
                            notes = collectedNotes.map { it.toNotesListItemModel() },
                            searchCounter = if (!search.value.query.isNullOrEmpty()) collectedNotes.size else null
                        )
                    }
            }
        }
    }
````

- New Composables:
-- `SearchBar` shows filter and clear actions when active; 
-- `FilterDialog` collects query, status, and created/updated ranges with date-time pickers; 
-- `SortBar` exposes sort-by and sort-order controls.
- All of those composables are used in `MainScreen`'s `StateResult`
- UX trade-offs: 300 ms debounce slightly delays fetch after typing but reduces redundant DB hits; raw query execution keeps UI consistent with DB at the cost of more SQL surface in code.
