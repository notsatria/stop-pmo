import org.gradle.kotlin.dsl.testImplementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.devtoolsKsp)
    kotlin("plugin.serialization") version "1.9.0"
}

android {
    namespace = "dev.notsatria.stop_pmo"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.notsatria.stop_pmo"
        minSdk = 24
        targetSdk = 36
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
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.work.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    testImplementation(libs.androidx.work.testing)
    testImplementation(libs.mockk)

    testImplementation(libs.koin.test.junit4)
    testImplementation(libs.koin.android.test)

    // Room
    implementation(libs.room)
    ksp(libs.roomCompiler)
    implementation(libs.roomKtx)

    // Koin
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.bundles.koin)

    // Compose Navigation
    implementation(libs.androidx.navigation.compose)

    // Timber
    implementation(libs.timber)

    // Serialization
    implementation(libs.kotlinx.serialization)

    // Material Kolor
    implementation(libs.materialKolor)

    // Datetime
    implementation(libs.kotlinx.datetime)

    // DataStore
    implementation(libs.datastore)

    // Logger
    implementation(libs.logger)

    // Charts
    implementation(libs.compose.charts)
}