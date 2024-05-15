buildscript {
    dependencies {
        val kotlinVersion = "1.9.24"

        classpath(kotlin("gradle-plugin", version = kotlinVersion))
    }
}// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    val kotlinVersion = "1.9.24"
    val kspVersion = "$kotlinVersion-1.0.20"
    val gradlePlugin = "8.4.0"

    id("com.android.application") version gradlePlugin apply false
    id("com.android.library") version gradlePlugin apply false
    id("org.jetbrains.kotlin.android") version kotlinVersion apply false
    id("com.google.devtools.ksp") version kspVersion apply false
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("com.google.firebase.crashlytics") version "3.0.1" apply false
}
