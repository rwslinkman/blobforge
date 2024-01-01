import org.jetbrains.kotlin.ir.backend.js.compile
import java.nio.file.Files

plugins {
    kotlin("jvm") version "1.9.21"
    id("io.ktor.plugin") version "2.3.6"
    kotlin("plugin.serialization") version "1.9.21"
}

group = "nl.rwslinkman"
version = project.findProperty("projVersion") ?: "0.0.0-SNAPSHOT"

application {
    mainClass.set("nl.rwslinkman.blobforge.ApplicationKt")
}

repositories {
    mavenCentral()
}

val ktorVersion = "2.2.4"
dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.4.12")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.21")
    testImplementation("io.mockk:mockk:1.13.8")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks.register("createReadableProjectVersionFile") {
    group = "compile"
    doLast {
        val propFilePath = this.project.file("src/main/resources/version.properties").absolutePath
        val propFile = File(propFilePath)
        if(propFile.exists()) {
            propFile.delete()
        }
        propFile.createNewFile()
        val projectVersion = rootProject.version.toString()

        propFile.writeText("version=$projectVersion")
    }
}

tasks.compileKotlin {
    dependsOn("createReadableProjectVersionFile")
}