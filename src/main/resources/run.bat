
@echo "-------------start run server-------------"
 

@set classpath=%classpath%;.;.\lib\*;.\lib\sensor-1.0.jar;.\lib\slf4j-api-1.7.7.jar;.\lib\slf4j-log4j12-1.7.7.jar;.\lib\mysql-connector-java-5.1.30.jar;.\lib\ojdbc14.jar;

rem -DtemplateRootDir=template

@set PATH=%JAVA_HOME%\bin;%PATH%;
@java -server -Xms128m -Xmx384m com.cnjson.sensor.nio.SocketServer -Dwork.dir=D:/
@if errorlevel 1 (
@echo ----------------------------------------------
@echo  "*********************Server Exception*********************"
@pause
)

:end