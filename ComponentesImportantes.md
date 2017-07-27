

# Componentes importantes
 ## *Este documento se muestran componentes importantes de la app para su fácil comprensión*

### BroadcastReceiver_BTState
Como esta app trata principalmente de señales de bluetooth, a continuación se describe una clase que su función es detectar cuando el adaptador bluetooth cambie de estado y notificar con un toast y asi saber que pasa en el dispositivo o teléfono inteligente.

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

En los bloques de código a continuación se explica la función de la clase BTLE_Device, la cual almacena los datos de todos los dispositivos BLE que escanea en el área:
```java
public BTLE_Device(android.bluetooth.BluetoothDevice bluetoothDevice) {
       this.bluetoothDevice = bluetoothDevice;
    } 
```
El constructor obtiene un objeto de tipo bluetooth, lo cual nos da acceso a observar los datos como el nombre del dispositivo, su dirección MAC, etc. La cual se logra con los siguientes métodos:
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
El ListAdapter es una clase que prepara los valores de los dispositivos bluetooth escaneados y los muestra en un objeto de tipo lista (ListView), debido a que la aplicación en un inicio detectaba todos los dispositivos bluetooth de baja energía que se encontraban en el área y únicamente queríamos la información de los beacon, hicimos un pequeño filtro que explicaremos más adelante con la dirección MAC de esos dispositivos y los metimos a un arreglo, de igual manera les pusimos los nombres para que no nos lanzara el que da por default, si no unos que nosotros elegimos:
```java

String[] MyBeaconsMac = {"F1:82:F5:F1:79:E8", "C0:62:E9:C5:37:F5", "F7:D4:CF:09:98:F0" ,"DD:BE:F9:A1:D8:99","D4:88:E6:45:E1:2B"};
String[] MyBeaconsName = {"BEACON 1", "BEACON 2", "BEACON 3" ,"BEACON 4","BEACON 5"};

```
Ya teniendo en cuenta esto, está clase tiene un constructor el cual va a recibir parámetros como la activity en la que se encontrará, un arrayList que contendrá los datos recabados de la clase BTLE_Device: 
```java
public ListAdapter_BTLE_Devices(Activity activity, int resource, ArrayList<BTLE_Device> objects) {
    super(activity.getApplicationContext(), resource, objects);

    this.activity = activity;
    layoutResourceID = resource;
    devices = objects;
}
```
Para poder realizar el proceso de obtención de datos, requerimos un método de tipo View el cual se encargará de agarrar información almacenada en el BTLE_Device.

```java
public View getView(int position, View convertView, ViewGroup parent) {

    if (convertView == null) {
        LayoutInflater inflater =
                (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(layoutResourceID, parent, false);
    }

    BTLE_Device device = devices.get(position);
    String name = device.getName();
    //TODO: cambiar el nombre del beacon por el numero de beacon
    for (int i = 0; i < MyBeaconsMac.length; i++) {
        if (MyBeaconsMac[i].equals(device.getAddress())) {
            name = MyBeaconsName[i];
        }
    }
    String address = device.getAddress();
    int rssi = device.getRSSI();

    TextView tv = null;

    tv = (TextView) convertView.findViewById(R.id.tv_name);
    if (name != null && name.length() > 0) {
        tv.setText(name);
    }
    else {
        tv.setText("No Name");
    }
    TextView tv_rssi = null;

    tv_rssi = (TextView) convertView.findViewById(R.id.tv_rssi);
    tv_rssi.setText("RSSI: " + Integer.toString(rssi));

    TextView tv_macaddr = null;
    tv_macaddr = (TextView) convertView.findViewById(R.id.tv_macaddr);
    if (address != null && address.length() > 0) {
        tv_macaddr.setText(device.getAddress());
    }
    else {
        tv_macaddr.setText("No Address");
    }

    return convertView;
}
```

### Utils
Esta clase únicamente tiene funciones extras que fueron creadas y separadas del código principal, para que fuese más entendible, aquí se tienen funciones únicamente para mensajes y la validación de que el bluetooth este encendido en el dispositivo:
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

    public static void requestUserBluetooth(Activity activity) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, MainActivity.REQUEST_ENABLE_BT);
    }

    public static IntentFilter makeGattUpdateIntentFilter() {

        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(Service_BTLE_GATT.ACTION_GATT_CONNECTED);
        intentFilter.addAction(Service_BTLE_GATT.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(Service_BTLE_GATT.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(Service_BTLE_GATT.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }

    public static String hexToString(byte[] data) {
        final StringBuilder sb = new StringBuilder(data.length);

        for(byte byteChar : data) {
            sb.append(String.format("%02X ", byteChar));
        }

        return sb.toString();
    }

    public static int hasWriteProperty(int property) {
        return property & BluetoothGattCharacteristic.PROPERTY_WRITE;
    }

    public static int hasReadProperty(int property) {
        return property & BluetoothGattCharacteristic.PROPERTY_READ;
    }

    public static int hasNotifyProperty(int property) {
        return property & BluetoothGattCharacteristic.PROPERTY_NOTIFY;
    }

    public static void toast(Context context, String string) {

        Toast toast = Toast.makeText(context, string, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
        toast.show();
    }
}

```
### Scanner_BTLE

Esta clase se encarga de realizar el escaneo de los dispositivos en un periodo de 20 segundos y se realiza el proceso por medio de la clase handler:



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

    // If you want to scan for only specific types of peripherals,
    // you can instead call startLeScan(UUID[], BluetoothAdapter.LeScanCallback),
    // providing an array of UUID objects that specify the GATT services your app supports.
    private void scanLeDevice(final boolean enable) {
        if (enable && !mScanning) {
          //  Utils.toast(ma.getApplicationContext(), "Starting BLE scan...");

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
 
El MainActivity es la clase principal en la que se utiliza cada una de las clases vistas hasta el momento, trataremos de explicarlo lo más detallado posible ya que es demasiado código lo que se lee, así que empezaremos con la declaración de variables que se necesitan:
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
En el código anterior, mostramos las variables que se usaran principalmente en este código, mandamos a llamar las clases que hemos estado explicando, así como las direcciones MAC de los beacons para poder realizar el filtro y solo encuentre los dispositivos bluetooth con esas direcciones, un ListView para mostrar los datos de estos, arrayRSSI son los valores que se le dan por default a los rssi de los beacons en caso de que si no detecta alguno, no se quede en 0 y se ponga el valor      de   -110.
A continuación, para poder predecir la localización de una persona ocupamos las siguientes matrices:
```java

private final String[][] MatrizRestaCuadratica = new String[][] {
           /*[1]*/      {"4,6.76,0.64 ,1.44 ,5.76","4,12.96,0.36,0.04,17.64","33.64,14.44,0.04,33.64,0.04","17.64,36,14.44,9,0.04"},
           /*[2]*/     {"2.56,16,2.56,14.44,3.24","16,77.44,31.36,2.56,11.56","12.96,57.76,1.96,0.36,5.76","1.96,7.84,6.76,17.64,0.64"},
           /*[3]*/   {"2.56,54.76,0.64,3.24,0.16","2.56,0.36,1.96,17.64,9","4,0.64,43.56,0.16,19.36","1,9,2.56,1.96,46.24"},
           /*[4]*/    {"1.96,10.24,0.16,1.44,14.44","0.64,33.64,0.04,3.24,4.84","6.76,0.04,4,25,21.16","1.96,4.84,36,3.24,10.24"}

   };
    private final String[][] MatrizAtenuaciones = new String[][] {
           /*[1]*/      {"[1,1]:-89,-85.6,-90.8,-90.8,-89.6","[1,2]:-90,-82.4,-93.4,-92.8,-88.8","[1,3]:-91.8,-89.8,-91.2,-88.2,-89.8","[1,4]:-89.8,-89,-92.8,-90,-93.2"},
           /*[2]*/     {"[2,1]:-92.6,-82,-87.6,-92.8,-88.8","[2,1]:-92,-81.2,-88.4,-91.6,-89.4","[2,3]:-92.6,-90.6,-92.6,-93.4,-90.6","[2,4]:-90.6,-89.2,-89.6,-88.8,-91.8"},
           /*[3]*/   {"[3,1]:-92.4,-85.4,-90.2,-92.2,-87.6","[3,3]:-92.4,-85.6,-92.6,-89.8,-88","[3,3]:-92,-85.2,-92.6,-90.6,-91.4","[3,4]:-91,-89,-89.4,-92.4,-85.2"},
           /*[4]*/    {"[4,1]:-92.6,-81.8,-91.4,-92.8,-86.2","[4,2]:-93.8,-81.2,-89.8,-88.2,-89.8","[4,3]:-91.4,-88.2,-87,-92,-89.4","[4,4]:-92.4,-87.8,-85,-91.2,-90.8"}

    };


```
Con estas matrices la idea es poder sacar unos índices con algo llamado resta cuadrática y lograr comparar entre todos esos cual era el menor y en teoría esa es la coordenada en la que te encuentras.
Lo siguiente es inicializar todas las variables que sean del tipo de las clases mencionadas anteriormente en el método principal onCreate:
```java
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
      /*  export = (Button) findViewById(R.id.exportar);
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDB();
            }
        });*/

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Utils.toast(getApplicationContext(), "BLE not supported");
            finish();
        }
        mBTStateUpdateReceiver = new         BroadcastReceiver_BTState(getApplicationContext());
        mBTLeScanner = new Scanner_BTLE(this, 20000, -95 );
        mBTDevicesHashMap = new HashMap<>();
        mBTDevicesArrayList = new ArrayList<>();

        adapter = new ListAdapter_BTLE_Devices(this, R.layout.btle_device_list_item, mBTDevicesArrayList);

        listView = new ListView(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        ((ScrollView) findViewById(R.id.scrollView)).addView(listView);
        err = (EditText) findViewById(R.id.errormessage);
        indexs = (EditText) findViewById(R.id.indexs);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        background = (LinearLayout) findViewById(R.id.backgr);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Scanning Beacons ...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Buttonflag = 1;

                if (!mBTLeScanner.isScanning()) {
                    startScan();
                 //   background.setBackgroundColor(getResources().getColor(R.color.Sunflower));


                }
                else {
                    stopScan();
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Emerald)));


                }
            }
        });
    }

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
 
A continuación, se nos presenta el método que le pasa los datos a la clase BTLE_Device para que vaya agregando la información que se recopilando del beacon:
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





