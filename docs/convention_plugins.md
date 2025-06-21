# Convention Plugins

This document explains how Convention Plugins are implemented in the project to manage dependencies and simplify Gradle configuration.

## Goals

- Group and isolate dependencies by framework (e.g., Compose, Koin, Room)
- Preserve original Gradle dependency methods (e.g., `implementation`, `ksp`, `androidTestImplementation`)
- Provide a type-safe mechanism for referencing project modules
- Avoid code duplication and dependency sprawl in build files

## Why Not `buildSrc` or TOML Bundles?

### `buildSrc` Drawbacks

- `buildSrc` is compiled before the main project — it can't access Version Catalog (TOML).
- Modifying `buildSrc` triggers a full rebuild of all modules.

### TOML Bundle Limitations

- You can't control how each dependency is added (e.g., `implementation`, `runtimeOnly`, `testImplementation`) inside a single bundle.
- Maintaining multiple tiny bundles per configuration becomes confusing and error-prone.

## Solution: `build-logic` + Convention Plugins

We use a custom `:build-logic` module that hosts all plugin logic. These are real Gradle plugins written in Kotlin. Each plugin encapsulates its own dependency group and configuration.

### Folder Structure

```
:build-logic
\- :convention
   \- main/kotlin
      |- ComposeConventionPlugin.kt
      |- KoinCoreConventionPlugin.kt
      |- KoinComposeConventionPlugin.kt
      |- RoomConventionPlugin.kt
      |- AppCoreConventionPlugin.kt
      |- LibraryCoreConventionPlugin.kt
      |- ModuleDependencies.kt
   \- build.gradle.kts
\- settings.gradle.kts

```

### Registering build-logic in project’s `settings.gradle.kts`

```kotlin
pluginManagement {

    **includeBuild("build-logic")**
    
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
```

### Example Plugin: `RoomConventionPlugin.kt`

```kotlin
class RoomConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        val libs = target.extensions.getByType<VersionCatalogsExtension>().named("libs")

        target.plugins.apply("com.google.devtools.ksp")

        target.dependencies.apply {
            add("implementation", libs.findLibrary("room.runtime").get())
            add("implementation", libs.findLibrary("room.ktx").get())
            add("ksp", libs.findLibrary("room.compiler").get())
        }
    }
}

```

### Registering Plugins in `convention/build.gradle.kts`

```kotlin
plugins {
    `kotlin-dsl`
}

group = "de.telma.todolist.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.android.tools.build.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        **register("roomConventionPlugin") {
            id = "convention.room" //id to reference the plugin
            implementationClass = "RoomConventionPlugin" //plugin's class
        }**
        register("composeConventionPlugin") {
            id = "convention.compose"
            implementationClass = "ComposeConventionPlugin"
        }
        register("koinCoreConventionPlugin") {
            id = "convention.koin.core"
            implementationClass = "KoinCoreConventionPlugin"
        }
        register("koinComposeConventionPlugin") {
            id = "convention.koin.compose"
            implementationClass = "KoinComposeConventionPlugin"
        }
        register("appCoreConventionPlugin") {
            id = "convention.core.app"
            implementationClass = "AppCoreConventionPlugin"
        }
        register("libraryCoreConventionPlugin") {
            id = "convention.core.library"
            implementationClass = "LibraryCoreConventionPlugin"
        }
    }
}

```

### Using a Plugin in a Module

```kotlin
// build.gradle.kts (:storage)
plugins {
    **id("convention.room")**
    id("convention.core.library")
}

```

## Project Module Imports

### `ModuleDependencies.kt`

Provides type-safe project module imports via extension properties:

```kotlin
object Modules {
    const val CORE_UI = "core-ui"
    const val STORAGE = "storage"
    const val COMPONENT_NOTES = "component-notes"
    const val FEATURE_EXAMPLE = "feature-example"
    const val FEATURE_MAIN = "feature-main"
}

fun DependencyHandler.projectByName(name: String): ProjectDependency {
    return project(mapOf("path" to ":$name")) as ProjectDependency
}

val DependencyHandler.coreUi: ProjectDependency
    get() = projectByName(Modules.CORE_UI)

// and so on for other modules...

```

### Example Usage

```kotlin
//build.gradle.kts (some module)

dependencies {
    implementation(coreUi)
    implementation(storage)
    implementation(componentNotes)
}

```

## Plugin Overview

| Plugin ID | Purpose |
| --- | --- |
| `convention.core.app` | Base plugin for `:app`, uses `com.android.application` |
| `convention.core.library` | Base plugin for feature/components, uses `android.library` |
| `convention.compose` | Compose UI dependencies |
| `convention.room` | Room + KSP setup |
| `convention.koin.core` | Koin core dependencies |
| `convention.koin.compose` | Koin integration for Compose |

### Dependencies per Plugin

- convention.core.app
    - `androidx.core:core-ktx`,
    - `androidx.lifecycle:lifecycle-runtime-ktx`,
    - `junit:junit`,
    - `androidx.test.ext:junit`,
    - `androidx.test.espresso:espresso-core`
- convention.core.library
    - `androidx.core:core-ktx`,
    - `androidx.lifecycle:lifecycle-runtime-ktx`,
    - `junit:junit`,
    - `androidx.test.ext:junit`,
    - `androidx.test.espresso:espresso-core`
- convention.compose
    - `androidx.activity:activity-compose`,
    - `androidx.compose:compose-bom`,
    - `androidx.compose.ui:ui`,
    - `androidx.compose.ui:ui-graphics`,
    - `androidx.compose.ui:ui-tooling`,
    - `androidx.compose.ui:ui-tooling-preview`,
    - `androidx.compose.ui:ui-test-manifest`,
    - `androidx.compose.ui:ui-test-junit4`,
    - `androidx.compose.material3:material3`,
    - `androidx.navigation:navigation-compose`

- convention.room
    - `androidx.room:room-runtime`,
    - `androidx.room:room-ktx`,
    - `androidx.room:room-compiler`

- convention.koin.core
    - `io.insert-koin:koin-core`,
    - `io.insert-koin:koin-core-coroutines`,
    - `io.insert-koin:koin-android`,
    - `io.insert-koin:koin-androidx-workmanager`,
    - `io.insert-koin:koin-android-test`
- convention.koin.compose
    - `io.insert-koin:koin-compose`,
    - `io.insert-koin:koin-androidx-compose`,
    - `io.insert-koin:koin-androidx-compose-navigation`


## Known Issues

- Android Studio may falsely report Version Catalog dependencies as unused.
- Plugin interface is verbose and can be hard to maintain at scale.
- Adding plugins inside of Convention plugin was an unpleasant experience, to say the least.

## Possible improvements
- Future iteration may involve wrapping plugin creation in Kotlin DSL builders for improved clarity.

## References

- [Simplify Android Builds with Convention Plugins](https://medium.com/@sridhar-sp/simplify-your-android-builds-a-guide-to-convention-plugins-b9fea8c5e117)
- [Migrating from buildSrc](https://medium.com/@ruikg0857/migrating-buildsrc-to-version-catalog-and-build-logic-5ab6866b8194)
- [Google Architecture Samples - Convention Plugins](https://github.com/android/architecture-samples/tree/multimodule)