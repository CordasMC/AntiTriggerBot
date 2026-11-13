plugins {
    `java-library`
    alias(libs.plugins.run.paper)
}

group = "dev.jstus"
version = "1.0"

repositories {
    mavenLocal()
    mavenCentral()

    // PaperMC
    maven("https://repo.papermc.io/repository/maven-public/")

    // CommandAPI & PacketEvents
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
}

dependencies {
    compileOnly(libs.paper.api)

    // Loaded at runtime by the plugin loader (see paper-libraries.json)
    compileOnly(libs.commandapi)

    // Provided by the PacketEvents plugin
    compileOnly(libs.packetevents)
}

tasks {
    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
    }

    withType<Javadoc>() {
        options.encoding = Charsets.UTF_8.name()
    }

    runServer {
        minecraftVersion("1.21.1")
    }

    jar {
        archiveFileName = "${rootProject.name}-${project.version}.jar"

        manifest {
            attributes["Implementation-Version"] = rootProject.version
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
    }

    defaultTasks("build")
}
