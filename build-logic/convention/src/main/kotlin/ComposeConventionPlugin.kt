import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            plugins.apply("org.jetbrains.kotlin.plugin.compose")
            plugins.apply("org.jetbrains.kotlin.plugin.serialization")

            dependencies {
                add("implementation", libs.findLibrary("activity.compose").get())
                add("implementation", platform(libs.findLibrary("compose.bom").get()))
                add("implementation", libs.findLibrary("compose.ui").get())
                add("implementation", libs.findLibrary("compose.ui.graphics").get())
                add("implementation", libs.findLibrary("compose.ui.tooling.preview").get())
                add("implementation", libs.findLibrary("compose.material3").get())
                add("implementation", libs.findLibrary("compose.navigation").get())
                add("implementation", libs.findLibrary("kotlinx.serialization.json").get())
                add("androidTestImplementation", platform(libs.findLibrary("compose.bom").get()))
                add("androidTestImplementation", libs.findLibrary("compose.ui.test.junit4").get())
                add("debugImplementation", libs.findLibrary("compose.ui.tooling").get())
                add("debugImplementation", libs.findLibrary("compose.ui.test.manifest").get())
            }
        }
    }
}