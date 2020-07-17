xargs -a app.pid kill && rm app.pid
mvn -q spring-boot:run >> logs/app.log & echo $! > app.pid