val slf4jVersion: String by project

base {
    archivesName = "common-hosting"
}

dependencies {
    api(project(path = ":common-inject"))
    api("org.slf4j:slf4j-api:${slf4jVersion}")
    testImplementation("org.slf4j:slf4j-simple:${slf4jVersion}")
}

publishing {
    publications {
        named<MavenPublication>("maven") {
            from(components["java"])
            pom {
                description = "Hosting framework for Java"
            }
        }
    }
}

tasks.compileJava {
    dependsOn(":common-inject:build")
}