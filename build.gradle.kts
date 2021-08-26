// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    val hiltVersion = "2.36"
    val kotlinVersion = "1.5.21"
    val gradleVersion = "7.0.1"
    val safeArgsVersion = "2.4.0-alpha04"
    val gmsVersion = "4.3.8"

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:$gradleVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$safeArgsVersion")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
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

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}