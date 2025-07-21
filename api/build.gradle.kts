import de.undercouch.gradle.tasks.download.Download

plugins {
    id("org.openapi.generator") version "7.14.0"
    id("de.undercouch.download") version "5.6.0"
}

group = "io.sakurasou.halo.bangumi"
version = "1.0.0-SNAPSHOT"

val openApiSpecUrl = "https://github.com/bangumi/api/raw/master/open-api/v0.yaml"
val openApiSpecFile = "${layout.projectDirectory}/src/main/resources/openapi/v0.yaml"

repositories {
    mavenCentral()
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