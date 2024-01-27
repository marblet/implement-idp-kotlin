import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.jlleitschuh.gradle.ktlint") version "12.0.3"
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.spring") version "1.9.21"
}

group = "com.marblet"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // arrow-kt
    implementation("io.arrow-kt:arrow-core:1.2.0")
    implementation("io.arrow-kt:arrow-fx-coroutines:1.2.0")

    // Generate openapi.yaml
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    // ORMapper
    implementation("org.jetbrains.exposed:exposed-spring-boot-starter:0.45.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.45.0")
    implementation("mysql:mysql-connector-java:8.0.33")

    // password hashing
    implementation("org.springframework.security:spring-security-crypto:6.0.3")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.77")

    // thymeleaf
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // JWT
    implementation("com.auth0:java-jwt:4.4.0")

    // test
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
