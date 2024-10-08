plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    id("maven-publish")
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
        }

        debug {
            isMinifyEnabled = false
        }
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
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlin.serialization)
    implementation(libs.kotlin.coroutine.core)
    implementation(libs.kotlin.coroutine.android)
    implementation(libs.lifecycle.viewmodel.ktx)
    api(libs.material)
    implementation(libs.annotation)

    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutine.test)
    testImplementation(libs.kotest.assertion)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.runner)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.waddle-corp"
                artifactId = "gentoo"
                version = "1.0.0-beta"

                artifact(layout.buildDirectory.file("outputs/aar/gentoo-release.aar")) {
                    classifier = "release"
                }
            }
        }
    }
}
