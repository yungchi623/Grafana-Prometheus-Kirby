# Kirby+Prometheus+Grafana Dashboard

Kirby is a data collection service. It gets real-time status of ISP server, including GPN and AWS. ISP data is transferred to prometheus service by kirby RESTful APIs. Prometheus service is a monitoring system and time series database. It periodly pulls metrics from jobs and stores ISP data in database. Grafana service is a data visualizer which displays statistical diagram by using prometheus metrics.

# Kirby Remote Controller

User can remote control our NWARP functions by Kirby Remote Controller. It is established connection with NWARP by mqtt protocol.

environment setup:
* one server: ubuntu 18.04
* install docker and docker-compose
* please check server ip and change ip config in project files (MQTTConfig.java)
* build code: spring boot maven (Apache Maven 3.6.3, jdk-11.0.5)

Step 1. Build kirby
		=> Go to kirby folder
		=> mvn clean
		=> mvn package 

Step 2. Copy kirby/target/kirby-0.0.1-SNAPSHOT.jar to docker-com/kirby folder

Step 3. Copy docker-compose folder to server

Step 4. Go to docker-com

Step 3. run docker-com --compatibility up

# Prometheus and Grafana Backup

you can run backup.sh script to backup prometheus env setting, prometheus db and grafana env setting.

# Prometheus and Grafana Restore

If you want to resore prometheus env setting, prometheus db and grafana env setting, you can run restore.sh script.
