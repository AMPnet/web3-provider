import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.4.32"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion

    id("org.springframework.boot") version "2.4.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.asciidoctor.convert") version "1.5.8"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
    id("io.gitlab.arturbosch.detekt").version("1.16.0")
    idea
    jacoco
}

group = "com.ampnet"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("redis.clients:jedis:3.6.0")
    implementation("com.github.AMPnet:jwt:0.2.0")

    implementation("com.github.briandilley.jsonrpc4j:jsonrpc4j:1.6")
    implementation("org.web3j:core:4.8.4") {
        exclude(group = "com.squareup.okhttp3")
    }
    implementation("io.github.microutils:kotlin-logging:2.0.5")
    runtimeOnly("javax.jws:jsr181-api:1.0-MR1")

    val okhttpVersion = "4.9.1"
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

detekt {
    input = files("src/main/kotlin")
    config = files("detekt-config.yml")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register<Copy>("copyDocs") {
    from(file("$buildDir/asciidoc/html5"))
    into(file("src/main/resources/static/docs"))
    dependsOn(tasks.asciidoctor)
}

jacoco.toolVersion = "0.8.6"
tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        xml.destination = file("$buildDir/reports/jacoco/report.xml")
        csv.isEnabled = false
        html.destination = file("$buildDir/reports/jacoco/html")
    }
    sourceDirectories.setFrom(listOf(file("${project.projectDir}/src/main/kotlin")))
    classDirectories.setFrom(fileTree("$buildDir/classes/kotlin/main"))
    dependsOn(tasks.test)
}
tasks.jacocoTestCoverageVerification {
    classDirectories.setFrom(sourceSets.main.get().output.asFileTree)
    violationRules {
        rule {
            limit {
                minimum = "0.7".toBigDecimal()
            }
        }
    }
    mustRunAfter(tasks.jacocoTestReport)
}

task("qualityCheck") {
    dependsOn(tasks.ktlintCheck, tasks.detekt, tasks.jacocoTestReport, tasks.jacocoTestCoverageVerification)
}
