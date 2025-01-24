@echo off
set SCRIPT_DIR=%%~dp0
java -jar %%SCRIPT_DIR%%\distribution/QuakCLI-1.0-SNAPSHOT.jar %%*
