import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class CoreConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                add("implementation", libs.findLibrary("androidx.core.ktx").get())
                add("implementation", libs.findLibrary("lifecycle.runtime.ktx").get())
                add("testImplementation", libs.findLibrary("junit").get())
                add("androidTestImplementation", libs.findLibrary("junit.test").get())
                add("androidTestImplementation", libs.findLibrary("espresso.core").get())
            }
        }
    }
}