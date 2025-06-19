plugins {
    id("convention.core.app")
    id("convention.compose")
    id("convention.room")
    id("convention.koin.core")
    id("convention.koin.compose")
}

android {
    namespace = "de.telma.todolist.architecture_example"
    compileSdk = 35

    defaultConfig {
        applicationId = "de.telma.todolist.architecture_example"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {
    implementation(project(":core-ui"))
    implementation(project(":storage"))
    implementation(project(":component-notes"))
    implementation(project(":feature-example"))
}