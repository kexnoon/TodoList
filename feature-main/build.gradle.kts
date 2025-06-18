plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
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

        implementation(libs.androidx.core.ktx)
        implementation(libs.lifecycle.runtime.ktx)
        implementation(libs.activity.compose)
        implementation(platform(libs.compose.bom))
        implementation(libs.compose.ui)
        implementation(libs.compose.ui.graphics)
        implementation(libs.compose.ui.tooling.preview)
        implementation(libs.compose.material3)
        testImplementation(libs.junit)
        androidTestImplementation(libs.junit.test)
        androidTestImplementation(libs.espresso.core)
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
}