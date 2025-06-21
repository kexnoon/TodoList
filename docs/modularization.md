# Modularization

This document outlines the modularization approach implemented in the project. The aim is to enhance maintainability, scalability, and clarity while preserving flexibility for future features like authentication or storage refactors.

## Goals of modularization

- **Decouple features and reusable logic**
- **Encapsulate implementation details inside each module**
- **Provide flexibility for future integrations (e.g., authorization, analytics)**

## Final Module Structure

```
:app                   - Main glue module. Hosts MainActivity, DI, and NavHost
:app-example           - Alternative app entry point for running example screens
:core-ui               - Navigation DSL and reusable UI components
:feature-main          - Main feature of the application
:feature-example       - Set of dummy/test screens
:component-notes       - Notes business logic, including use cases and repository
:storage               - Room database and local data source
```

## Design Decisions

### 1. Feature, Domain, and Data Separation

- `component-notes` combines both domain and data logic for the Notes business domain.

> This is a pragmatic choice. Splitting domain and data modules at this scale would create excessive granularity.
>
- `feature-main` and `feature-example` provide Composables, ViewModels and Screens for each of feature and UI logic.
- `:storage` includes Room database, with its entities and DAOs. Potentially can be also used for some form of internal storage

### 2. Navigation Strategy

- Each feature module defines and incapsulates its own destinations.
- Cross-feature navigation is performed via **deep links**.

> This avoids exposing internal destination details and prevents cyclic dependencies.
>

Navigation API Example:

```kotlin
fun NavGraphBuilder.exampleScreens() {
    mainScreen()
    dummyScreenOne()
    dummyScreenTwo()
    dummyScreenThree()
}

internal fun NavGraphBuilder.dummyScreenOne() { ... }
```

> Start destination must remain constant — that’s why we separated  :app-example from .app, so we could have two navigations with static starting point for each.
>

### 3. Dependency Injection

- Koin is used.
- DI is scoped to `:app` and `:app-example` only.
- Bridge module for UI will **not** be introduced. DI stays in glue modules.

> Future migrations to Hilt are possible if Koin becomes limiting.
>

### 4. Public APIs and Visibility

- One function per feature module that registers its screens
- Implementation logic is marked `internal`
- Shared models are declared in `component-notes`

### 5. Build Logic

- Gradle Convention Plugins are used for declaring module dependencies
- `buildSrc` is deprecated; TOML not used for module links due to ordering constraints

Example of module import :

```kotlin
// build-logic/convention/main/kotlin/ModuleDependencies.kt
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.dsl.DependencyHandler

object Modules {
    const val CORE_UI = "core-ui"
    const val STORAGE = "storage"
    const val COMPONENT_NOTES = "component-notes"
    const val FEATURE_EXAMPLE = "feature-example"
    const val FEATURE_MAIN = "feature-main"
}

private fun DependencyHandler.projectByName(name: String): ProjectDependency {
    return project(mapOf("path" to ":$name")) as ProjectDependency
}

val DependencyHandler.coreUi: ProjectDependency
    get() = projectByName(Modules.CORE_UI)

val DependencyHandler.storage: ProjectDependency
    get() = projectByName(Modules.STORAGE)

val DependencyHandler.componentNotes: ProjectDependency
    get() = projectByName(Modules.COMPONENT_NOTES)

val DependencyHandler.featureExample: ProjectDependency
    get() = projectByName(Modules.FEATURE_EXAMPLE)

val DependencyHandler.featureMain: ProjectDependency
    get() = projectByName(Modules.FEATURE_MAIN)
```

```kotlin
// build.gradle.kts(:app)
dependencies {
    implementation(coreUi)
    implementation(storage)
    implementation(componentNotes)
    implementation(featureMain)
}
```

## Technical Constraints

### 1. Flavors vs. Separate App Modules

Initial idea was to use product flavors (`main`, `example`) for switching between app configurations. However, this led to undesirable build variants like `exampleRelease`, which would never be shipped. To avoid this:

- `:app` hosts the production logic
- `:app-example` is a fully separate entry point for testing and debugging feature-example

### 2. DI Scope and bridge module

- Centralized Koin setup across modules became too verbose
- The idea of a DI bridge module, however, was rejected
- All DI setup lives in `:app` and `:app-example`, following the glue-module idea

### 3. Gradle and Dependency Resolution

- Dependencies between modules are declared via convention plugins
- TOML cannot be used for module links due to build ordering constraints
- `buildSrc` is avoided due to long incremental build times tight coupling, and incompatability with Version Catalog (buildSrc is builded before any module)

> Module dependencies are managed using strongly-typed helper extensions in build-logic/convention.
>

## Known issues

- Koin scopes setup across modules is verbose and awkward

## Possible improvements

- Evaluate whether to keep Koin or migrate to Hilt
- Find an alternative to glue-modules if possible

## References

- [Google Modularization Guide](https://developer.android.com/topic/modularization)
- [Best Practices for Modularization](https://developer.android.com/topic/modularization/patterns)
- [Real Clean Architecture](https://medium.com/clean-android-dev/the-real-clean-architecture-in-android-modularization-e26940fd0a23)

---