@echo off
SETLOCAL EnableDelayedExpansion

set base=%~dp0..
set current=dir
set java="%JAVA_HOME%\bin\java"

for %%F in ("%base%\lib\*") do (set jars=!jars!;%%F)

set classpath="%base%\config%jars%"

%java% %args% -classpath %classpath% uk.ac.ebi.fgpt.goci.GOCITrackerDriver %*
