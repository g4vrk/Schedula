java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

dependencies {

    implementation(project(":common")) {
        exclude(group = "io.papermc.paper", module = "paper-api")
    }

    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")

}