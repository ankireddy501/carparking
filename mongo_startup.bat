rem MONGO provide the location where the mongodb is located.
rem         If you don't have mongo db please install.

rem PROJECT_LOC provide the location where your project is located.

set MONGO=C:\Stanley\Software\mongodb-win32-x86_64-2008plus-2.4.9\mongodb-win32-x86_64-2008plus-2.4.9\bin
set PROJECT_LOC=C:\Users\vst\Documents\GitHub\iot
call %MONGO%\mongod -dbpath %PROJECT_LOC%\demodb