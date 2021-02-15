import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar


val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val koin_version: String by project
val exposed_version: String by project
val valiktor_version: String by project
val flyway_version: String by project
val hikari_version: String by project
val postgreSql_version: String by project
val h2_version: String by project
val mysql_connection_version: String by project
val bcrypt_version: String by project

plugins {
    application
    kotlin("jvm") version "1.4.10"

    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "com.skyz"
version = "0.0.1"


application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    maven { url = uri("https://plugins.gradle.org/m2/") }
}

dependencies {
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-gson:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("org.koin:koin-ktor:$koin_version")
    implementation("org.koin:koin-logger-slf4j:$koin_version")

    implementation("org.postgresql:postgresql:$postgreSql_version")
    implementation("com.zaxxer:HikariCP:$hikari_version")
    implementation("org.flywaydb:flyway-core:$flyway_version")

    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")

    implementation ("com.h2database:h2:$h2_version")
    implementation ("mysql:mysql-connector-java:$mysql_connection_version")

    testImplementation("io.ktor:ktor-server-tests:$ktor_version")

    implementation ("org.mindrot:jbcrypt:$bcrypt_version")
}