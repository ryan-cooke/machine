@echo off
cd %0\..\
set TOKEN=25d2e4db5df4d35f545f2d35d1200341b1604d7e
wget.exe https://api.github.com/repos/ryan-cooke/machine/releases/latest?access_token=%TOKEN% --no-check-certificate -O git.json -q
FOR /f %%i IN ('type git.json ^| jq .assets[0].id') DO set ID=%%i
FOR /f %%i IN ('type git.json ^| jq .assets[0].name') DO set NAME=%%i
wget --auth-no-challenge --no-check-certificate --header="Accept: application/octet-stream" https://api.github.com/repos/ryan-cooke/machine/releases/assets/%ID%?access_token=%TOKEN% -O %NAME% -q
del git.json