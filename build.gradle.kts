import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    alias(libs.plugins.kotlin)
    alias(libs.plugins.maven.publish)
    jacoco
}

group = property("GROUP") as String
version = property("VERSION_NAME") as String

gradlePlugin {
    website.set(property("POM_URL") as String)
    vcsUrl.set(property("POM_SCM_URL") as String)
    plugins {
        create("jacocoAndroidGradlePlugin") {
            id = "com.mxalbert.gradle.jacoco-android"
            implementationClass = "com.mxalbert.gradle.jacoco.android.JacocoAndroidPlugin"
            displayName = property("POM_NAME") as String
            description = property("POM_DESCRIPTION") as String
            tags.set(listOf("jacoco", "android", "plugin"))
        }
    }
}

repositories {
    google()
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = true
    }
}

val integrationTest by sourceSets.creating

val integrationTestTask = tasks.register<Test>("integrationTest") {
    group = "verification"
    testClassesDirs = integrationTest.output.classesDirs
    classpath = integrationTest.runtimeClasspath
    mustRunAfter(tasks.test)

    useJUnitPlatform()
    dependsOn(tasks.getByName("publishAllPublicationsToMavenRepository"))
    systemProperty("pluginVersion", version)
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = true
    }
}
tasks.check {
    dependsOn(integrationTestTask)
}

tasks.jacocoTestReport {
    reports {
        html.required.set(false)
        xml.required.set(true)
    }
}

dependencies {
    compileOnly(libs.agp.api)
    compileOnly(libs.kgp)

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.agp)
    testImplementation(libs.kgp)

    "integrationTestImplementation"(gradleTestKit())
    "integrationTestImplementation"(libs.junit.jupiter.api)
    "integrationTestRuntimeOnly"(libs.junit.jupiter.engine)
}

publishing {
    repositories {
        maven(url = "$buildDir/localMaven")
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.S01)
    signAllPublications()
}
