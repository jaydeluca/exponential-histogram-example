plugins {
    kotlin("jvm") version "1.9.21"
}

group = "org.histograms"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    implementation(platform("io.opentelemetry:opentelemetry-bom:1.34.1"))
    implementation(platform("io.opentelemetry:opentelemetry-bom-alpha:1.34.1-alpha"))
    implementation("io.opentelemetry:opentelemetry-api")
    implementation("io.opentelemetry:opentelemetry-sdk")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}