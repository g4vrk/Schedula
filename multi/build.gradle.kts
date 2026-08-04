plugins {
    id("com.gradleup.shadow") version "9.6.1"
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

dependencies {

    implementation(project(":common"))
    implementation(project(":folia"))
    implementation(project(":bukkit"))

    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")

}

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        dependsOn(
            project(":bukkit").tasks.named("shadowJar"),
            project(":folia").tasks.named("shadowJar")
        )
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
            artifactId = "schedula-multi"
            version = project.version.toString()
        }
    }
}