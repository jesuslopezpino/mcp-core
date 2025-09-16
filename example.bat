@echo off
echo MCP Core Example Usage
echo =====================

REM Set JAVA_HOME if not already set
if "%JAVA_HOME%"=="" (
    echo Setting JAVA_HOME to default Java installation...
    set JAVA_HOME=C:\Program Files\Java\jdk-17
)

echo.
echo Building the project...
call .\mvnw.cmd clean compile -q

if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo ✅ Build successful!
echo.
echo The MCP Core library is now available at:
echo   target\mcp-core-1.0.0-SNAPSHOT.jar
echo.
echo You can use this JAR in other projects by adding it as a dependency:
echo.
echo   <dependency>
echo     <groupId>com.acme.mcp</groupId>
echo     <artifactId>mcp-core</artifactId>
echo     <version>1.0.0-SNAPSHOT</version>
echo   </dependency>
echo.
echo Or install it to your local Maven repository:
echo   .\mvnw.cmd install
echo.
echo Running tests to verify functionality...
call .\mvnw.cmd test -q

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ All tests passed! The library is ready to use.
) else (
    echo.
    echo ❌ Some tests failed. Check the output above.
)

echo.
pause
