plugins {
    id("com.gradleup.shadow") version "9.6.1"
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

dependencies {

    implementation(project(":common")) {
        exclude(group = "io.papermc.paper", module = "paper-api")
    }

    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")

}

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        archiveClassifier.set("")
    }

    build {
        dependsOn(shadowJar)
    }
}

publishing {
    publications {
        create<MavenPublication>("shadow") {
            artifact(tasks.shadowJar)

            groupId = "com.g4vrk"
            artifactId = "schedula-folia"
            version = project.version.toString()
        }
    }
}