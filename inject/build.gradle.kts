val asmVersion = project.property("asmVersion") as String

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