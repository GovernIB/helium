# Helium BPMN per a IntelliJ

Plugin d'IntelliJ IDEA per editar les definicions de procés BPMN 2.0 de Helium amb el **mateix editor visual que
Helium** (`helium-back/src/main/jsapp/designer`: bpmn-js, plafó de propietats i extensions de Helium).

## Què fa

- Afegeix una pestanya **Disseny** als fitxers `.bpmn` i `.bpmn20.xml`, davant de la pestanya de text (XML).
- El document del fitxer és la font de veritat:
  - cada canvi del diagrama s'escriu al document;
  - si el document canvia per una altra via (pestanya de text, control de versions...), el diagrama es torna a carregar;
  - desar, desfer i refer són els de l'IDE (Ctrl+Z / Ctrl+Shift+Z dins el diagrama desfan el document).
- Amb un tema fosc, l'editor fa servir els colors de l'esquema de l'editor de l'IDE. Si canvia el tema es torna a carregar
  sol, conservant la vista. Els colors no s'escriuen al fitxer.
- Handlers de les service tasks:
  - **predefinits**: el `handlersPredefinits.json` del `helium-disseny` del projecte, o el que porta el plugin si el
    projecte no en té;
  - **propis**: les classes del projecte que implementen `es.caib.helium.bpmn.handler.HeliumBpmnHandler`, amb els seus
    camps com a paràmetres. S'actualitzen sols quan canvia el codi del projecte (un handler nou, un camp nou...), sense
    haver de tornar a obrir el diagrama.

## Com funciona

| Peça | Què fa |
|---|---|
| `HeliumBpmnEditorProvider` | Registra la pestanya per als fitxers BPMN (`FitxersBpmn`). |
| `HeliumBpmnEditor` | Navegador JCEF amb l'editor; sincronitza el document i l'editor i aplica desfer/refer. |
| `RecursosEditor`, `PeticionsEditor` | Serveixen l'editor des del classpath del plugin a l'origen fictici `http://helium-designer.localhost/helium2back/designer/`, sense xarxa. La pàgina d'entrada es genera a partir del manifest de Vite. |
| `CatalegHandlers` | Catàleg de handlers predefinits i propis (PSI de Java). |

L'editor web sap que s'executa dins l'IDE per `window.__APP_CONFIG__.mode = 'ide'`. El protocol de missatges entre
l'editor i el plugin està descrit a `designer/src/ide.ts`.

## Compilar i provar

Cal un JDK 25 (el build el troba com a toolchain de Gradle), Node.js/npm (per compilar l'editor) i accés a Internet
per a les dependències de Gradle.

```bash
cd helium-intellij-plugin

# Contra un IntelliJ instal·lat (no el descarrega)
./gradlew buildPlugin -PplatformLocalPath=/ruta/a/intellij-idea

# Tests
./gradlew test -PplatformLocalPath=/ruta/a/intellij-idea

# IntelliJ de prova amb el plugin instal·lat
./gradlew runIde -PplatformLocalPath=/ruta/a/intellij-idea
```

- Sense `-PplatformLocalPath`, Gradle descarrega l'IntelliJ indicat a `gradle.properties` (`platformType`,
  `platformVersion`).
- `buildPlugin` compila l'editor amb `npm run build`. Amb `-PsenseBuildDesigner` es fa servir el `dist/` de l'editor
  que ja hi hagi.
- El plugin queda a `build/distributions/helium-intellij-plugin-<versió>.zip`. Per distribuir-lo, vegeu *Publicar una
  versió nova*.

## Instal·lar

El plugin es distribueix des d'un repositori de plugins propi: el directori `dist/` d'aquest mòdul publicat a la
branca `helium-4.0` de GitHub. Així IntelliJ sap d'on ve el plugin (sense l'avís *Cannot update plugin*) i n'instal·la
sol les versions noves.

1. *Settings → Plugins → ⚙ → Manage Plugin Repositories...*, afegir
   `https://raw.githubusercontent.com/GovernIB/helium/helium-4.0/helium-intellij-plugin/dist/updatePlugins.xml`
2. A la pestanya *Marketplace* de *Plugins*, cercar **Helium BPMN**, instal·lar-lo i reiniciar l'IDE.

Si ja estava instal·lat des del disc, n'hi ha prou amb afegir el repositori: IntelliJ hi troba el plugin en arrencar i
el fa servir com a origen de les actualitzacions.

Sense repositori també es pot instal·lar el ZIP de `dist/` amb *⚙ → Install Plugin from Disk...*, però llavors
IntelliJ avisa que no pot actualitzar-lo.

Cal IntelliJ IDEA 2026.3 o posterior amb el runtime per defecte (JetBrains Runtime, que porta JCEF) i el plugin Java
activat. No cal compilar res ni tenir Node.

## Publicar una versió nova

1. Augmentar `pluginVersion` a `gradle.properties` (no pot ser `SNAPSHOT`: IntelliJ compara versions per saber si hi
   ha una actualització).
2. `./gradlew distribuir -PplatformLocalPath=/ruta/a/intellij-idea`: compila el plugin, en copia el ZIP a `dist/`
   (esborrant l'anterior) i genera `dist/updatePlugins.xml` amb la versió i la URL de `pluginRepositoryUrl`.
3. Fer commit de `dist/` i portar-lo a la branca `helium-4.0` de GitHub. Fins llavors la URL del repositori no té la
   versió nova.

`RepositoriPluginsTest` comprova que IntelliJ pot llegir `dist/updatePlugins.xml` i que el ZIP que anuncia és a `dist/`.

## Limitacions

- Requereix IntelliJ IDEA 2026.3 o posterior (`pluginSinceBuild`). Per suportar versions anteriors cal compilar contra la
  més antiga i comprovar-ho amb `verifyPlugin`.
- No es desplega a Helium des de l'IDE: el fitxer es puja des de Helium com fins ara.
- No hi ha navegació del diagrama al codi (classe del handler) ni validació o autocompleció de les expressions.
