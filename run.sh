#!/bin/bash
git status
read -n 1 -p "Press any key to continue..."
git pull
mvn test -Dtest=TrendFollowingStrategyTest
mvn spring-boot:run &