plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.doctor"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.doctor"
        minSdk = 28
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
}

dependencies {

    // Core
    implementation(libs.appcompat)
    implementation(libs.activity.ktx)
    implementation(libs.constraintlayout)

    // Material
    implementation(libs.material)

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // CardView
    implementation("androidx.cardview:cardview:1.0.0")

    // CoordinatorLayout
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

    // Glide (load ảnh)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}