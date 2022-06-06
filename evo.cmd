@echo off

setlocal

@REM Set the current directory to the installation directory
set CURRENT_DIR=%~dp0

set EVO_JAR=%CURRENT_DIR%evodb-1.0.jar
set JDBC_JAR_PATH=

@REM The following command is the shortcut for "evo" command written in CMD
java -cp "%EVO_JAR%;%CLASSPATH%;%JDBC_JAR_PATH%" com.evo.vcs.App %*