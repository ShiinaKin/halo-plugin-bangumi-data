import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlinxSerialization)
}

group = "io.sakurasou.halo.bangumi"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.client.core) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation(libs.ktor.client.cio) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation(libs.ktor.client.contentNegotiation) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation(libs.ktor.client.serialization.kotlinx) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_21)
    }
}