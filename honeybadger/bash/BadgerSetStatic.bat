@echo off 
echo Choose: 
echo [A] Set Static IP 
echo [B] Set DHCP 
echo. 
:choice 
SET /P C=[A,B]? 
for %%? in (A) do if /I "%C%"=="%%?" goto A 
for %%? in (B) do if /I "%C%"=="%%?" goto B 
goto choice 
:A 
@echo off 
set IP_Addr=192.168.0.42
set D_Gate=192.168.0.1
set Sub_Mask=255.255.255.0

echo "Setting Static IP Information" 
netsh interface ip set address "Wi-Fi" static %IP_Addr% %Sub_Mask% %D_Gate% 1 
netsh int ip show config 
pause 
goto end

:B 
@ECHO OFF 
ECHO Resetting IP Address and Subnet Mask For DHCP 
netsh int ip set address name = "Wi-Fi" source = dhcp

ipconfig /renew

ECHO Here are the new settings for %computername%: 
netsh int ip show config

pause 
goto end 
:end