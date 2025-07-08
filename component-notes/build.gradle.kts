plugins {
    id("convention.core.library")
    id("convention.koin.core")
    id("convention.room")
}

android {
    namespace = "de.telma.todolist.component_notes"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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


    packaging {
        resources {
            pickFirsts += "META-INF/LICENSE.md"
            // It's a good idea to proactively add other common ones you might encounter:
            pickFirsts += "META-INF/AL2.0"
            pickFirsts += "META-INF/LGPL2.1"
            pickFirsts += "META-INF/LICENSE-notice.md"
            // pickFirsts += "META-INF/DEPENDENCIES"
            pickFirsts += "META-INF/LICENSE" // If you see errors for LICENSE without .md
            pickFirsts += "META-INF/LICENSE.txt"
            // pickFirsts += "*.kotlin_module"
        }
    }
}

dependencies {
    implementation(storage)
}