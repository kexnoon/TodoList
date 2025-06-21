plugins {
    id("convention.core.app")
    id("convention.compose")
    id("convention.room")
    id("convention.koin.core")
    id("convention.koin.compose")
}

android {
    namespace = "de.telma.todolist"
    compileSdk = 35

    defaultConfig {
        applicationId = "de.telma.todolist"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            isDebuggable = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
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
    implementation(coreUi)
    implementation(storage)
    implementation(componentNotes)
    implementation(featureMain)
}