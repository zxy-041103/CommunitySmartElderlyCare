@echo off
cd /d d:\CommunitySmartElderlyCareMonitoringandManagementPlatform\backend
echo Starting backend server...
call mvn spring-boot:run -DskipTests
