pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/Truphone/esim-sdk-android-kotlin")

            credentials {
                username = "GITHUB_USER" // Your GitHub username
                password = "GITHUB_USER_TOKEN" // Your GitHub token
            }
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "esim sdk example"
include(":example-app")
