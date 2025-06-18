plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("convention.core")
    id("convention.compose")
}

android {
    namespace = "de.telma.todolist.feature_main"
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    dependencies {

        implementation(project(":component-notes"))
        implementation(project(":core-ui"))

        implementation(libs.kotlinx.serialization.json)

        //Koin
        runtimeOnly(libs.koin.core)
        implementation(libs.koin.core.coroutines)
        implementation(libs.koin.android)
        implementation(libs.koin.workmanager)
        runtimeOnly(libs.koin.compose)
        runtimeOnly(libs.koin.androidx.compose)
        implementation(libs.koin.androidx.compose.navigation)
        testImplementation(libs.koin.android.test)
        androidTestImplementation(libs.koin.android.test)
    }
}