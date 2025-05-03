plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.rizqi.wideloc"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rizqi.wideloc"
        minSdk = 21
        targetSdk = 35
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.timber)
    implementation(libs.stepper.indicator) {
        exclude(group = "com.android.support", module = "support-compat")
    }
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.scarlet.core)
    implementation(libs.scarlet.websocket.okhttp)
    implementation(libs.scarlet.moshi.adapter)
    implementation(libs.scarlet.stream.adapter.coroutines)
    implementation(libs.scarlet.message.adapter.gson)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    //noinspection KaptUsageInsteadOfKsp
    kapt(libs.room.compiler)
//    implementation(libs.kotlinx.metadata.jvm)

    testImplementation(libs.room.testing)

}