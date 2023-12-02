FROM eclipse-temurin:17.0.9_9-jdk
EXPOSE 8080:8080
ENV BLOBFORGE_VOLUME=/app/blobs
ENV BLOBFORGE_ADMIN_KEY=cZUUUkLtJmcjWEhLRlQ49KqKZH56vkPJ
RUN mkdir /app
COPY ./build/libs/*-all.jar /app/blobforge.jar
ENTRYPOINT ["java","-jar","/app/blobforge.jar"]