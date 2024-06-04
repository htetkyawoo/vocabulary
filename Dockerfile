FROM eclipse-temurin:17-alpine

ARG PROFILE
ARG DATASOURCE_URL
ARG DATASOURCE_PASSWORD
ARG DATASOURCE_USERNAME
ARG MAIL_HOST
ARG MAIL_PORT

ENV proifle=$PROFILE
ENV datasource_password=$DATASOURCE_PASSWORD
ENV datasource_url=jdbc:postgresql://$DATASOURCE_URL
ENV datasource_username=$DATASOURCE_USERNAME
ENV mail_host=$MAIL_HOST
ENV mail_port=$MAIL_PORT
COPY ./target/*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Dspring.profiles.active=${proifle}","-Dspring.datasource.url=${datasource_url}","-Dspring.datasource.username=${datasource_username}","-Dspring.datasource.password=${datasource_password}","-Dspring.mail.host=${mail_host}","-Dspring.mail.port=${mail_port}","-jar","app.jar"]
