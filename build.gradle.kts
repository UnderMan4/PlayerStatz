import com.github.gradle.node.npm.task.NpxTask
import java.io.IOException

plugins {
    id("java")
    id("com.github.node-gradle.node") version "5.0.0"
}

group = "pl.underman.playerstatz"
version = "0.1"

repositories {
    mavenCentral()
    maven {
        setName("papermc-repo")
        setUrl("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
       setName("sonatype")
        setUrl("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
}

node {
    download.set(true)
    nodeProjectDir.set(file("webapp/"))
}

val targetJavaVersion = 17
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks.withType(JavaCompile::class).configureEach {
    options.apply {
        encoding = "utf-8"
    }
}

val yarn = tasks.named("yarn") {
    dependsOn(tasks.npmInstall)
}

val buildWebapp = tasks.register<NpxTask>("buildWebapp") {
    doFirst {
        if (!file("webapp/dist/").deleteRecursively())
            throw IOException("Failed to delete build directory!")
    }
    
    command.set("vite")
    args.set(listOf("build"))
    dependsOn(yarn)
    inputs.dir("webapp/")
    outputs.dir("webapp/dist/")
}

val zipWebapp = tasks.register<Zip>("zipWebapp") {
    dependsOn(buildWebapp)
    from(fileTree("webapp/dist/"))
    archiveFileName.set("webapp.zip")
    destinationDirectory.set(file("src/main/resources/"))
    
    inputs.dir("webapp/dist/")
    outputs.file(file("src/main/resources/webapp.zip"))
}

tasks.processResources {
    dependsOn(zipWebapp)
    filesMatching("plugin.yml") {
        expand("version" to version)
    }
}
