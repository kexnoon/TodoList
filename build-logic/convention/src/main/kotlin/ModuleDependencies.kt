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