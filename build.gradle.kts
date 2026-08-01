plugins {
    id("java-library")
    id("maven-publish")
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    group = rootProject.group
    version = rootProject.version

    java {
        toolchain.languageVersion = JavaLanguageVersion.of(17)
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:26.0.2")
        compileOnly("org.projectlombok:lombok:1.18.42")
        annotationProcessor("org.projectlombok:lombok:1.18.42")
    }
}