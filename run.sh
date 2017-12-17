#!/bin/bash
git pull
mvn test -Dtest=TrendFollowingStrategyTest
mvn spring-boot:run &