plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
    id("maven-publish")
    id("io.freefair.lombok") version "8.6"
    id("io.papermc.paperweight.userdev") version "1.7.1"
}

apply(plugin = "java")
apply(plugin = "io.freefair.lombok")
apply(plugin = "maven-publish")

group = "com.wizardlybump17.custom-world-manager"
version = "0.1.0"

repositories {
    mavenCentral()

    maven {
        url = uri("https://maven.pkg.github.com/WizardlyBump17/WLib")
        credentials {
            username = (project.findProperty("gpr.user") ?: System.getenv("USERNAME")) as String
            password = (project.findProperty("gpr.key") ?: System.getenv("TOKEN")) as String
        }
    }
}

val lombok = "1.18.32"
val jetbrainsAnnotations = "24.1.0"
val paper = "1.20.1-R0.1-20230921.165944-178"
val wlib = "1.6.2"

dependencies {
    compileOnly("org.projectlombok:lombok:${lombok}")
    compileOnly("org.jetbrains:annotations:${jetbrainsAnnotations}")
    annotationProcessor("org.projectlombok:lombok:${lombok}")

    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:${paper}")

    compileOnly("com.wizardlybump17.wlib:bukkit-utils:${wlib}")
    compileOnly("com.wizardlybump17.wlib:utils:${wlib}")
    compileOnly("com.wizardlybump17.wlib:core:${wlib}")
    compileOnly("com.wizardlybump17.wlib:objects:${wlib}")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    javadoc {
        dependsOn(getTasksByName("delombok", false))
        options {
            this as StandardJavadocDocletOptions
            addBooleanOption("Xdoclint:none", true)
            addStringOption("Xmaxwarns", "1")
        }
    }

    assemble {
        dependsOn(reobfJar)
    }
}
