@echo off
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

echo Cleaning previous build output directory...
if exist build\classes rmdir /s /q build\classes

echo Creating build directories...
mkdir build\classes
mkdir build\classes\leanersdts
mkdir build\classes\Images

echo Listing contents of src\main\resources\leanersdts (FXML source):
dir src\main\resources\leanersdts\*.fxml

echo Copying FXML files to build\classes\leanersdts\...
xcopy src\main\resources\leanersdts\*.fxml build\classes\leanersdts\ /Y /I

echo Copying CSS files to build\classes\leanersdts\...
xcopy src\main\java\leanersdts\*.css build\classes\leanersdts\ /Y /I > nul

echo Copying Image files to build\classes\Images\...
xcopy src\main\resources\Images build\classes\Images\ /E /I /Y > nul

echo Contents of build\classes\leanersdts AFTER copying resources:
dir build\classes\leanersdts
echo Contents of build\classes\Images AFTER copying resources:
dir build\classes\Images

echo Compiling Java source files into build\classes...
javac -d build/classes -cp "lib/json-20250517.jar;lib/slf4j-api-2.0.9.jar;lib/slf4j-simple-2.0.9.jar;lib/javafx/javafx-sdk-17.0.14/lib/*" --module-path "lib/javafx/javafx-sdk-17.0.14/lib" --add-modules javafx.controls,javafx.fxml,javafx.web,javafx.media src\main\java\leanersdts\*.java

echo Contents of build\classes\leanersdts AFTER compilation:
dir build\classes\leanersdts

echo Compilation finished. Running application...
java -cp "lib/json-20250517.jar;lib/slf4j-api-2.0.9.jar;lib/slf4j-simple-2.0.9.jar;lib/javafx/javafx-sdk-17.0.14/lib/*;build/classes" --module-path "lib/javafx/javafx-sdk-17.0.14/lib" --add-modules javafx.controls,javafx.fxml,javafx.web,javafx.media,java.net.http -Xmx512m -Xms256m leanersdts.LeanerSDTS > output_new.log 2>&1