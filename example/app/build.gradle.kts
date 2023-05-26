plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.jacoco.android)
}

android {
    namespace = "com.mxalbert.example.android"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.mxalbert.example.android"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "version"
    productFlavors {
        create("free") {
            dimension = "version"
            applicationId = "com.dicedmelon.example.android.free"
        }
        create("paid") {
            dimension = "version"
            applicationId = "com.dicedmelon.example.android"
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    buildFeatures.viewBinding = true

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

jacoco {
    toolVersion = "0.8.10"
}

tasks.withType<Test>().configureEach {
    extensions.configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

jacocoAndroidUnitTestReport {
    csv.required.set(true)
    html.required.set(true)
    xml.required.set(true)
}

dependencies {
    implementation(libs.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.espresso.core)
}
