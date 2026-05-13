import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("java")
    id("signing")
    id("com.vanniktech.maven.publish") version "0.36.0"
}

subprojects {
    apply(plugin = "com.vanniktech.maven.publish")
    apply(plugin = "java-library")
    apply(plugin = "signing")

    val annotationsVersion: String by project
    val junitVersion: String by project

    group = "io.github.lxgaming"

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("org.jetbrains:annotations:${annotationsVersion}")
        testImplementation("org.junit.jupiter:junit-jupiter:${junitVersion}")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    mavenPublishing {
        publishToMavenCentral()
        signAllPublications()

        pom {
            name.set("Common")
            url.set("https://github.com/LXGaming/Common")
            developers {
                developer {
                    id.set("lxgaming")
                    name.set("LXGaming")
                }
            }
            issueManagement {
                system.set("GitHub Issues")
                url.set("https://github.com/LXGaming/Common/issues")
            }
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            scm {
                connection.set("scm:git:https://github.com/LXGaming/Common.git")
                developerConnection.set("scm:git:https://github.com/LXGaming/Common.git")
                url.set("https://github.com/LXGaming/Common")
            }
        }
    }

    signing {
        val signingKey: String? by project
        val signingPassword: String? by project
        if (signingKey != null && signingPassword != null) {
            useInMemoryPgpKeys(signingKey, signingPassword)
        }
    }

    tasks.javadoc {
        isFailOnError = false
        options {
            this as CoreJavadocOptions

            addStringOption("Xdoclint:none", "-quiet")
        }
    }

    tasks.processResources {
        from("../LICENSE") {
            into("META-INF")
            rename { "${it}-Common" }
        }
    }

    tasks.test {
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
        }

        useJUnitPlatform()
    }
}

mavenPublishing {
    configure(JavaLibrary(
        javadocJar = JavadocJar.None(),
        sourcesJar = false
    ))
}

tasks.jar {
    enabled = false
}