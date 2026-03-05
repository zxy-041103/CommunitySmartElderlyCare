@echo off
echo ========================================
echo 数据库连接测试工具
echo ========================================
echo.

echo 正在编译测试程序...
javac -encoding UTF-8 -cp "target/classes;target/dependency/*" src/main/java/com/community/elderly/DatabaseConnectionTestRunner.java 2>nul

if %errorlevel% neq 0 (
    echo 编译失败，请确保已安装 Maven 并运行 mvn clean package
    echo.
    echo 使用方法：
    echo 1. 确保已安装 Maven
    echo 2. 在 backend 目录下运行: mvn clean package
    echo 3. 运行此脚本
    echo.
    pause
    exit /b 1
)

echo 编译成功！
echo.

echo 正在运行数据库连接测试...
java -cp "target/classes;target/dependency/*" com.community.elderly.DatabaseConnectionTestRunner

echo.
echo ========================================
echo 测试完成
echo ========================================
pause
