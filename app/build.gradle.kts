//import androidx.glance.appwidget.compose
//import androidx.navigation.compose.navigation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
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
            applicationIdSuffix = ".dev" // Optional: e.g., com.example.myapp.dev
            versionNameSuffix = "-dev"   // Optional
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

    flavorDimensions += "appVersion"

    productFlavors {
        create("example") {
            dimension = "appVersion"
            applicationIdSuffix = ".example"
            versionNameSuffix = "-example"
            buildConfigField("boolean", "isExampleVersion", "true")
        }
        create("full") {
            dimension = "appVersion"
            applicationIdSuffix = ".full"
            versionNameSuffix = "-full"
            buildConfigField("boolean", "isExampleVersion", "false")
        }
    }
}

dependencies {

    implementation(project(":core-ui"))
    implementation(project(":storage"))
    implementation(project(":component-notes"))
    implementation(project(":feature-example"))
    implementation(project(":feature-main"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    //Koin
    runtimeOnly(libs.koin.core)
    implementation(libs.koin.core.coroutines)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.workmanager)
    runtimeOnly(libs.koin.compose)
    runtimeOnly(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.compose.navigation)
    testImplementation(libs.koin.android.test)
    androidTestImplementation(libs.koin.android.test)

    //Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler) // Use 'ksp' for the compiler
}