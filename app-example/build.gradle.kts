plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("convention.core")
    id("convention.room")
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

    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    implementation(libs.compose.navigation)
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