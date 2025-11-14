import org.gradle.kotlin.dsl.release

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)

}
android {
    namespace = "com.fhzapps.bodytrack"
    compileSdk {
        version = release(36)
    }
    buildFeatures{
        buildConfig=true
    }
    defaultConfig {
        applicationId = "com.fhzapps.bodytrack"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        buildConfigField("String", "API_KEY","f331d51c0amsh341ca9b8a82ab86p1906f6jsn8817dd9102ee")
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.ktor)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)


    // Koin for Jetpack Compose
    implementation(libs.koin.androidx.compose)
    implementation (libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    ksp(libs.androidx.room.compiler)

    //coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.gif) //necessary for GIFS

    //retrofit
    implementation(libs.gson)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)


}
