import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class RoomConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            plugins.apply("com.google.devtools.ksp")

            dependencies {
                add("implementation", libs.findLibrary("room.runtime").get())
                add("implementation", libs.findLibrary("room.ktx").get())
                add("ksp", libs.findLibrary("room.compiler").get())
            }

        }
    }
}