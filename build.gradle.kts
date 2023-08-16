import com.github.gradle.node.npm.task.NpxTask
import java.io.IOException

plugins {
    id("java")
    id("com.github.node-gradle.node") version "5.0.0"
    id("co.uzzu.dotenv.gradle") version "2.0.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "pl.underman.playerstatz"
version = "0.1"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        setUrl("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        setUrl("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")

    // https://mvnrepository.com/artifact/com.h2database/h2
    implementation("com.h2database:h2:2.1.210")

    // https://mvnrepository.com/artifact/org.hibernate/hibernate-core
    implementation("org.hibernate:hibernate-core:6.2.7.Final")

    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")

    testCompileOnly("org.projectlombok:lombok:1.18.28")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.28")


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
//    dependsOn(zipWebapp)
    filesMatching("plugin.yml") {
        expand("version" to version)
    }
}


val copyPlugin = tasks.register("copyPlugin", Copy::class) {

    if (env.SERVER_PATH.isPresent) {
        copy {
            from("${project.buildDir}/libs/${project.name}-${project.version}-all.jar")
            into(env.SERVER_PATH.value + "/plugins")
        }
    } else {
        throw IOException("SERVER_PATH not set!")
    }
}