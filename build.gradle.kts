buildscript {
    dependencies {
        val kotlinVersion = "1.9.22"

        classpath(kotlin("gradle-plugin", version = kotlinVersion))
    }
}// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    val kotlinVersion = "1.9.22"
    val kspVersion = "$kotlinVersion-1.0.17"
    val gradlePlugin = "8.3.0"

    id("com.android.application") version gradlePlugin apply false
    id("com.android.library") version gradlePlugin apply false
    id("org.jetbrains.kotlin.android") version kotlinVersion apply false
    id("com.google.devtools.ksp") version kspVersion apply false
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
}
