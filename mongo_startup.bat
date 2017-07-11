rem MONGO provide the location where the mongodb is located.
rem         If you don't have mongo db please install.

rem PROJECT_LOC provide the location where your project is located.

set MONGO=C:\softwares\mongodb-win32-x86_64-enterprise-windows-64-3.4.5\bin

if not exist ..\demodb mkdir ..\demodb
call %MONGO%\mongod -dbpath ..\demodb

pause