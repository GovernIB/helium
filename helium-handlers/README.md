# Arquetipus per crear handlers per tipus d'expedients per estat de Helium

**Crear un nou projecte de handlers**

Per crear un nou projecte de handlers cal executar la següent comanda de maven, substituint els valors de -DdefaultVariablePrefix i -DdefaultClassPrefix pels valors desitjats:

```
mvn archetype:generate -DarchetypeGroupId=es.caib.helium.handlers -DarchetypeArtifactId=helium-handlers-archetype -DarchetypeVersion=0.1 -DdefaultVariablePrefix=Demo -DdefaultClassPrefix=Demo
```

**Generar l'arquetipus**

Per a generar l'arquetipus a partir del projecte cal executar la següent comanda maven:

```
mvn -U clean archetype:create-from-project -Dinteractive=false -DkeepParent=true -DpropertyFile=archetype.properties -DpackageName=es.caib.helium.handlers -Darchetype.filteredExtensions=java,xml,md
```