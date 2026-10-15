val slf4jVersion = project.property("slf4jVersion") as String

base {
    archivesName = "common-hosting"
}

dependencies {
    api(project(path = ":common-inject"))
    api("org.slf4j:slf4j-api:${slf4jVersion}")
    testImplementation("org.slf4j:slf4j-simple:${slf4jVersion}")
}

mavenPublishing {
    pom {
        description.set("Hosting framework for Java")
    }
}

tasks.compileJava {
    dependsOn(":common-inject:build")
}