#!/bin/sh

prometheus_tar=`ls backup | grep prometheus | awk '{print $1}'`
#grafana_tar=`ls backup | grep grafana | awk '{print $1}'`
#alertmanager_tar=`ls backup | grep alertmanager | awk '{print $1}'`

#docker load -i ./backup/${prometheus_tar}
#docker load -i ./backup/${grafana_tar}
#docker load -i ./backup/${alertmanager_tar}

#docker-compose up -d

prometheus_cid=`docker ps | grep prometheus | awk '{print $1}'`
tar_name=`ls backup/prometheus | grep .tar.gz |awk -F'.' '{print $1}'`
docker cp ./backup/prometheus/${tar_name}.tar.gz ${prometheus_cid}:/prometheus
docker exec -ti ${prometheus_cid} sh -c "cd /prometheus && tar zxvf ${tar_name}.tar.gz && find ./${tar_name} -name '*' -exec mv {} . \; && rm -rf ${tar_name} ${tar_name}.tar.gz"


