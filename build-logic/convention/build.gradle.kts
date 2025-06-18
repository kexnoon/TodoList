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
        register("coreConventionPlugin") {
            id = "convention.core"
            implementationClass = "CoreConventionPlugin"
        }
        register("roomConventionPlugin") {
            id = "convention.room"
            implementationClass = "RoomConventionPlugin"
        }
    }
}
