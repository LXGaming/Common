base {
    archivesName = "common"
}

publishing {
    publications {
        named<MavenPublication>("maven") {
            from(components["java"])
            pom {
                description = "Library with common utils"
            }
        }
    }
}