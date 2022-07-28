#!/bin/sh

export modelname="N501"
#export versionurl="https://gamersgir.appspot.com"
export versionurl="https://nwarp-prod.appspot.com"
export posturl="http://localhost:8080"

full_digital() {

		var_str=$1

		var_index=0
		for line in `awk -v v=${var_str} 'BEGIN {
			n=split(v,a,".")
			for (i=1;i<=n;i++) {
				print a[i]
			}
		}'`; do
			eval var_$var_index=$line
			var_index=`expr $var_index + 1`
			#echo "var_index: ${var_index}"
		done

		par_str=$2

		par_index=0
		for line in `awk -v v=${par_str} 'BEGIN {
			n=split(v,a,".")
			for (i=1;i<=n;i++) {
				print a[i]
			}
		}'`; do
			eval par_$par_index=$line
			par_index=`expr $par_index + 1`
			#echo "par_index: ${par_index}"
		done

		if [ $par_index -ne $var_index ]
		then
			return
		fi

		index=0

		total=""

		while [ $index -lt $par_index ]
		do
			var_get_value="var_"$index
			eval var_value=\$$var_get_value
			#echo "var: ${var_value}" 
			par_get_value="par_"$index
			eval par_value=\$$par_get_value
			#echo "par: ${par_value}"
			index=`expr $index + 1`
			zero_num=$(printf "%0${par_value}d" $var_value)
			total=${total}${zero_num}
		done
		echo "${total}"
}


generate_fw_data()
{
time=$(date +%s)

  cat <<EOF
{
  "timestamp": "$time",
  "modelname": "$modelname",
  "version": $1
}
EOF
}

generate_gd_data()
{
time=$(date +%s)
  cat <<EOF
{
  "timestamp": "$time",
  "version": $1
}
EOF
}

generate_gamelist_data()
{
time=$(date +%s)
  cat <<EOF
{
  "timestamp": "$time",
  "version": $1
}
EOF
}


generate_server_token()
{
time=$(date -u +%Y%m%d%H%M)
SecretSalt='4gamersnwarp'
suffix=$(echo -n "$time$SecretSalt" | openssl dgst -sha256 -binary | xxd -p -c 32 | tr 'a-f' 'A-F' )
echo "$time-$suffix"
}

generate_post_token()
{
time=$(date -u +%Y%m%d%H%M)
SecretSalt='ilove4gamers'
suffix=$(echo -n "$time$SecretSalt" | openssl dgst -sha256 -binary | xxd -p -c 32 | tr 'a-f' 'A-F' )
echo "$time-$suffix"
}

token=$(generate_server_token)
versionInfo=$(curl -s -XGET -H "KIRBYTOKEN: $token" ${versionurl}/sys/contentver)

gamelist=$(jq -nr "${versionInfo}|.gamelist")
gd=$(jq -nr "${versionInfo}|.gd")
fw=$(jq -nr "${versionInfo}|.fw")
#echo "${fw} ${gd} ${gamelist}"
digFw=$(full_digital $fw "0.2.4")
digGd=$(full_digital $gd "0.4")
digGl=$(full_digital $gamelist "0")

token=$(generate_post_token)
curl -i \
-H "Accept: application/json" \
-H "Content-Type:application/json" \
-H "Authorization: $token" -XPOST --data "$(generate_fw_data ${digFw})" ${posturl}/prometheus/version/firmware

token=$(generate_post_token)
curl -i \
-H "Accept: application/json" \
-H "Content-Type:application/json" \
-H "Authorization: $token" -XPOST --data "$(generate_gd_data ${digGd})" ${posturl}/prometheus/version/gamedetector

token=$(generate_post_token)
curl -i \
-H "Accept: application/json" \
-H "Content-Type:application/json" \
-H "Authorization: $token" -XPOST --data "$(generate_gamelist_data ${digGl})" ${posturl}/prometheus/version/gamelist



