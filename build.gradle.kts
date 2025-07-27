import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.2.0"
    alias(libs.plugins.kotlinxSerialization)
    id("run.halo.plugin.devtools") version "0.6.1"
}

group = "io.sakurasou.halo.bangumi"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("run.halo.tools.platform:plugin:2.21.0"))
    compileOnly("run.halo.app:api")
    implementation(project(":api"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines)
    compileOnly(libs.kotlinx.coroutines.reactor)
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
    implementation(libs.logging)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.register<Copy>("processUiResources") {
    from(project(":ui").layout.buildDirectory.dir("dist"))
    into(layout.buildDirectory.dir("resources/main/console"))
    dependsOn(project(":ui").tasks.named("assemble"))
    shouldRunAfter(tasks.named("processResources"))
}

tasks.named("classes") {
    dependsOn(tasks.named("processUiResources"))
}

halo {
    version = "2.21"
}