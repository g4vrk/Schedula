pluginManagement {

    repositories {

        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
        mavenCentral()

    }

}

rootProject.name = "schedula"

include(
    "common",
    "multi",
    "platform:bukkit",
    "platform:folia",
    "platform:minestom",
    "platform:sponge",
    "platform:fabric"
)

dependencyResolutionManagement {

    versionCatalogs {

        create("libs") {

            from(files("libs.versions.toml"))

        }

    }

}
