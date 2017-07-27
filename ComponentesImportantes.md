# Componentes importantes
Esta sección se muestra la codificación desarrollada para la funcionalidad de la aplicación.

### BroadcastReceiver_BTState
A continuación, se describe una clase la cual tiene como función detectar cuando el adaptador bluetooth cambie de estado y notificar con un toast, de esta manera saber que pasa en el dispositivo o teléfono inteligente.

```java

public class BroadcastReceiver_BTState extends BroadcastReceiver {
    Context activityContext;
    public BroadcastReceiver_BTState(Context activityContext) {
        this.activityContext = activityContext;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Utils.toast(activityContext, "Bluetooth is off");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Utils.toast(activityContext, "Bluetooth is turning off...");
                    break;
                case BluetoothAdapter.STATE_ON:
                    Utils.toast(activityContext, "Bluetooth is on");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                   Utils.toast(activityContext, "Bluetooth is turning on...");
                    break;
            }
        }
    }
}
```



### BTLE_Device

En los siguientes bloques de código se explica la función de la clase BTLE_Device, la cual almacena los datos de todos los dispositivos BLE (Bluetooth Low Energy) que escanea en el área:
```java
public BTLE_Device(android.bluetooth.BluetoothDevice bluetoothDevice) {
       this.bluetoothDevice = bluetoothDevice;
    } 
```
El constructor obtiene un objeto de tipo bluetooth, lo cual nos da acceso a observar los datos como el nombre del dispositivo, su dirección MAC y el RSSI. La cual se logra con los siguientes getters settters:
```java
public String getAddress() {
        return bluetoothDevice.getAddress();
    }
    public String getName() {
        return bluetoothDevice.getName();
    }
public void setRSSI(int rssi) {
    this.rssi = rssi;
}

public int getRSSI() {
    return rssi;
}
```

### ListAdapter_BTLE_Devices
El *ListAdapter* es una clase que prepara los valores de los dispositivos bluetooth escaneados y los muestra en un objeto de tipo lista (ListView), debido a que la aplicación en un inicio detectaba todos los dispositivos bluetooth de baja energía que se encontraban en el área y únicamente era requerida la información de los Beacon Beeks, se hizo un pequeño filtro con la dirección MAC de esos dispositivos despues fueron añadidos a un arreglo, esta parte esta explicada a mayor detalle más adelante en la clase *MainActivity*, de igual manera los nombres que los Beacon traen por default fueron cambiados para mejorar el entendimiento de los mismos:
```java

String[] MyBeaconsMac = {"F1:82:F5:F1:79:E8", "C0:62:E9:C5:37:F5", "F7:D4:CF:09:98:F0" ,"DD:BE:F9:A1:D8:99","D4:88:E6:45:E1:2B"};
String[] MyBeaconsName = {"BEACON 1", "BEACON 2", "BEACON 3" ,"BEACON 4","BEACON 5"};

```
Ya teniendo en cuenta esto, está clase tiene un constructor el cual va a recibir parámetros como la *activity* en la que se encontrará, un *arrayList* que contendrá los datos recabados de la clase *BTLE_Device*: 
```java
public ListAdapter_BTLE_Devices(Activity activity, int resource, ArrayList<BTLE_Device> objects) {
    super(activity.getApplicationContext(), resource, objects);

    this.activity = activity;
    layoutResourceID = resource;
    devices = objects;
}
```


### Utils
Esta clase únicamente tiene funciones extras que fueron creadas y separadas del código principal, para que fuese más entendible, aquí se tienen funciones únicamente para mensajes. 

La validación de que el Bluetooth este encendido en el dispositivo se ejecuta mediante el siguiente código:
```java
public class Utils {

    public static boolean checkBluetooth(BluetoothAdapter bluetoothAdapter) {

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return false;
        }
        else {
            return true;
        }
    }
```

En el siguiente código, se manda a llamar una actividad en la cual activa el Bluetooth:

```java
    public static void requestUserBluetooth(Activity activity) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, MainActivity.REQUEST_ENABLE_BT);
    }
```
### Scanner_BTLE

Esta clase se encarga de almacenar todos los datos del escaneo de los dispositivos así como el período de escaneo y el nivel de atenuación máxima. El proceso es realizado por medio de la clase *handler* (Hilos de programación).

```java
public class Scanner_BTLE {


    private MainActivity ma;

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private long scanPeriod;
    private int signalStrength;

    public Scanner_BTLE(MainActivity mainActivity, long scanPeriod, int signalStrength) {
        ma = mainActivity;
        mHandler = new Handler();
        this.scanPeriod = scanPeriod;
        this.signalStrength = signalStrength;
        final BluetoothManager bluetoothManager =
                (BluetoothManager) ma.getSystemService(Context.BLUETOOTH_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }
    }

    public boolean isScanning() {
        return mScanning;
    }

    public void start() {
        if (!Utils.checkBluetooth(mBluetoothAdapter)) {
            Utils.requestUserBluetooth(ma);
            ma.stopScan();
        }
        else {

            scanLeDevice(true);
        }
    }

    public void stop() {
        scanLeDevice(false);
    }

    
    private void scanLeDevice(final boolean enable) {
        if (enable && !mScanning) {
          
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Utils.toast(ma.getApplicationContext(), "Stopping BLE scan...");
                    mScanning = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                    ma.stopScan();

                }
            }, scanPeriod);

            mScanning = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
//            mBluetoothAdapter.startLeScan(uuids, mLeScanCallback);
        }
        else {
            mScanning = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    }
```
En este módulo de código se ejecuta un hilo en el que se va actualizando los datos de la intensidad de señal y al finalizar
el período antes decretado se agrega al dispositivo junto con el RSSI:
```java
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

                    final int new_rssi = rssi;
                    if (rssi > signalStrength) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                ma.addDevice(device, new_rssi);
                            }
                        });
                    }
                }
            };
}


```
### MainActivity
 
Esta es la clase principal que concentra las clases mencionadas hasta el momento, ahora procedemos a fragmentar el código:
```java
public static final int REQUEST_ENABLE_BT = 1;
public static final int BTLE_SERVICES = 2;
public int Iteracion = 0;
public HashMap<String,BTLE_Device> mBTDevicesHashMap;
public HashMap<String,String> CalibracionHashMap;

private ArrayList<BTLE_Device> mBTDevicesArrayList;

String  ArrayMacAddress[] = {"1","2", "3", "4", "5"};
String ArrayRSSI[] = {"-110","-110",  "-110","-110", "-110"};
    // you must change this mac addres array with your beacons mac adresses
    String[] MyBeaconsMac = {"F1:82:F5:F1:79:E8", "C0:62:E9:C5:37:F5", "F7:D4:CF:09:98:F0" ,"DD:BE:F9:A1:D8:99","D4:88:E6:45:E1:2B"};
private ListAdapter_BTLE_Devices adapter;
private ListView listView;
private TextView err;
private TextView indexs;

private BroadcastReceiver_BTState mBTStateUpdateReceiver;
private Scanner_BTLE mBTLeScanner;
private Button export;
private LinearLayout background;
FloatingActionButton fab;
int Buttonflag = 0;

```
En el código anterior, mostramos las variables que se usaran principalmente en este código, utilizamos el arreglo *MyBeaconsMac* para guardar las direcciones MAC de interés, por otra parte usamos *ArrayMacAddress* y *ArrayRSSI* para ordenar los datos recibidos cada vez que sea ejecutado el scanner.

La matriz que se muestra a continuación, es el resultado obtenido en experimentos de medición. En esta matriz se guarda el promedio obtenido de las atenuaciones.

*Ejemplo:*
1: -89,-85.6,-90.8,-90.8,-89.6

El primer elemento (-89) representa la atenuación del beacon 1 en la coordenada [1,1]. <br>
El segundo elemento (-85.6) representa la atenuación del beacon 2 en la coordenada [1,1]. <br>
y así sucesivamente para los 3 Beacon restantes.<br>

```java

    private final String[][] MatrizAtenuaciones = new String[][] {
           /*[1]*/      {"[1,1]:-89,-85.6,-90.8,-90.8,-89.6","[1,2]:-90,-82.4,-93.4,-92.8,-88.8","[1,3]:-91.8,-89.8,-91.2,-88.2,-89.8","                         [1,4]:-89.8,-89,-92.8,-90,-93.2"},
           /*[2]*/     {"[2,1]:-92.6,-82,-87.6,-92.8,-88.8","[2,1]:-92,-81.2,-88.4,-91.6,-89.4","[2,3]:-92.6,-90.6,-92.6,-93.4,-90.6","                         [2,4]:-90.6,-89.2,-89.6,-88.8,-91.8"},
           /*[3]*/   {"[3,1]:-92.4,-85.4,-90.2,-92.2,-87.6","[3,3]:-92.4,-85.6,-92.6,-89.8,-88","[3,3]:-92,-85.2,-92.6,-90.6,-91.4","                           [3,4]:-91,-89,-89.4,-92.4,-85.2"},
           /*[4]*/    {"[4,1]:-92.6,-81.8,-91.4,-92.8,-86.2","[4,2]:-93.8,-81.2,-89.8,-88.2,-89.8","[4,3]:-91.4,-88.2,-87,-92,-89.4","                           [4,4]:-92.4,-87.8,-85,-91.2,-90.8"}
    };


```
Con esta matriz se obtendrán índices con el método [resta cuadrática](https://drive.google.com/open?id=0B_SXGEKN91UQUDJHUjlQcnd0U2c) para obtener el valor mínimo y la [Gradiente](ttps://drive.google.com/open?id=0B_SXGEKN91UQX0FvV1ltRGR4X3M). 

--Nota: Para poder apreciar los datos de los experimentos se deben descargar los archivos.


En el siguiente módulo se empieza a inicializar las variables declaradas anteriormente, aquí la más destacable es *mBTLeScanner* puesto que es donde requerimos poner el tiempo y la potencia con la que se escanea. Gracias a las pruebas realizadas anteriormente decidimos implementar un estándar de 20 segundos de tiempo y -95 dbm de potencia.
```java
        mBTStateUpdateReceiver = new         BroadcastReceiver_BTState(getApplicationContext());
        mBTLeScanner = new Scanner_BTLE(this, 20000, -95 );
```
Se crean métodos para iniciar y detener el escaneo de los beacons como los que se muestran a continuación:
```java

@Override
    protected void onStart() {
        super.onStart();

        registerReceiver(mBTStateUpdateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    protected void onResume() {
        super.onResume();

//        registerReceiver(mBTStateUpdateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();

//        unregisterReceiver(mBTStateUpdateReceiver);
        stopScan();
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(mBTStateUpdateReceiver);
        stopScan();
    }
```
 
A continuación, se nos presenta el método que le pasa los datos a la clase BTLE_Device para que vaya agregando la información que se va recopilando del Beacon:

```java
public void addDevice(BluetoothDevice device, int rssi) {

    String address = device.getAddress();
    if(Arrays.asList(MyBeaconsMac).contains(address)){
        //Toast.makeText(this, address, Toast.LENGTH_SHORT).show();
        if (!mBTDevicesHashMap.containsKey(address)) {
            BTLE_Device btleDevice = new BTLE_Device(device);
            btleDevice.setRSSI(rssi);
            mBTDevicesHashMap.put(address, btleDevice);

            mBTDevicesArrayList.add(btleDevice);
        } else {
            mBTDevicesHashMap.get(address).setRSSI(rssi);
        }
    }
    adapter.notifyDataSetChanged();
}
```

## Canvas
Clase encargada de mostrar de manera gráfica la localización de una persona en un plano 2D, el cual se ha construido por medio de imágenes en una matriz 4x4, tal como podran observar en el siguiente módulo de código:
```java
try {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    imagenes[i][j] = new ImageView(getApplicationContext());
                    imagenes[i][j].setPadding(1, 1, 1, 1);
                    imagenes[i][j].setImageResource(R.drawable.casilla);
                    // imagenes[i][j].setId(i);
                    //Lugar donde pongo la accion que me marca en un toast los id de los imageView
                    lienzoImagenes.addView(imagenes[i][j]);
                }
            }
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
```

Este plano dibuja un punto en la coordenada que se recibe desde el *MainActivity* al finalizar el escaneo de los Beacons :

```java
tvCoordenadasX=(TextView)findViewById(R.id.tvCoordenadasX);
        String acomodo="    \t 1 \t\t\t 2 \t\t\t 3 \t\t  4\t\t\t";
        tvCoordenadasX.setText(acomodo);
          
        int coordenada = getIntent().getExtras().getInt("coordenada");
        String RealCoord = Utils.IntToCoord(coordenada);
        String[] coor_array = RealCoord.split(",");
        String coor_arrayX = coor_array[0];
        String coor_arrayY = coor_array[1];
        Toast.makeText(this,"Las coordenadas son: "+coor_arrayX +" y "+ coor_arrayY,Toast.LENGTH_LONG).show();
        int x=Integer.parseInt(coor_arrayX);
        int y= Integer.parseInt(coor_arrayY);
        try
        {
            imagenes[x-1][y-1].setImageResource(R.drawable.circulo);
            
        }catch (Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
```
Con esto se concluye las partes más importantes de la aplicación.


