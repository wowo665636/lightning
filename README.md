#bi







--监控topo
mvn clean install -P prod-jk -Dmaven.test.skip=true;
--计费topo
mvn clean install -P prod-charge -Dmaven.test.skip=true;

--北研kafka+北研storm+BI监控redis
mvn clean install -P prod-by -Dmaven.test.skip=true;

--视频 storm 配置
mvn clean install -P prod-tv -Dmaven.test.skip=true;
