#!/bin/sh
BASEDIR=/home/zoralo/docker-com
rm -rf $BASEDIR/backup
mkdir $BASEDIR/backup
mkdir $BASEDIR/backup/prometheus

prometheus_cid=`docker ps | grep prometheus | awk '{print $1}'`

#docker commit -p ${prometheus_cid} prometheus
#docker save -o $BASEDIR/backup/"prometheus_$(date +'%Y%m%d%H%M%S').tar" prometheus
#grafana_cid=`docker ps | grep grafana | awk '{print $1}'`
#docker commit -p ${grafana_cid} grafana
#docker save -o $BASEDIR/backup/"grafana_$(date +'%Y%m%d%H%M%S').tar" grafana

#alertmanager_cid=`docker ps | grep alertmanager | awk '{print $1}'`
#docker commit -p ${alertmanager_cid} alertmanager
#docker save -o ./backup/"alertmanager_$(date +'%Y%m%d%H%M%S').tar" alertmanager

tarInfo=$(curl --silent -XPOST http://localhost:9090/api/v2/admin/tsdb/snapshot?skip_head=true)
tar_name=$(jq -nr "${tarInfo}|.name")
docker exec -t ${prometheus_cid} sh -c "cd /prometheus/snapshots && tar zcf ${tar_name}.tar.gz ${tar_name}/"
docker cp ${prometheus_cid}:/prometheus/snapshots/${tar_name}.tar.gz $BASEDIR/backup/prometheus/
docker exec -t ${prometheus_cid} sh -c "cd /prometheus && rm -rf snapshots"

#tar_name="data_$(date +'%Y%m%d%H%M%S').tar"
#docker exec t ${prometheus_cid} sh -c "cd /prometheus && tar cvf ${tar_name} /prometheus/"
#docker cp ${prometheus_cid}:/prometheus/${tar_name} ./backup/prometheus/
#docker exec -t ${prometheus_cid} sh -c "cd /prometheus && rm ${tar_name}"

#docker-compose down
#prometheus_mid=`docker images | grep prometheus | awk '{print $3}'`
#grafana_mid=`docker images | grep grafana | awk '{print $3}'`
#alertmanager_mid=`docker images | grep alertmanager | awk '{print $3}'`
#docker image rm ${prometheus_mid} ${grafana_mid} ${alertmanager_mid}
