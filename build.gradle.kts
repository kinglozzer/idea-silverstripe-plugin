import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.changelog.Changelog

plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.15.0"
    id("org.jetbrains.changelog") version "2.1.2"
}

group = "com.kinglozzer"
version = "1.0.4"

sourceSets {
    main {
        java.srcDirs("src/main/java", "src/main/gen")
    }
}

repositories {
    mavenCentral()
}

intellij {
    version.set("2022.3")
}

tasks {
    test {
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
        }
    }

    patchPluginXml {
        untilBuild.set("")

        changeNotes.set(provider {
            changelog.renderItem(
                changelog
                    .getLatest()
                    .withHeader(false)
                    .withEmptySections(false),
                Changelog.OutputType.HTML
            )
        })
    }
}

changelog {
    version.set("1.0.4")
    path.set(file("CHANGELOG.md").canonicalPath)
    header.set(provider { "${version.get()}" })
    itemPrefix.set("-")
    keepUnreleasedSection.set(true)
    unreleasedTerm.set("[Unreleased]")
    groups.set(listOf("Added", "Changed", "Deprecated", "Removed", "Fixed"))
}
