include("common")

listOf(
    "hosting",
    "inject"
).forEach {
    include(it)
    findProject(":${it}")?.name = "common-${it}"
}