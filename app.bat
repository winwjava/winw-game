@echo off
cd /d "%~dp0"
mvn clean install
rem mvn spring-boot:run >> logs\app.log
rem start javaw -jar target\winw-game.war --spring.profiles.active=product