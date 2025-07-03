@echo off
setlocal enabledelayedexpansion

echo [CONFIG] Setting up environment variables...

set "PROJECT_ROOT=%~dp0"
set "PROJECT_ROOT=%PROJECT_ROOT:~0,-1%"
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "JAVAFX_SDK_LIB=C:\Users\Soets\Documents\javafx-sdk-17.0.11\lib"
set "SRC_DIR=%PROJECT_ROOT%\src\main\java"
set "RESOURCES_DIR=%PROJECT_ROOT%\src\main\resources"
set "BUILD_DIR=%PROJECT_ROOT%\build"
set "CLASSES_DIR=%BUILD_DIR%\classes"
set "LIB_DIR=%PROJECT_ROOT%\lib"

set "PATH=%JAVA_HOME%\bin;%PATH%"

REM --- Build Module Path ---
echo [CONFIG] Building explicit module path from JARs in lib directory...
set "MODULE_PATH="
set "MODULE_PATH=%JAVAFX_SDK_LIB%"
for %%j in ("%LIB_DIR%\*.jar") do (
    set "MODULE_PATH=!MODULE_PATH!;%%j"
)

echo [CLEAN] Removing previous build directory...
if exist "%BUILD_DIR%" (
    rmdir /s /q "%BUILD_DIR%"
    echo [CLEAN] Previous build directory removed.
)

echo [SETUP] Creating build directories...
mkdir "%BUILD_DIR%"
mkdir "%CLASSES_DIR%"
echo [SETUP] Build directories created.

echo [COPY] Copying resources (FXML, CSS, Images)...
xcopy "%RESOURCES_DIR%" "%CLASSES_DIR%" /E /I /Y /Q > nul
if %errorlevel% neq 0 (
    echo [ERROR] Failed to copy resources.
    exit /b 1
)
echo [COPY] Resources copied successfully.

echo [COMPILE] Finding all .java files...
set "SOURCES_FILE=sources.txt"
if exist "%SOURCES_FILE%" del "%SOURCES_FILE%"
dir /s /b "%SRC_DIR%\*.java" > "%SOURCES_FILE%"

echo [COMPILE] Compiling Java sources...
javac --module-path "%MODULE_PATH%" -d "%CLASSES_DIR%" @"%SOURCES_FILE%" > "%BUILD_DIR%\build_output.log" 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed. See errors above.
    del "%SOURCES_FILE%"
    exit /b 1
)
echo [COMPILE] Compilation successful.
del "%SOURCES_FILE%"

echo [RUN] Starting the application...
java --module-path "%MODULE_PATH%;%CLASSES_DIR%" --add-modules javafx.controls,javafx.fxml,javafx.web,javafx.media,org.slf4j,org.json,java.sql,java.logging -m leanersdts/leanersdts.LeanerSDTS

if %errorlevel% neq 0 (
    echo [ERROR] Application failed to run.
    exit /b 1
)

echo [END] Application finished.
endlocal
