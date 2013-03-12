cd /D "%~dp0"
java -jar @@jar.name@@ --series-banners --season-banners --fanart-dir D:\Data\fanart\TV\ %*

cd ..\sage-scripting-framework
java -jar ssf.jar scripts\scanLibrary.py