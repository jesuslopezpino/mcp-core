@echo off
echo Building MCP Core...

REM Check if Maven is available, if not use Maven Wrapper
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Maven not found in PATH. Using Maven Wrapper...
    set MAVEN_CMD=mvnw.cmd
) else (
    echo Using system Maven...
    set MAVEN_CMD=mvn
)

echo Running Maven tests...
%MAVEN_CMD% -q -DskipTests=false test

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ All tests passed successfully!
    echo.
    echo To build the project:
    echo   %MAVEN_CMD% clean install
    echo.
    echo To run tests only:
    echo   %MAVEN_CMD% test
) else (
    echo.
    echo ❌ Tests failed. Check the output above for details.
    exit /b 1
)
