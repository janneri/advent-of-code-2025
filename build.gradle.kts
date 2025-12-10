plugins {
    kotlin("jvm") version "2.2.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ksmtVersion = "0.6.4"

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ksmt:ksmt-core:${ksmtVersion}")
    implementation("io.ksmt:ksmt-z3:${ksmtVersion}")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}