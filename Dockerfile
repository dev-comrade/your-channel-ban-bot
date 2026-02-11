FROM bellsoft/liberica-openjre-alpine:21 AS extractor
COPY target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM bellsoft/liberica-openjre-alpine:21
WORKDIR /app
RUN mkdir log && chown 33:33 log
RUN mkdir files && chown 33:33 files
COPY --from=extractor dependencies/ ./
COPY --from=extractor snapshot-dependencies/ ./
COPY --from=extractor spring-boot-loader/ ./
COPY --from=extractor application/ ./
USER 33
VOLUME /app/log
VOLUME /app/files

CMD ["java", "org.springframework.boot.loader.launch.JarLauncher"]
