# 基础镜像
FROM openjdk:8-jre-slim

# 作者
MAINTAINER baozhongjie

# 配置
ENV PARAMS=""

# 时区
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 添加应用
ADD target/group_buy_plus-app.jar /group_buy_plus-app.jar

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /group_buy_plus-app.jar $PARAMS"]