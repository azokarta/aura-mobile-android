plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
}

android {
    compileSdk = rootProject.extra["compileSdkVersion"] as Int
    buildToolsVersion = rootProject.extra["buildToolsVersion"] as String

    defaultConfig {
        applicationId = "kz.aura.merp.employee"
        minSdk = rootProject.extra["minSdkVersion"] as Int
        targetSdk = rootProject.extra["targetSdkVersion"] as Int
        multiDexEnabled = true

        versionCode = 19
        versionName = "1.0.14"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // It's not recommended
    signingConfigs {
        create("release") {
            storeFile = file("keystore.jks")
            storePassword = "auraemployee"
            keyPassword = "auraemployee"
            keyAlias = "key_aura_employee"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "BASE_URL_AUTH", "\"http://werp.kz:30001\"")
            buildConfigField("String", "BASE_URL_CORE", "\"http://werp.kz:30020\"")
            buildConfigField("String", "BASE_URL_FINANCE", "\"http://werp.kz:30021\"")
            buildConfigField("String", "BASE_URL_SERVICE", "\"http://werp.kz:30022\"")
            buildConfigField("String", "BASE_URL_CRM", "\"http://werp.kz:30023\"")
            signingConfig = signingConfigs.getByName("release")
        }

        getByName("debug") {
            isDebuggable = true
            buildConfigField("String", "BASE_URL_AUTH", "\"http://werp.kz:32001\"")
            buildConfigField("String", "BASE_URL_CORE", "\"http://werp.kz:32020\"")
            buildConfigField("String", "BASE_URL_FINANCE", "\"http://werp.kz:32021\"")
            buildConfigField("String", "BASE_URL_SERVICE", "\"http://werp.kz:32022\"")
            buildConfigField("String", "BASE_URL_CRM", "\"http://werp.kz:32023\"")
        }
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_11)
        targetCompatibility(JavaVersion.VERSION_11)
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures{
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    // Android
    val kotlinVersion = rootProject.extra["kotlinVersion"]
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.1")

    // UI
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    // Lifecycle
    val lifecycleVersion = "2.3.1"
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

    // Navigation Component
    val navigationComponentVersion = "2.3.5"
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationComponentVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationComponentVersion")

    // Retrofit
    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    // Dagger - Hilt
    val hiltVersion = rootProject.extra["hiltVersion"]
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")

    // Coroutines
    val coroutinesVersion = "1.5.0-native-mt"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    // Mapkit
    val mapkitVersion = "4.0.0-full"
    implementation("com.yandex.android:maps.mobile:$mapkitVersion")

    // Multidex
    val multidexVersion = "1.0.3"
    implementation("com.android.support:multidex:$multidexVersion")

    // SmartLocation
    val smartLocationVersion = "3.3.3"
    implementation("io.nlopez.smartlocation:library:$smartLocationVersion")

    // Firebase
    val firebaseVersion = "28.0.1"
    implementation(platform("com.google.firebase:firebase-bom:$firebaseVersion"))
    implementation ("com.google.firebase:firebase-messaging-ktx")
    implementation ("com.google.firebase:firebase-firestore-ktx")

    // Biometric
    val biometricVersion = "1.2.0-alpha03"
    implementation("androidx.biometric:biometric-ktx:$biometricVersion")

    // JodaTime
    val jodaVersion = "2.10.9.1"
    implementation("net.danlew:android.joda:$jodaVersion")

    // Glide
    val glideVersion = "4.12.0"
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    kapt("com.github.bumptech.glide:compiler:$glideVersion")

    // Timber
    val timberVersion = "4.7.1"
    implementation("com.jakewharton.timber:timber:$timberVersion")

    // App Startup
    val startupVersion = "1.0.0"
    implementation("androidx.startup:startup-runtime:$startupVersion")

    // Crypto
    val cryptoVersion = "1.0.0"
    implementation("androidx.security:security-crypto:$cryptoVersion")

    // Decoro
    val decoroVersion = "1.5.0"
    implementation("ru.tinkoff.decoro:decoro:$decoroVersion")

}