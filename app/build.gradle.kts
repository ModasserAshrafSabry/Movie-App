plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt") // مهم لـ Room
    id("org.jetbrains.kotlin.plugin.compose")
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.movieapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.movieapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // ✅ ربط مفتاح TMDB من gradle.properties
        buildConfigField(
            "String",
            "TMDB_API_KEY",
            "\"${project.findProperty("TMDB_API_KEY") ?: ""}\""
        )
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
        languageVersion = "1.9"
        apiVersion = "1.9"
    }

    buildFeatures {
        compose = true
        buildConfig = true // ⚡ مهم جدًا لتوليد BuildConfig
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
<<<<<<< Updated upstream
<<<<<<< Updated upstream
    implementation(libs.firebase.auth)
=======
    implementation(libs.androidx.ui.text)
    implementation(libs.firebase.firestore)
>>>>>>> Stashed changes
=======
    implementation(libs.androidx.ui.text)
    implementation(libs.firebase.firestore)
>>>>>>> Stashed changes
    val composeBom = platform("androidx.compose:compose-bom:2024.10.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Retrofit & Gson
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Coil (for images)
    implementation("io.coil-kt:coil-compose:2.4.0")


    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.3")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    kapt("androidx.room:room-compiler:2.6.0")

<<<<<<< Updated upstream
<<<<<<< Updated upstream
    implementation("androidx.compose.material:material-icons-extended:1.7.4")
=======
    implementation ("com.google.firebase:firebase-auth-ktx:22.1.2")
    implementation ("com.google.firebase:firebase-firestore-ktx:24.8.1")
>>>>>>> Stashed changes
=======
    implementation ("com.google.firebase:firebase-auth-ktx:22.1.2")
    implementation ("com.google.firebase:firebase-firestore-ktx:24.8.1")
>>>>>>> Stashed changes
}
