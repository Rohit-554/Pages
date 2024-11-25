import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.org.jetbrains.kotlin.kapt)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "io.jadu.pages"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.jadu.pages"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        properties.load(rootProject.file("local.properties").inputStream())
        buildConfigField("String","EMAIL", "\"${properties.getProperty("EMAIL")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    val nav_version = "2.8.3"
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
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // ViewModel utilities for Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    kapt(libs.androidx.lifecycle.compiler)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)

    // To use Kotlin annotation processing tool (kapt)
    kapt(libs.androidx.room.compiler)
    implementation (libs.androidx.room.ktx)

    // Jetpack Compose integration
    implementation(libs.androidx.navigation.compose.v283)
    //Google Fonts
    implementation(libs.androidx.ui.text.google.fonts.v174)
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation (libs.androidx.material.icons.extended.v175)
    implementation ("io.coil-kt:coil-compose:2.1.0")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.compose.material:material:1.7.4")
    implementation ("com.airbnb.android:lottie-compose:6.6.0")
    implementation ("com.google.code.gson:gson:2.11.0")

    val paging_version = "3.3.4"
    val work_version = "2.9.1"

    implementation("androidx.paging:paging-runtime:$paging_version")
    implementation("androidx.paging:paging-compose:3.3.4")
    implementation("androidx.room:room-paging:2.6.1")
    implementation("io.coil-kt.coil3:coil-compose:3.0.3")
    implementation ("androidx.glance:glance-appwidget:1.1.1")

    // For interop APIs with Material 3
    implementation ("androidx.glance:glance-material3:1.1.0")
    implementation("androidx.work:work-runtime-ktx:$work_version")
    implementation ("com.github.1902shubh:SendMail:1.0.0")

}

kapt {
    correctErrorTypes = true
}