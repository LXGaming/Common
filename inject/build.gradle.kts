val asmVersion: String by project

base {
    archivesName = "common-inject"
}

dependencies {
    testImplementation("org.ow2.asm:asm:${asmVersion}")
}

mavenPublishing {
    pom {
        description.set("Lightweight* dependency injection framework for Java")
    }
}