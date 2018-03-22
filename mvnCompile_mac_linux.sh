#!/bin/bash

cd application
mvn package
cd target
./chess-application-1.0.jar
