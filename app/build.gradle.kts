plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.xxx.devcore"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.xxx.devcore"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    ///todo =============导航================
    implementation(libs.androidx.navigation.compose)
    ///todo =============vm================
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    ///todo =============coroutines================
    implementation(libs.kotlinx.coroutines.android)
    ///todo =============网络架构================
    implementation(libs.retrofit)
    implementation(libs.okhttp3)
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.converter.gson)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.gson)
    // kotlin序列化
    implementation(libs.kotlinx.serialization.json)
    ///todo =============日志框架================
    implementation(libs.timber)
    ///todo =============系统icons================
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    ///todo =============coil================
    implementation(libs.coil.compose)
    ///todo =============webkit================
    implementation(libs.androidx.webkit)
    ///todo =============启动页================
    implementation(libs.androidx.core.splashscreen)
    ///todo =============吐司框架================
    //implementation(libs.toaster)
    ///todo =============权限框架================
    //implementation(libs.xxpermissions)
    ///todo =============腾讯存储================
    implementation(libs.mmkv)
    ///todo =============支付宝支付================
    implementation(libs.alipaysdk.android)
    ///todo =============compose pager================
    implementation("com.google.accompanist:accompanist-pager:0.36.0")
    ///todo =============constraintlayout================
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")
    ///todo =============arouter================
    //implementation("com.alibaba:arouter-api:1.5.2")
    //kapt("com.alibaba:arouter-compiler:1.5.2")
    ///todo =============startup================
    implementation("androidx.startup:startup-runtime:1.2.0")
    ///todo =============datetime================
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
    ///todo =============datastore================
    implementation("androidx.datastore:datastore:1.2.0")
    implementation("androidx.datastore:datastore-preferences:1.2.0")


    ///todo plugin
    implementation(project(":login"))
    implementation(project(":network"))

    ///todo material3 不支持Pulltorefresh， material 支持
    implementation("androidx.compose.material:material:1.10.2")


}