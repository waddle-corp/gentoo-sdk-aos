plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.waddle.gentoo"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField("String", "API_KEY", "\"${System.getenv("GENTOO_RELEASE_API_KEY")}\"")
            buildConfigField("String", "DAILY_SHOT_BASE_URL", "\"${System.getenv("GENTOO_RELEASE_BASE_URL")}\"")
        }

        debug {
            isMinifyEnabled = false

            buildConfigField("String", "API_KEY", "\"${System.getenv("GENTOO_DEBUG_API_KEY")}\"")
            buildConfigField("String", "DAILY_SHOT_BASE_URL", "\"${System.getenv("GENTOO_DEBUG_BASE_URL")}\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlin.serialization)
    implementation(libs.kotlin.coroutine.core)
    implementation(libs.kotlin.coroutine.android)
    api(libs.material)
    implementation(libs.annotation)

    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutine.test)
    testImplementation(libs.kotest.assertion)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.runner)
}
