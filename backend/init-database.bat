@echo off
echo Initializing database...
echo.

REM Check if MySQL is available
where mysql >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo MySQL command line tool not found in PATH.
    echo Please ensure MySQL is installed and added to PATH.
    echo.
    echo Alternative: Use MySQL Workbench or phpMyAdmin to execute init-database.sql
    pause
    exit /b 1
)

echo Executing database initialization script...
mysql -u root -pweilaikeqi999 < init-database.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Database initialized successfully!
) else (
    echo.
    echo Database initialization failed. Please check your MySQL credentials.
)

pause