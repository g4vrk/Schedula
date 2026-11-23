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
    "bukkit",
    "folia",
    "multi",
    "minestom",
    "sponge",
    "fabric"
)

dependencyResolutionManagement {

    versionCatalogs {

        create("libs") {

            from(files("libs.versions.toml"))

        }

    }

}
