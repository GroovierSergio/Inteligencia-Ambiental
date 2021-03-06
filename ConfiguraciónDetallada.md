# Configuración detallada


## Configuración de los Beacon Beeks.

En esta parte explicaremos el porque de las configuraciones para los Beacon Beeks y para la aplicación que lee las señales.

### 1 Hz.
 Se eligió 1 Hz ya que es la máxima configuración posible. Esto significa que se envía una señal por segundo al menos para el modelo de Beacon Beeks utilizados en este proyecto. La configuración a esa velocidad genera menos irregularidad entre las señales al momento de escanearlas.


### -16 dBm.
Esta configuración en nuestro caso es la más apropiada, ya que abarca como máximo 18 metros cuadrados, lo necesario para el laboratorio ya 
que la señal se atenua con los objetos que se encuentran dentro del lugar. Para establecer la configuración de la potencia de las señales 
de las balizas se deberán hacer pruebas y decidir cual es la más adecuada para el entorno en el que se esta trabajando. Nuestra 
recomendación es la siguiente:


###                  Day Mode :                    
| Advertisement Rate    |  Transmission Power   |
|-----------------------|-----------------------|
| 1 Hz (Once a second)  |  -16 dBm              |

###                 Night Mode :                    
| Advertisement Rate    |  Transmission Power   |
|-----------------------|-----------------------|
| 1 Hz (Once a second)  |  -16 dBm              |




La aplicación con la cuál se mide las señales RSSI también debe ser configurada, se recomienda que el tiempo de espera
para la recepción de las señales de los Beacon Beeks sea de 20 segundos ya que en este lapso se logra estabilizar la señal.

###  Configuración de la aplicación móvil :
|Tiempo de lectura      |Alcance                |
| --------------------- | --------------------- |
|      20 segundos      |-95 dBm                |
                                     

 
 También se debe configurar la aplicación para que esta no detecte cualquier dispositivo Bluetooth si no solo las direcciones MAC
 asociadas a los dispositivos que enviaran las señales, como ejemplo nosotros la configuramos de la siguiente manera:
 
 ### Configuración de  las direcciones MAC :  
|Número de Beacon       |Dirección MAC          |
| --------------------- | --------------------- |
| 1                     | F1:82:F5:F1:79:E8     |
| 2                     | C0:62:E9:C5:37:F5     |
| 3                     | F7:D4:CF:09:98:F0     |
| 4                     | DD:BE:F9:A1:D8:99     |
| 5                     | D4:88:E6:45:E1:2B     |








