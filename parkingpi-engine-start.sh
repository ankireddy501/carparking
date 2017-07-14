#!/usr/bin/env bash
sudo java -jar /var/lib/parkingpi-application/parking-management-0.0.1-SNAPSHOT.jar --spring.config.location=/var/lib/parkingpi-application/parkingpi.properties &
sudo echo $! > /var/lib/parkingpi-application/piprocess.pid &