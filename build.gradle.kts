// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    extra["kotlinVersion"] = "1.5.30"
    extra["hiltVersion"] = "2.38.1"

    val gradleVersion = "7.0.1"
    val safeArgsVersion = "2.3.5"
    val gmsVersion = "4.3.8"

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:$gradleVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${rootProject.extra["kotlinVersion"]}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$safeArgsVersion")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${rootProject.extra["hiltVersion"]}")
        classpath("com.google.gms:google-services:$gmsVersion")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

extra["compileSdkVersion"] = 30
extra["buildToolsVersion"] = "30.0.3"
extra["minSdkVersion"] = 23
extra["targetSdkVersion"] = 30

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}