plugins {
    id("java-library")
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

subprojects {
    apply(plugin = "java-library")

    java {
        toolchain.languageVersion = JavaLanguageVersion.of(17)
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:26.0.2")
        compileOnly("org.projectlombok:lombok:1.18.42")
        annotationProcessor("org.projectlombok:lombok:1.18.42")
    }
}