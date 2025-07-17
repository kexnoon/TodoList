# Navigation

This document describes the navigation architecture and implementation strategy used in the project. The goal is to provide a scalable, modular, and testable approach to navigation using Jetpack Compose and a Coordinator pattern.

## Goals and Requirements

### General Goals

- **Simple to implement**
- **Easy to read**
- **Easily extensible** for future requirements such as dialogs, webviews, fragments, activities, etc..

### Business Requirements

- Support for basic screen-to-screen navigation
- Potential support for error messages and dialogs
- Optional separation of authorized/unauthorized zones via activities and stack reset

### Technical Requirements

- Single source of navigation events
- Navigation triggered via ViewModel to avoid recomposition issues
- Ability to manage and reset the navigation stack

## Architecture Overview

Navigation is coordinated through a centralized `NavigationCoordinator` that emits `NavEvent` objects. These events are collected in `MainActivity`, where they are executed against a `NavController`.

### NavigationCoordinator

```kotlin
class NavigationCoordinator {
    private val _navEvents = MutableSharedFlow<NavEvent>()
    val navEvents: SharedFlow<NavEvent> get() = _navEvents

    suspend fun execute(navEvent: NavEvent) {
        _navEvents.emit(navEvent)
    }
}
```

### NavEvent Structure

Each event encapsulates the transition logic via an interface and `execute()` method:

```kotlin
sealed interface ComposableNavEvent {
    fun execute(navController: NavController)
}

sealed interface ActivityNavEvent {
    fun execute(activity: Activity)
}

sealed class NavEvent {
    data class ToComposeScreen private constructor(
        val destination: Destination? = null,
        val deeplink: Uri? = null
    ) : NavEvent(), ComposableNavEvent {
        constructor(destination: Destination) : this(destination, null)
        constructor(deeplink: Uri) : this(null, deeplink)

        override fun execute(navController: NavController) {
            destination?.let { navController.navigate(it) }
            deeplink?.let { navController.navigate(it) }
        }
    }

    data class PopTo(val destination: Destination, val isInclusive: Boolean = false) : NavEvent(), ComposableNavEvent {
        override fun execute(navController: NavController) {
            navController.popBackStack(destination, isInclusive)
        }
    }

    data object PopBack : NavEvent(), ComposableNavEvent {
        override fun execute(navController: NavController) {
            navController.popBackStack()
        }
    }

    data class Toast(val text: String = "", val length: Length = Length.Short) : NavEvent(), ActivityNavEvent {
        override fun execute(activity: Activity) {
            Toast.makeText(activity, text, length.i).show()
        }

        enum class Length(val i: Int) {
            Short(Toast.LENGTH_SHORT),
            Long(Toast.LENGTH_LONG)
        }
    }
}
```

### ViewModel Integration

ViewModels trigger navigation via the coordinator, separating navigation logic from UI code.

```kotlin
class MainScreenViewModel(
    private val coordinator: NavigationCoordinator,
    private val repository: NoteRepository
) : BaseViewModel<List<Note>, UiEvents?>() {

    fun onButtonOneClick() {
        viewModelScope.launch {
            coordinator.execute(NavEvent.ToComposeScreen(Destination.DummyScreenOne))
        }
    }

    fun onButtonTwoClick() {
        viewModelScope.launch {
            coordinator.execute(NavEvent.ToComposeScreen(Destination.DummyScreenTwo("Sample message")))
        }
    }
}
```

### Event Handling in Activity

In `MainActivity`, navigation events are collected and executed based on their type.

```kotlin
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        coordinator.navEvents.collect { event ->
            when (event) {
                is ComposableNavEvent -> event.execute(navController)
                is ActivityNavEvent -> event.execute(this@MainActivity)
            }
        }
    }
}
```

## Composable Routing

Each screen is declared as a typed `Destination` object, optionally supporting arguments:

```kotlin
sealed class Destination {
    @Serializable object MainScreen : Destination()
    @Serializable object DummyScreenOne : Destination()
    @Serializable data class DummyScreenTwo(val message: String) : Destination()
}
```

Composable screens are registered in the `NavHost`, with arguments extracted as needed:

```kotlin
composable<Destination.DummyScreenTwo> {
    val args = it.toRoute<Destination.DummyScreenTwo>()
    val viewModel = koinViewModel<DummyScreenTwoViewModel>()
    DummyScreenTwo(viewModel = viewModel, message = args.message)
}
```

### Navigation Extensions

To reduce boilerplate and centralize screen registration, each screen’s routing logic is placed into an extension function on `NavGraphBuilder`:

```kotlin
internal fun NavGraphBuilder.dummyScreenTwo() {
    composable<Destination.DummyScreenTwo> {
        val args = it.toRoute<Destination.DummyScreenTwo>()
        val viewModel = koinViewModel<DummyScreenTwoViewModel>()
        DummyScreenTwo(viewModel = viewModel, message = args.message)
    }
}
```

## Known issues

- Private primary constructor in `NavEvent.ToComposeScreen` will become an error in Kotlin 2.2 There should be some other way to implement both navigating via destination and deeplinks

## Possible improvements
- Better deep link handling
- Supporting multiple navigation graphs for authorized/unauthorized areas

## Conclusion

This navigation setup balances clarity, extensibility, and separation of concerns. While further refinements (like advanced routing DSLs or wrappers) are possible, the current approach is robust and scalable for the app’s current scope.