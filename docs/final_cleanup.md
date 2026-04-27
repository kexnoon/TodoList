# Final Cleanup Decisions

## Active Modules

The active Gradle module graph is:

- `:app`
- `:core-ui`
- `:storage`
- `:component-notes`
- `:feature-main`

Legacy/example modules (`:app-example`, `:feature-example`, and `:core-data`) are not part of the active project setup.

## Launcher and Deep Links

`MainActivity` owns one launcher intent filter with `MAIN` and `LAUNCHER`.

Deep-link handling is kept in a separate `VIEW` intent filter so launcher visibility does not depend on deep-link `data` matching.

## Database Asset Strategy

The configured Room database name remains variant-specific through `BuildConfig.DB_NAME`.

The packaged seed asset is explicit and stable: `todo_list_dev.db`. This avoids release builds deriving a missing asset name from the release database name.

## Time Source

`Clock` is bound once in the app-level Koin module. Feature/component modules consume that binding instead of declaring their own time source.

## Related Architecture Work

ViewModel delegate splitting is tracked separately in `specs/viewmodel-delegates.md`.
