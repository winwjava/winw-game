# git config --global credential.helper store

sendWechatMsg(){
  wechatAlertUrl=https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=c3e6cf15-b16d-43d0-854e-86c357968a9a
  data='{"msgtype": "text","text": {"content": "'$1'"}}'
  resp=$(curl -s $wechatAlertUrl -H 'Content-Type: application/json' -d "$data")
  echo "$(date +"%F %T,%3N") alert response: $resp" >> logs/build.log
}

if [[ "$1" = "" ]]; then
  echo "Usage: sh build.sh [every n seconds run once] , rather than only run once."
fi

cd "$(dirname "$0")"
mkdir -p jars logs

while true
do
  
  # 1. GIT pull, git checkout branch
  gitPull=$(git pull)

  wait # wait GIT Command completion.
  # git show Fast-forward, mean is git update ok.
  if [[ $gitPull == *"Fast-forward"* ]]; then
    echo "$(date +"%F %T,%3N") $gitPull" >> logs/build.log
    
    # 2. MAVEN package
    mvn clean package  -Dmaven.test.skip=true >> logs/build.log
    wait # wait MAVEN Command completion.
    # 3. MAVEN build SUCCESS
    if [[ $? -eq 0 ]]; then
      #sendWechatMsg "Maven BUILD SUCCESS."
      
      # 4. deploy and restart service, after kill wait 10s then force kill.
      sh bin/saas-service.sh deploy 10 >> logs/build.log
      sleep 5s
      wait
      sh bin/miniapp-service.sh deploy 10 >> logs/build.log

    else
      mvnBuild=$(tail -n 20 logs/build.log)
      # sendMail "Maven BUILD FAILURE" $mvnBuild
      sendWechatMsg "Maven BUILD FAILURE: \r\n$mvnBuild"
    fi
    
  fi
  
  if [[ "$1" = "" ]]; then
    exit 1
  else
    sleep $1 # 每15秒从GIT仓库查看是否有更新。
  fi
done
