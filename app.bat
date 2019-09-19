@echo off
cd /d "%~dp0"
call mvn clean install
rem mvn spring-boot:run >> logs\app.log
start javaw -jar target\winw-game.war --spring.profiles.active=product
pause