plugins {
    kotlin("jvm") version "2.0.21"
    id("application")
    id("com.github.johnrengelman.shadow") version "8.0.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("org.example.intersection.IntersectionMainKt")
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}