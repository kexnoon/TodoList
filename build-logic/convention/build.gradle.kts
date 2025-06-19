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
        register("appCoreConventionPlugin") {
            id = "convention.core.app"
            implementationClass = "AppCoreConventionPlugin"
        }
        register("libraryCoreConventionPlugin") {
            id = "convention.core.library"
            implementationClass = "LibraryCoreConventionPlugin"
        }
        register("composeConventionPlugin") {
            id = "convention.compose"
            implementationClass = "ComposeConventionPlugin"
        }
        register("roomConventionPlugin") {
            id = "convention.room"
            implementationClass = "RoomConventionPlugin"
        }
        register("koinCoreConventionPlugin") {
            id = "convention.koin.core"
            implementationClass = "KoinCoreConventionPlugin"
        }
        register("koinComposeConventionPlugin") {
            id = "convention.koin.compose"
            implementationClass = "KoinComposeConventionPlugin"
        }
    }
}
