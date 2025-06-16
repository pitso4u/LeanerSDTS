@echo off
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

if not exist build\classes mkdir build\classes
if not exist build\classes\leanersdts mkdir build\classes\leanersdts

xcopy src\main\resources\leanersdts\*.fxml build\classes\leanersdts\ /Y
xcopy src\main\java\leanersdts\*.css build\classes\leanersdts\ /Y
xcopy src\main\resources\Images build\classes\Images\ /E /I /Y

javac --module-path "lib/javafx/javafx-sdk-17.0.14/lib" --add-modules javafx.controls,javafx.fxml,javafx.web,javafx.media,java.net.http -verbose -sourcepath src/main/java -cp "lib/json-20250517.jar;lib/slf4j-api-2.0.9.jar;lib/slf4j-simple-2.0.9.jar;build/classes" -d build/classes src/main/java/leanersdts/*.java > compile_output.log 2>&1