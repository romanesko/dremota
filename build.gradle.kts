val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.0.21"
    id("io.ktor.plugin") version "3.0.1"
    kotlin("plugin.serialization") version "1.4.21"
    id("app.cash.sqldelight") version "2.0.2"


}

kotlin {
    jvmToolchain(17)
}

group = "dremota"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {

    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-cors-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-status-pages")


    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.2")
    implementation("io.ktor:ktor-client-okhttp:3.0.1")


    implementation("app.cash.sqldelight:gradle-plugin:2.0.2")
//    implementation("org.xerial:sqlite-jdbc:3.47.0.0")
    implementation("app.cash.sqldelight:sqlite-driver:2.0.2")

    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")


    implementation("org.telegram:telegrambots-longpolling:7.10.0")
    implementation("org.telegram:telegrambots-client:7.10.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    implementation("com.github.jknack:handlebars:4.4.0")

}

tasks.register<Exec>("compileSvelte") {
    doFirst { println("Compiling Svelte Project...") }
    doFirst {
        val baseUrl = System.getenv("BASE_URL")
        if (baseUrl == null) {
            println("Warning: BASE_URL is not set.")
        } else {
            println("Compiling Svelte Project with BASE_URL: $baseUrl")
        }
    }
    commandLine("./npm.build.sh", System.getenv("BASE_URL"))
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("dremota")
        }
    }
}

