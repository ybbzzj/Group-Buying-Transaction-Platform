# 拉取代码
```bash      
git clone https://github.com/ybbzzj/Group-Buying-Transaction-Platform.git
```
# Maven构建
```bash
cd group-buy-plus
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
cd /group-buy-plus/docs/dev-ops
docker-compose -f docker-compose-app.yml up -d
```

