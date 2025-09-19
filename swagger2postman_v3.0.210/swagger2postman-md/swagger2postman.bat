@ECHO OFF
CLS
SET swaggerFile="./Swagger_files/%1"
SET apiName=%2
SET configFile=%3

IF %1.==. GOTO Ayuda
IF "%apiName%"=="" GOTO Ayuda
IF "%configFile%"=="" SET configFile=./config.json
IF NOT EXIST "%swaggerFile%" GOTO Error1
IF NOT EXIST "%configFile%" GOTO Error2


REM ejecución 
ECHO.
ECHO =====================
ECHO == swagger2postman ==
ECHO =====================
ECHO Fichero YAML:   %swaggerFile%
ECHO Nombre del API: %apiName%
ECHO Configuracion:  %configFile%
ECHO.
ECHO Generando ficheros en el directorio Postman_files 
ECHO.  
@ECHO ON
node index.js --file %swaggerFile% --api_name %apiName% --configuration %configFile% 
@ECHO OFF
ECHO. 


GOTO end


:Error1
ECHO.
ECHO ERROR: El fichero %swaggerFile% no existe
GOTO Ayuda

:Error1
ECHO.
ECHO ERROR: El fichero %configFile% no existe
GOTO Ayuda

:Ayuda
ECHO.
ECHO ****************************************************************
ECHO *               Swagger2Postman                                *
ECHO *                                                              *
ECHO * Modo de uso:                                                 *
ECHO * Swagger2Postman fichero.yaml api_name [config.json]          *
ECHO *                                                              *
ECHO * Los ficheros yaml deben estar en el directorio Swagger_files *
ECHO * Los ficheros postman se dejan en el directorio Postman_files *
ECHO *                                                              *
ECHO * Revise el documento 'Modo de uso.md'                         *
ECHO *                                                              *
ECHO ****************************************************************
ECHO.
GOTO End

:End



