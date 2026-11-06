plugins {
    kotlin("jvm") version "2.4.20"
    id("org.jetbrains.intellij.platform") version "2.20.0"
}

group = "com.github.leafhighlighting"
version = "0.6.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdea("2026.2.3")
        bundledModule("intellij.xml.psi")
    }

    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
}

intellijPlatform {
    pluginVerification {
        ides {
            recommended()
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }

    patchPluginXml {
        sinceBuild.set("262")
    }

}