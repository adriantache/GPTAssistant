buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.9.10"))
    }
}// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0-beta06" apply false
    id("com.android.library") version "8.2.0-beta06" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    kotlin("jvm") version "1.9.10" apply false
    id("com.google.gms.google-services") version "4.3.15" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version ("1.9.0") apply false
}
