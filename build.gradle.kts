import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.8.10"
    id("io.ktor.plugin") version "2.2.3"
}

group = "llesha"
version = "1.0.0"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("com.github.h0tk3y.betterParse:better-parse:0.4.4")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core-jvm:2.3.4")
    implementation("io.ktor:ktor-network-tls-jvm:2.3.4")
    implementation("io.ktor:ktor-server-cors-jvm:2.3.4")
    implementation("io.ktor:ktor-server-sessions-jvm:2.3.4")
    implementation("io.ktor:ktor-server-auth-jvm:2.3.4")
    implementation("io.ktor:ktor-server-auth-jwt:2.3.4")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.4")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.4")
    implementation("io.ktor:ktor-server-html-builder:2.3.4")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.4")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}

tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>("compileKotlin").configure {
    compilerOptions.freeCompilerArgs.add("-opt-in=kotlin.contracts.ExperimentalContracts")
}