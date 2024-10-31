val asmVersion: String by project

base {
    archivesName = "common-inject"
}

dependencies {
    testImplementation("org.ow2.asm:asm:${asmVersion}")
}

publishing {
    publications {
        named<MavenPublication>("maven") {
            from(components["java"])
            pom {
                description = "Lightweight* dependency injection framework for Java"
            }
        }
    }
}