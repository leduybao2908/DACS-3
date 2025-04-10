pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://repo.stringee.com/maven/releases/") }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://repo.stringee.com/maven/releases/") }
    }

    versionCatalogs {
        create("libs") {
            from(files("gradle/libs.versions.toml")) // ✅ Chỉ đúng tại đây!
        }
    }
}

rootProject.name = "DACS3"
include(":app")
