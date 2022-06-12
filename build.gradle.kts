@file:Suppress("SpellCheckingInspection", "PropertyName")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.0"
    id("org.cadixdev.licenser") version "0.6.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val KT_VER = "1.7.0"

group = "me.dkim19375"
version = "1.1.0"

val basePackage = "me.dkim19375.${project.name.toLowerCase()}.libs"
val fileName = tasks.shadowJar.get().archiveFileName.get()

tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

license {
    header.set(resources.text.fromFile(rootProject.file("HEADER")))
    include("**/*.kt")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://libraries.minecraft.net/")
    maven("https://repo.triumphteam.dev/releases/")
    maven("https://repo.triumphteam.dev/snapshots/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://s01.oss.sonatype.org/content/repositories/releases/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("com.mojang:brigadier:1.0.18")
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("org.jetbrains:annotations:23.0.0")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.8.0")
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

    implementation("net.kyori:adventure-api:4.11.0")
    implementation("dev.triumphteam:triumph-gui:3.1.2")
    implementation("net.kyori:adventure-platform-bukkit:4.1.1")
    implementation("me.mattstudios:triumph-config:1.0.5-SNAPSHOT")

    implementation("io.github.dkim19375:dkim-bukkit-core:3.3.40") {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$KT_VER") {
        exclude(module = "annotations")
    }
}

tasks.processResources {
    outputs.upToDateWhen { false }
    expand("pluginVersion" to project.version)
}

tasks.create("removeBuildJars") {
    doLast {
        File(project.rootDir, "build/libs").deleteRecursively()
    }
}

val server = "1.17"
val servers = setOf(
    "1.8",
    "1.16",
    "1.17",
    "1.18"
)

tasks.create("copyFile") {
    doLast {
        val jar = tasks.shadowJar.get().archiveFile.get().asFile
        val pluginFolder = file(rootDir).resolve("../.TestServers/${server}/plugins")
        if (pluginFolder.exists()) {
            jar.copyTo(File(pluginFolder, tasks.shadowJar.get().archiveFileName.get()), true)
        }
    }
}

tasks.create("deleteAll") {
    doLast {
        for (deleteServer in servers) {
            for (file in File("../.TestServers/${deleteServer}/plugins").listFiles() ?: emptyArray()) {
                if (file.name.startsWith(tasks.shadowJar.get().archiveBaseName.get())) {
                    file.delete()
                }
            }
        }
    }
}

val relocations = setOf(
    "kotlin",
    "kotlinx",
    "reactor",
    "net.kyori",
    "org.yaml.snakeyaml",
    "dev.triumphteam.gui",
    "org.reactivestreams",
    "me.mattstudios.config",
    "me.dkim19375.dkimcore",
    "org.jetbrains.annotations",
    "me.dkim19375.dkimbukkitcore",
    "org.intellij.lang.annotations",
)

tasks.shadowJar {
    relocations.forEach { name ->
        relocate(name, "${basePackage}.$name")
    }
    exclude("DebugProbesKt.bin")
    mergeServiceFiles()
    finalizedBy(tasks.getByName("copyFile"))
}