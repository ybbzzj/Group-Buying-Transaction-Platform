# 拉取代码
```bash
cd /
git clone https://github.com/ybbzzj/Group-Buying-Transaction-Platform.git
```
# Maven构建
```bash
cd Group-Buying-Transaction-Platform
mvn clean install
```
# 构建Docker镜像
```bash
cd group_buy_plus-app
chmod +x build.sh
source build.sh
```
# 启动容器
```bash
cd /Group-Buying-Transaction-Platform/docs/dev-ops
vim docker-compose-app.yml
# 修改MySQL和Redis的IP和Port
docker-compose -f docker-compose-app.yml up -d
```


