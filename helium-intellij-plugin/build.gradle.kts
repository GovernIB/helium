import org.jetbrains.intellij.platform.gradle.TestFrameworkType

/*
 * Plugin d'IntelliJ per editar definicions de procés BPMN de Helium.
 *
 * L'editor visual és el mateix de Helium (helium-back/src/main/jsapp/designer, bpmn-js + extensions de Helium):
 * es compila amb npm i s'empaqueta dins el plugin, que el mostra en un navegador JCEF. El catàleg de handlers
 * predefinits és el de helium-disseny.
 *
 * No forma part del build de Maven: es construeix amb Gradle, que és l'única via suportada per a plugins d'IntelliJ.
 */
plugins {
    java
    id("org.jetbrains.intellij.platform") version "2.20.0"
}

group = "es.caib.helium"
version = providers.gradleProperty("pluginVersion").get()

// IntelliJ 2026.x és de Java 25
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        val local = providers.gradleProperty("platformLocalPath")
        if (local.isPresent) {
            local(local.get())
        } else {
            create(providers.gradleProperty("platformType"), providers.gradleProperty("platformVersion"))
        }
        // PSI de Java per trobar els handlers propis del projecte
        bundledPlugin("com.intellij.java")
        // Navegador incrustat (JCEF): des de 2026.x és un plugin a part
        bundledPlugin("com.intellij.modules.jcef")
        testFramework(TestFrameworkType.Platform)
        // Fixtures amb PSI de Java per provar la cerca de handlers propis
        testFramework(TestFrameworkType.Plugin.Java)
    }
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.opentest4j:opentest4j:1.3.0")
}

intellijPlatform {
    // No hi ha formularis de Swing ni opcions de configuració: s'eviten tasques que arrenquen l'IDE en el build
    instrumentCode = false
    buildSearchableOptions = false
    pluginConfiguration {
        version = providers.gradleProperty("pluginVersion")
        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            untilBuild = provider { null }
        }
    }
}

// --- Editor visual de Helium i catàleg de handlers ----------------------------------------------------------------

val designerDir = layout.projectDirectory.dir("../helium-back/src/main/jsapp/designer")
val catalegHandlers = layout.projectDirectory.file("../helium-disseny/src/main/resources/handlersPredefinits.json")

/** Compila l'editor de Helium (npm run build). Amb -PsenseBuildDesigner es fa servir el dist/ que ja hi hagi. */
val buildDesigner = tasks.register<Exec>("buildDesigner") {
    description = "Compila l'editor BPMN de Helium (npm run build)"
    group = "build"
    workingDir = designerDir.asFile
    commandLine("npm", "run", "build")
    onlyIf { !providers.gradleProperty("senseBuildDesigner").isPresent }
    inputs.dir(designerDir.dir("src"))
    inputs.files(designerDir.file("package.json"), designerDir.file("index.html"), designerDir.file("vite.config.ts"))
    outputs.dir(designerDir.dir("dist"))
}

tasks.processResources {
    dependsOn(buildDesigner)
    // L'editor es serveix des del classpath amb la mateixa ruta base que a Helium (/helium2back/designer/)
    from(designerDir.dir("dist")) {
        into("designer")
    }
    from(catalegHandlers) {
        into("helium")
    }
}
