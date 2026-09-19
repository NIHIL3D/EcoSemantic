FROM eclipse-temurin:8-jre-jammy
WORKDIR /data
COPY blazegraph.jar /opt/blazegraph.jar
EXPOSE 9999
CMD ["java", "-Xmx1g", "-Dfile.encoding=UTF-8", "-jar", "/opt/blazegraph.jar"]