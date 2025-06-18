import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class KoinCoreConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                add("runtimeOnly", libs.findLibrary("koin.core").get())
                add("implementation", libs.findLibrary("koin.core.coroutines").get())
                add("implementation", libs.findLibrary("koin.android").get())
                add("implementation", libs.findLibrary("koin.workmanager").get())

                add("testImplementation", libs.findLibrary("koin.android.test").get())
                add("androidTestImplementation", libs.findLibrary("koin.android.test").get())
            }
        }
    }
}

class KoinComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                add("runtimeOnly", libs.findLibrary("koin.compose").get())
                add("runtimeOnly", libs.findLibrary("koin.androidx.compose").get())
                add("implementation", libs.findLibrary("koin.androidx.compose.navigation").get())
            }
        }
    }
}