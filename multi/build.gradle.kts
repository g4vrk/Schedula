java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

dependencies {

    implementation(project(":common"))
    implementation(project(":folia"))
    implementation(project(":bukkit"))

    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")

}