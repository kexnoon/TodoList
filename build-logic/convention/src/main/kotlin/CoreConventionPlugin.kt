import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

private class AppCoreConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("com.android.application")
            CoreConventionPlugin().apply(this)
        }
    }
}

private class LibraryCoreConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("com.android.library")
            CoreConventionPlugin().apply(this)
        }
    }
}

private class CoreConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            plugins.apply("org.jetbrains.kotlin.android")

            dependencies {
                add("implementation", libs.findLibrary("androidx.core.ktx").get())
                add("implementation", libs.findLibrary("lifecycle.runtime.ktx").get())
                add("testImplementation", libs.findLibrary("junit").get())
                add("androidTestImplementation", libs.findLibrary("junit.test").get())
                add("androidTestImplementation", libs.findLibrary("espresso.core").get())
                add("testImplementation", libs.findLibrary("mockk.core").get())
                add("androidTestImplementation", libs.findLibrary("mockk.android").get())
                add("testImplementation", libs.findLibrary("coroutines.test").get())
                add("androidTestImplementation", libs.findLibrary("coroutines.test").get())
            }
        }
    }
}