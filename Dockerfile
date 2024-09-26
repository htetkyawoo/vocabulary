FROM eclipse-temurin:17-alpine

ENV PROFILE=default
ENV ADMIN_EMAIL=admin@gmail.com
ENV ADMIN_USERNAME=root
ENV ADMIN_PASSWORD=admin
ENV ADMIN_GENDER=MALE
ENV DATASOURCE_PASSWORD=admin
ENV DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/vocabulary
ENV DATASOURCE_USERNAME=root
ENV MAIL_HOST=host.docker.internal
ENV MAIL_PORT=8025
ENV DEFAULT_PAGESIZE=20
ENV ALLOWED_METHODS="**"
ENV ALLOWED_HEADERS="*"
ENV ALLOWED_ORIGINS=http://localhost:3000
ENV RESET_CODE_DURATION=60
COPY ./target/*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Dadmin.account.gender=${ADMIN_GENDER}","-Dadmin.account.email=${ADMIN_EMAIL}", "-Dadmin.account.username=${ADMIN_USERNAME}", "-Dadmin.account.password=${ADMIN_PASSWORD}", "-Dreset.code.duration=${RESET_CODE_DURATION}","-Ddefault.pageSize=${DEFAULT_PAGESIZE}", "-Dsecurity.allowedMethods=${ALLOWED_METHODS}", "-Dsecurity.allowedHeaders=${ALLOWED_HEADERS}", "-Dsecurity.allowedOrigins=${ALLOWED_ORIGINS}","-Dspring.profiles.active=${PROFILE}","-Dspring.datasource.url=${DATASOURCE_URL}","-Dspring.datasource.username=${DATASOURCE_USERNAME}","-Dspring.datasource.password=${DATASOURCE_PASSWORD}","-Dspring.mail.host=${MAIL_HOST}","-Dspring.mail.port=${MAIL_PORT}","-jar","app.jar"]
