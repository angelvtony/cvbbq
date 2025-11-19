import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.cvbbq"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.cvbbq"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // --- API KEY SETUP ---
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { stream ->
                localProperties.load(stream)
            }
        }
        // Reads the key you put in local.properties
        val geminiApiKey: String = localProperties.getProperty("GEMINI_API_KEY") ?: ""

        // Makes it available in your Kotlin code as BuildConfig.GEMINI_API_KEY
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")

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
        debug {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation(libs.androidx.constraintlayout)

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // Image Loading
    implementation("com.github.bumptech.glide:glide:4.16.0") // 5.x is alpha, 4.16 is stable
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    // Animation
    implementation("com.airbnb.android:lottie:6.6.0")

    // --- GEMINI AI SDK (ADD THIS) ---
    // Use version 0.9.0 or higher to support Gemini 1.5 models
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // Networking (Retrofit - Keep only if you have other APIs, not needed for the SDK above)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // PDF
    implementation("com.itextpdf:itext7-core:7.2.5")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

kapt {
    correctErrorTypes = true
}