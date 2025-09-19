# Swagger2Postman
*Versión: 3.0.2 - 2024-04-17*

## Descripción
Swagger2postman es una herramienta desarrollada en node.js cuya funcionalidad es generar
colecciones postman y ficheros de variables postman a partir de un fichero swagger en formato .yaml

Usa un fichero llamado **config.json** que permite configurar la generación de esta colecciones. Por defecto este fichero tiene la configuración para seguir la normativa de generación de colecciones postman de la Comunidad de Madrid

 

## Preparacion del entorno
Swagger2postman es un ejecutable desarrollado en node.js
Para el uso de swagger2postman se debe tener instalado node y npm 

Descarga de Node.js y npm: https://nodejs.org/es/download/ 

### Primera instalación

    Si no existe el directorio "node_modules" habrá que hacer una instalación
    * Abrir una consola de comandos con "cmd"
    * Ejecutar: "npm install"
    * Esto compilará Swagger2Postman y lo dejará listo para su uso


## Directorios
Se recomienda usar los siguientes directorios:

- *Swagger_files*: donde estarán los swagger (ficheros yaml) con los que generar las colecciones de prueba
- *Postman_files*: donde se dejarán los postman generados con las colecciones generadas


## Ejecución con Swagger2Postman.bat
La ejecución recomendada de swagger2postman en windows se hará a través de bat: **swagger2posman.bat**

    swagger2postman.bat <fichero.yaml> <nombreapi> [config.json]"

En la ejecución por defecto no hay que pasar el parametro "config.json"
Los ficheros yaml deben estar en el directorio *Swagger_files*
Los ficheros postman se dejan en el directorio *Postman_files*


### Ejemplo de ejecución
    swagger2postman.bat plantilla_definicion_API.yaml plantilla


### Resultado de la ejecución
El resultado de la ejecución quedará en el directorio *Postman_files*
Se generarán 6 ficheros postman por cada fichero .yaml

| Fichero                                          | Descripción |
| ------                                           | ------ |
| %api_name%_01_pre_backend.postman_colection      | Colección para pruebas en desarrollo sin pasar por el API Manager|
| %api_name%_01_pre_backend.postman_environment    | Variables de entorno para pruebas en desarrollo sin pasar por el API Manager |
| %api_name%_02_pre_apim.postman_colection         | Colección para pruebas a través del API Manager de validación |
| %api_name%_02_pre_apim.postman_environment       | Variables de entorno para pruebas a través del API Manager de validación |
| %api_name%_03_pro_apim.postman_colection         | Colección para pruebas en producción a través del API Manager de producción|
| %api_name%_03_pro_apim.postman_environment       | Variables de entorno para pruebas en producción a través del API Manager de producción|



## Fichero config.json

En este fichero se configuran las colecciones a generar
y los valores por defecto de las variables para los test de ok y de error

Si no se pasa el tercer parámetro se usará el config.json por defecto
Si se desea, se puede usar otro fichero .json de configuración distinto y pasar como tercer parámetro la ruta de este fichero

| Variable  | Comentario                                                                                                                              |
| --------  | ----------
| is_inline | Indica si los valores de los ejemplos de los test_case estará inline (directamente escritos en cada caso) o en el fichero de environment mediante variables |
| examples/sucessful  | Valores de ejemplo para cada tipo de dato en los test sucessful, siempre que no exista en el swagger un "example"
| examples/wrong  | Valores de ejemplo para cada tipo de dato en los test wrong, siempre que no exista en el swagger un "example"
| enviroments | Configuracion de los distintos enviroment a generar (explicados en "Resultado de la ejecución"). Dentro de cada enviroment hay variables para configurar ese enviornment
| target_folder | Directorio de salida donde se dejarán los ficheros Postman
| validate_schema | Indica si en cada test case se va a validar el Json de respuesta en base al esquema del swagger
| has_scopes | Indica si la colección tendrá pruebas con scopes. En ese caso se generarán test case duplicados para probar con cada scope a probar el API Manager
| number_of_scopes | Número de scopes a probar. Serán los que se hayan definido para ese API concreto
| application_token | Indica si la colección tendrá pruebas con token de aplicación. Será lo que se haya definido para ese API concreto



Leer también el documento /docs/swagger2postman-Archivo_de_configuracion.pdf

## Documentación
Tiene más información de uso en el directorio */docs/*


