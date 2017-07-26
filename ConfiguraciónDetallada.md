# Configuración detallada


##Configuración de los Beacon Beeks.

En esta parte explicaremos el porque de las configuraciones para los Beacon Beeks y para la aplicación que lee las señales.

### 1 Hz.
 Se eligió 1 Hz ya que es la máxima configuración posible. Esto significa que se envía una señal por segundo. No se puede configurar
 a mayor potencia, al menos para el modelo de Beacon Beeks utilizados en este proyecto. Por lo que se debe mantener así.


### -16 dBm.
Esta configuración en nuestro caso es la más apropiada, ya que abarca como máximo 18 metros cuadrados, lo necesario para el laboratorio ya 
que la señal se atenua con los objetos que se encuentran dentro del lugar. Para establecer la configuración de la potencia de las señales 
de las balizas se deberán hacer pruebas y decidir cual es la más adecuada para el entorno en el que se esta trabajando. Nuestra 
recomendación es la siguiente:


|                   Day Mode                    |
| --------------------- | --------------------- |
| Advertisement Rate    | 1 Hz (Once a second)  |
| Transmission Power    | -16 dBm               |


|                   Night Mode                  |
| --------------------- | --------------------- |
| Advertisement Rate    | 1 Hz (Once a second)  |
| Transmission Power    | -16 dBm               |



## Configuración de la aplicación móvil.


|     Configuración de la aplicación móvil      |
| --------------------- | --------------------- |
|Tiempo de lectura      | 20 segundos           |
| Alcance               | -95 dBm               |

La aplicación con la cuál se mide las señales RSSI también debe ser configurada, se recomienda que el tiempo de espera
 para la recepción de las señales de los Beacon Beeks sea de 20 segundos de espera  ya que en este lapso se logra estabilizar la señal.
 
 También se debe configurar la aplicación para que esta no detecte cualquier dispositivo Bluetooth si no solo las direcciones MAC
 asociadas a los dispositivos Beacon Beeks, como ejemplo nosotros la configuramos de la siguiente manera:
 
|     Configuración de las direcciones MAC      |
| --------------------- | --------------------- |
| 1                     | F1:82:F5:F1:79:E8     |
| 2                     | C0:62:E9:C5:37:F5     |
| 3                     | F7:D4:CF:09:98:F0     |
| 4                     | DD:BE:F9:A1:D8:99     |
| 5                     | D4:88:E6:45:E1:2B     |








