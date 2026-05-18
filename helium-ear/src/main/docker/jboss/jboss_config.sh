#!/bin/sh

CONFIG_STANDALONE_FILE=$JBOSS_HOME/standalone/configuration/standalone.xml
SCRIPT_DIR=`dirname -- "$0"`
DATASOURCES_FILE=$SCRIPT_DIR/datasources.xml
SYSTEM_PROPS_FILE=$SCRIPT_DIR/system-props.xml
KEYCLOAK_FILE=$SCRIPT_DIR/keycloak.xml
LOGGING_FILE=$SCRIPT_DIR/logging.xml
MAIL_FILE=$SCRIPT_DIR/mail.xml
TEMP_PROPS_FILE=$SCRIPT_DIR/jboss_properties.tmp
JBOSS_PROPS_FILE=/home/jboss/apps/helium/helium.properties
JBOSS_SYSTEM_PROPS_FILE=/home/jboss/apps/helium/helium-system.properties

if ! grep -q "<system-properties" $CONFIG_STANDALONE_FILE; then
	echo "Configuració inicial de JBoss..."
	sed '/<\/extensions>/a <system-properties\/>' -i $CONFIG_STANDALONE_FILE
	sed '/<\/socket-binding-group>/i <socket-binding name="proxy-https" port="443"\/>' -i $CONFIG_STANDALONE_FILE
	sed '/<\/socket-binding-group>/i <outbound-socket-binding name="smtp-helium" source-port="0" fixed-source-port="false"><remote-destination host="${env.JBOSS_MAIL_HOST}" port="${env.JBOSS_MAIL_PORT}"\/><\/outbound-socket-binding>' -i $CONFIG_STANDALONE_FILE
	sed -i 's/<http-listener name="default" socket-binding="http" redirect-socket="https" enable-http2="true"\/>/<http-listener name="default" socket-binding="http" proxy-address-forwarding="true" redirect-socket="proxy-https"\/>/g' $CONFIG_STANDALONE_FILE
	echo "...configuració inicial de JBoss finalitzada"
fi

echo "Modificant fitxer de configuració de JBoss $CONFIG_STANDALONE_FILE..."
sed -i '/<system-properties>/,/<\/system-properties>/c \<system-properties\/>' $CONFIG_STANDALONE_FILE
sed -e '/<system-properties\/>/ {' -e "r $SYSTEM_PROPS_FILE" -e 'd' -e '}' -i $CONFIG_STANDALONE_FILE
sed -i '/<subsystem xmlns="urn:jboss:domain:datasources:5.0">/,/<\/subsystem>/c \<subsystem xmlns="urn:jboss:domain:datasources:5.0"\/>' $CONFIG_STANDALONE_FILE
sed -e '/<subsystem xmlns="urn:jboss:domain:datasources:5.0"\/>/ {' -e "r $DATASOURCES_FILE" -e 'd' -e '}' -i $CONFIG_STANDALONE_FILE
sed -i '/<subsystem xmlns="urn:jboss:domain:keycloak:1.1">/,/<\/subsystem>/c \<subsystem xmlns="urn:jboss:domain:keycloak:1.1"\/>' $CONFIG_STANDALONE_FILE
sed -e '/<subsystem xmlns="urn:jboss:domain:keycloak:1.1"\/>/ {' -e "r $KEYCLOAK_FILE" -e 'd' -e '}' -i $CONFIG_STANDALONE_FILE
sed -i '/<subsystem xmlns="urn:jboss:domain:logging:6.0">/,/<\/subsystem>/c \<subsystem xmlns="urn:jboss:domain:logging:6.0"\/>' $CONFIG_STANDALONE_FILE
sed -e '/<subsystem xmlns="urn:jboss:domain:logging:6.0"\/>/ {' -e "r $LOGGING_FILE" -e 'd' -e '}' -i $CONFIG_STANDALONE_FILE
sed -i '/<subsystem xmlns="urn:jboss:domain:mail:3.0">/,/<\/subsystem>/c \<subsystem xmlns="urn:jboss:domain:mail:3.0"\/>' $CONFIG_STANDALONE_FILE
sed -e '/<subsystem xmlns="urn:jboss:domain:mail:3.0"\/>/ {' -e "r $MAIL_FILE" -e 'd' -e '}' -i $CONFIG_STANDALONE_FILE
echo "...fitxer de configuració de JBoss modificat"

echo "Modificant fitxers de properties per incorporar variables d'entorn..."
awk -F '=' 'NF {if (ENVIRON[$1]) {print $1 "=" ENVIRON[$1]} else {print $1 "=" $2}}' $JBOSS_PROPS_FILE > $TEMP_PROPS_FILE && mv $TEMP_PROPS_FILE $JBOSS_PROPS_FILE
#unset `awk -F '=' '{print $1}' $JBOSS_PROPS_FILE | tr '\n' ' '`
awk -F '=' 'NF {if (ENVIRON[$1]) {print $1 "=" ENVIRON[$1]} else {print $1 "=" $2}}' $JBOSS_SYSTEM_PROPS_FILE > $TEMP_PROPS_FILE && mv $TEMP_PROPS_FILE $JBOSS_SYSTEM_PROPS_FILE
#unset `awk -F '=' '{print $1}' $JBOSS_SYSTEM_PROPS_FILE | tr '\n' ' '`
echo "...fitxers de properties modificats"
