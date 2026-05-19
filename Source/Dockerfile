FROM 177890020059.dkr.ecr.mx-central-1.amazonaws.com/goldenimages/giattol9jdk21-slim:20251006211231
USER root
WORKDIR /opt/apps

# Combinar los comandos mkdir y chown en un solo RUN
RUN mkdir -p /opt/apps/ms-api-tmf632-party-mgt /opt/apps/logs/ms-api-tmf632-party-mgt \
    && chown -R usrapp:usrapp /opt/apps/logs/ms-api-tmf632-party-mgt

# Cambia a Agreement Management
WORKDIR /opt/apps/ms-api-tmf632-party-mgt
COPY boot/target/*.jar ms-api-tmf632-party-mgt.jar

USER usrapp
EXPOSE 16600
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-jar","/opt/apps/ms-api-tmf632-party-mgt/ms-api-tmf632-party-mgt.jar"]
