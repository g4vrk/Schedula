rootProject.name = "schedula"

include(
    "common",
    "bukkit",
    "folia",
    "multi",
    "minestom",
    "sponge"
)

dependencyResolutionManagement {

    versionCatalogs {

        create("libs") {

            from(files("libs.versions.toml"))

        }

    }

}
