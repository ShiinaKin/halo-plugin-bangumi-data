import de.undercouch.gradle.tasks.download.Download
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.openapi.generator") version "7.14.0"
    id("de.undercouch.download") version "5.6.0"
}

group = "io.sakurasou.halo.bangumi"
version = "1.0.0-SNAPSHOT"

val openApiSpecUrl = "https://github.com/bangumi/api/raw/master/open-api/v0.yaml"
val openApiSpecFile = "${layout.projectDirectory}/src/main/resources/openapi/api.yml"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.contentNegotiation)
    implementation(libs.ktor.client.serialization.kotlinx)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.logging)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

tasks.register("generateBangumiApi") {
    group = "bangumi"
    dependsOn("process")
    finalizedBy("cleanUselessFile")
}

tasks.register<Download>("downloadOpenApiOfBangumi") {
    src(openApiSpecUrl)
    dest(openApiSpecFile)
    overwrite(true)
}

tasks.register("process") {
    dependsOn("downloadOpenApiOfBangumi")
    mustRunAfter("downloadOpenApiOfBangumi")
    finalizedBy("openApiGenerate")
}

openApiGenerate {
    generatorName.set("kotlin")
    inputSpec.set(openApiSpecFile)
    outputDir.set("$projectDir")
    apiPackage.set("io.sakurasou.halo.bangumi.api")
    modelPackage.set("io.sakurasou.halo.bangumi.model")
    invokerPackage.set("io.sakurasou.halo.bangumi.invoker")
    groupId.set("io.sakurasou.halo.bangumi")
    library.set("jvm-ktor")
}

tasks.register<Delete>("cleanUselessFile") {
    group = "clean"
    mustRunAfter("openApiGenerate")
    delete("$projectDir/build.gradle")
    delete("$projectDir/settings.gradle")
    delete("$projectDir/.gradle")
    delete("$projectDir/gradle")
    delete("$projectDir/gradlew")
    delete("$projectDir/gradlew.bat")
}