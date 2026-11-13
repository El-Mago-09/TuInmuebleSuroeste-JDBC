@echo off
REM Compila y ejecuta el modulo de inmuebles (requiere JDK 17+ y db.properties)
if not exist out mkdir out
javac -encoding UTF-8 -d out -cp lib\mysql-connector-j-26.10.0.jar src\com\tuinmueble\conexion\*.java src\com\tuinmueble\modelo\*.java src\com\tuinmueble\dao\*.java src\com\tuinmueble\principal\*.java
if errorlevel 1 exit /b 1
java -cp "out;lib\mysql-connector-j-26.10.0.jar" com.tuinmueble.principal.Main %*
