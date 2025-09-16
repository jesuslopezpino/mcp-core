#!/bin/bash

echo "Building MCP Core..."

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "Maven not found in PATH. Please install Maven or add it to your PATH."
    echo "You can install Maven with:"
    echo "  - Ubuntu/Debian: sudo apt install maven"
    echo "  - macOS: brew install maven"
    echo "  - Or download from: https://maven.apache.org/download.cgi"
    exit 1
fi

echo "Running Maven tests..."
mvn -q -DskipTests=false test

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ All tests passed successfully!"
    echo ""
    echo "To build the project:"
    echo "  mvn clean install"
    echo ""
    echo "To run tests only:"
    echo "  mvn test"
else
    echo ""
    echo "❌ Tests failed. Check the output above for details."
    exit 1
fi
