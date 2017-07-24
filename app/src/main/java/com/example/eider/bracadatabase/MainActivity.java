package com.example.eider.bracadatabase;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
        private final static String TAG = MainActivity.class.getSimpleName();

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
   private final String[][] MatrizRestaCuadratica = new String[][] {

           /*[1]*/      {"[1,1]:229.68", "[1,2]:63.8", "[1,3]:34.24","[1,4]:138.88","[1,5]:148.36","[1,6]:360.72","[1,7]:28.48","[1,8]:16.72"},
           /*[2]*/     {"[2,1]:26.6","[2,2]:264.96","[2,3]:55.12","[2,4]:546.36","[2,5]:29.92","[2,6]:181.04","[2,7]:61.88","[2,8]:213.36"},
           /*[3]*/   {"[3,1]:26.2","[3,2]:32.6","[3,3]:73.52","[3,4]:57.92","[3,5]:20","[3,6]:115.16","[3,7]:138.36","[3,8]:81.56"},
           /*[4]*/    {"[4,1]:365.2","[4,2]:59.84","[4,3]:149.36","[4,4]:83.96","[4,5]:558.4","[4,6]:79.6","[4,7]:47.12","[4,8]:405.76"},
            /*[5]*/  {"[5,1]:61.32","[5,2]:183.24","[5,3]:60.72","[5,4]:99.04","[5,5]:169.52","[5,6]:450.2","[5,7]:16.2","[5,8]:76.28"},
            /*[6]*/  {"[6,1]:452.92","[6,2]:58.6","[6,3]:178.96","[6,4]:56.36","[6,5]:59.92","[6,6]:61.4","[6,7]:428.6","[6,8]:308.12"},
            /*[7]*/  {"[7,1]:62.24","[7,2]:91.76","[7,3]:33.72","[7,4]:43.76","[7,5]:355.48","[7,6]:88.96","[7,7]:186","[7,8]:338.96"},
            /*[8]*/  {"[8,1]:167.16","[8,2]:134.36","[8,3]:37","[8,4]:42.64","[8,5]:60","[8,6]:28.12","[8,7]:80.44","[8,8]:50.96"}


   };

    private final String[][] VectoresPromediados = new String[][] {

            /*[1]*/      {"-81,-90,-95,-93,-85" , "-92,-92,-93,-99,-88", "-92,-91,-89,-91,-90","-88,-88,-96,-99,-91"
            /*[1]*/           ,"-90,-92,-96,-92,-91","-91,-90,-92,-89,-92","-90,-89,-92,-86,-91","-89,-91,-89,-82,-92"},
            /*[2]*/     {"-89,-90,-91,-89,-90" ,"-90,-89,-92,-91,-93","-93,-91,-91,-89,-85","-87,-91,-91,-88,-85"
            /*[2]*/     ,"-90,-90,-91,-88,-88","-91,-96,-91,-91,-88","-93,-92,-91,-91,-91","-92,-90,-93,-90,-92"},
            /*[3]*/   {"-87,-87,-91,-91,-89","-87,-91,-93,-93,-88","-91,-92,-88,-91,-85","-90,-91,-93,-91,-81"
            /*[3]*/      ,"-89,-89,-91,-91,-87","-92,-95,-91,-83,-95","-96,-96,-92,-84,-91","-91,-94,-92,-87,-95"},
            /*[4]*/    {"-88,-88,-89,-92,-87","-90,-87,-87,-89,-85","-90,-92,-88,-92,-83","-91,-91,-93,-88,-85"
            /*[4]*/    ,"-90,-94,-92,-100,-88","-90,-93,-91,-92,-87","-95,-95,-91,-86,-90","-92,-92,-90,-86,-86"},
            /*[5]*/  {"-92,-87,-91,-92,-91","-92,-92,-90,-92,-90","-93,-87,-90,-91,-89","-91,-89,-90,-94,-83"
            /*[6]*/   ,"-92,-90,-92,-93,-82","-93,-91,-89,-93,-92","-92,-90,-91,-93,-91","-93,-96,-91,-92,-90"},
            /*[6]*/  {"-92,-90,-90,-93,-91","-91,-86,-88,-92,-87","-92,-89,-92,-89,-89","-91,-90,-89,-90,-92"
            /*[6]*/     ,"-92,-93,-87,-91,-93","-95,-90,-91,-92,-91","-93,-92,-88,-93,-90","-94,-92,-88,-93,-88"},
            /*[7]*/  {"-93,-86,-93,-92,-89","-91,-90,-92,-92,-87","-92,-88,-91,-93,-90","-92,-91,-90,-91,-93"
            /*[7]*/   ,"-92,-91,-84,-93,-93","-90,-89,-88,-91,-93","-93,-90,-83,-99,-92","-93,-93,-84,-91,-92"},
            /*[8]*/  {"-96,-82,-89,-92,-91","-93,-83,-89,-93,-92","-91,-88,-87,-88,-89","-96,-93,-86,-92,-89"
            /*[8]*/  ,"-91,-91,-83,-92,-91","-93,-91,-79,-91,-88","-91,-93,-83,-92,-90","-97,-94,-81,-91,-89"}

    };

    private ListAdapter_BTLE_Devices adapter;
        private ListView listView;
        private TextView err;

        private BroadcastReceiver_BTState mBTStateUpdateReceiver;
        private Scanner_BTLE mBTLeScanner;
    private Button export;
    private LinearLayout background ;
    FloatingActionButton fab;
    int Buttonflag = 0;

    EditText nombre,numero,correo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        export = (Button) findViewById(R.id.exportar);
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDB();
            }
        });

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Utils.toast(getApplicationContext(), "BLE not supported");
            finish();
        }
        mBTStateUpdateReceiver = new BroadcastReceiver_BTState(getApplicationContext());
        mBTLeScanner = new Scanner_BTLE(this, 20000, -95 );
        mBTDevicesHashMap = new HashMap<>();
        mBTDevicesArrayList = new ArrayList<>();

        adapter = new ListAdapter_BTLE_Devices(this, R.layout.btle_device_list_item, mBTDevicesArrayList);

        listView = new ListView(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        ((ScrollView) findViewById(R.id.scrollView)).addView(listView);
        err = (EditText) findViewById(R.id.errormessage);
         fab = (FloatingActionButton) findViewById(R.id.fab);
        background = (LinearLayout) findViewById(R.id.backgr);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Scanning Beacons ...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Buttonflag = 1;

                //fab.setBackgroundTintList(ColorStateList.valueOf());
                //   fab.setBackgroundColor(R.color.colorPrimaryDark);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
//                Utils.toast(getApplicationContext(), "Thank you for turning on Bluetooth");
            }
            else if (resultCode == RESULT_CANCELED) {
                Utils.toast(getApplicationContext(), "Please turn on Bluetooth");
            }
        }
        else if (requestCode == BTLE_SERVICES) {
            // Do something
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_clientes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_add:
                    //You can remove elements while iterating
                    //TODO agregar sentencias correctas de sqlit
               insertInSQLite(ArrayMacAddress[0],ArrayRSSI[0],
                       ArrayMacAddress[1],ArrayRSSI[1],
                       ArrayMacAddress[2],ArrayRSSI[2],
                       ArrayMacAddress[3],ArrayRSSI[3],
                       ArrayMacAddress[4],ArrayRSSI[4]);
                Toast.makeText(this, "Data insert succesfuly", Toast.LENGTH_SHORT).show();

                    //TODO HASTA AQUIIIIIIIIIII!!
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
        //noinspection SimplifiableIfStatement



    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fab:
//                Utils.toast(getApplicationContext(), "Scan Button Pressed");
                if (!mBTLeScanner.isScanning()) {
                    startScan();


                }
                else {
                    stopScan();
                    fab.setBackgroundTintList(new ColorStateList(new int[][]
                            {new int[]{0}}, new int[]{getResources().getColor(R.color.Emerald)}));

                }

                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       /* Context context = view.getContext();
       Utils.toast(context, "List Item clicked");
        // do something with the text views and start the next activity.
        stopScan();
        String name = mBTDevicesArrayList.get(position).getName();
        String address = mBTDevicesArrayList.get(position).getAddress();
*/

    }

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

    public void startScan(){
        //Utils.toast(getApplicationContext(),"Scanning...");
        mBTDevicesArrayList.clear();
        mBTDevicesHashMap.clear();
        mBTLeScanner.start();
    }

    public void stopScan() {
        mBTLeScanner.stop();


        //TODO: vaciar el hashmap a un vector 
        if (Buttonflag != 0) {

            try {
                for (String key : mBTDevicesHashMap.keySet()) {
                    //   Toast.makeText(this, key + " : " + mBTDevicesHashMap.get(key).getRSSI(), Toast.LENGTH_LONG).show();
                    if (MyBeaconsMac[0].equals(key)) {
                        ArrayMacAddress[0] = key;
                        ArrayRSSI[0] = String.valueOf(mBTDevicesHashMap.get(key).getRSSI());
                    }
                    if (MyBeaconsMac[1].equals(key)) {
                        ArrayMacAddress[1] = key;
                        ArrayRSSI[1] = String.valueOf(mBTDevicesHashMap.get(key).getRSSI());
                    }
                    if (MyBeaconsMac[2].equals(key)) {
                        ArrayMacAddress[2] = key;
                        ArrayRSSI[2] = String.valueOf(mBTDevicesHashMap.get(key).getRSSI());
                    }
                    if (MyBeaconsMac[3].equals(key)) {
                        ArrayMacAddress[3] = key;
                        ArrayRSSI[3] = String.valueOf(mBTDevicesHashMap.get(key).getRSSI());
                    }
                    if (MyBeaconsMac[4].equals(key)) {
                        ArrayMacAddress[4] = key;
                        ArrayRSSI[4] = String.valueOf(mBTDevicesHashMap.get(key).getRSSI());
                    }

                }
                for (int i = 0; i < MyBeaconsMac.length; i++) {
                    if (ArrayMacAddress[i].equals(String.valueOf(i + 1))) {
                        ArrayMacAddress[i] = MyBeaconsMac[i];
                    }
                }

            } catch (Exception e) {
                Toast.makeText(this, "error men: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

//deaqui
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Write your message here.");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ArrayList<String> ArraySimilares = new ArrayList<>();
                            int contador = 0;
                            //   ArraySimilares.add("Resultante:"+Arrays.asList(ArrayRSSI).toString()+"\n"+"similares:");
                            for (int rows = 0; rows < MatrizRestaCuadratica.length; rows++) {
                                for (int columns = 0; columns < MatrizRestaCuadratica[rows].length; columns++) {
                                    //de la mitad de la matriz en adelante
                                    String[] StaticVector = VectoresPromediados[rows][columns].split(",");
                                    //TODO: aqui hago el calculo de la resta cuadratica
                                    Double ResultadoRestaCuadratica = 0.0;
                                    for (int i = 0; i < 5; i++) {
                                        Double res = 0.0;
                                        res = Double.valueOf(StaticVector[i]) - Double.valueOf(ArrayRSSI[i]);
                                        ResultadoRestaCuadratica += Math.pow(res, 2);
                                    }

                                    //ArraySimilares.add("R:"+ResultadoRestaCuadratica.toString());
                                    contador++;
                                    if (contador == 8) {
                                        //  ArraySimilares.add("\n");
                                        contador = 0;
                                    }

                                    //Toast.makeText(this, "resultado resta cuadratica: "+ ResultadoRestaCuadratica.toString(), Toast.LENGTH_SHORT).show();
                                    // TODO: 19/07/2017 aqui hacer la comparacion +- 1 contra cada elemento de la matrizRestaCuadratica
                                    //// TODO: 19/07/2017 se hara una brecha mas grande los datos generados no son parecidos
                                    String[] splitvector = MatrizRestaCuadratica[rows][columns].split(":");
                                    Double StaticRestaCuadratica = Double.valueOf(splitvector[1]);
                                    String coordenada = splitvector[0];
                                    Double umbralSuperior = ResultadoRestaCuadratica + 2;
                                    Double umbralInferior = ResultadoRestaCuadratica - 2;
                                    if (StaticRestaCuadratica <= umbralSuperior && StaticRestaCuadratica >= umbralInferior) {
                                        ArraySimilares.add("/s/" + coordenada);

                                    }

                                } // end innner loop
                            } // end upper loop
                            String similares = "";
                            try {
                            for (String elemento : ArraySimilares) {
                                //// TODO: 21/07/2017 falta guardar en el hashmap los valores de la calibracion para despues compararlos  
                                if(CalibracionHashMap.containsKey(elemento)){
                                    Integer.valueOf(CalibracionHashMap.get(elemento));
                                }
                                similares += elemento + "\n";
                            }
                            err.setText(similares);
                        }
                            catch (Exception ex){
                                Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        
                        }
                     
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();

    }

        Buttonflag=0;
        //err.setText("ya se acabo el pex pushale de nuevo -->");
    }

    public void insertInSQLite(String maddress1, String rss1,
                               String maddress2, String rss2,
                               String maddress3, String rss3,
                               String maddress4, String rss4,
                               String maddress5, String rss5){
        AdminSQLiteHelper admin = new AdminSQLiteHelper(getApplicationContext(),"EnviromentalIntel.db",null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        if(db!=null){
            String sql = "INSERT INTO BeaconInfo(MAC1,RSSI1,MAC2,RSSI2,MAC3,RSSI3,MAC4,RSSI4,MAC5,RSSI5) VALUES"+
                    "( '"+maddress1+"' ,'"+rss1+"', " +
                    "'"+maddress2+"','"+rss2+"', " +
                    "'"+maddress3+"','"+rss3+"', " +
                    "'"+maddress4+"','"+rss4+"', " +
                    "'"+maddress5+"','"+rss5+"'" +
                    ")";
            db.execSQL(sql);
            db.close();
        }
    }
    private void exportDB(){
    String NAME_DB= "EnviromentalIntel.db";
;    File sd = Environment.getExternalStorageDirectory();
    File data = Environment.getDataDirectory();
    FileChannel source=null;
    FileChannel destination=null;
    String currentDBPath = "/data/"+ "com.example.eider.4bracadatabase" +"/databases/"+NAME_DB;
    String backupDBPath = NAME_DB;
    File currentDB = new File(data, currentDBPath);
    File backupDB = new File(sd, backupDBPath);

       try {
        source = new FileInputStream(currentDB).getChannel();
        destination = new FileOutputStream(backupDB).getChannel();
        destination.transferFrom(source, 0, source.size());
        source.close();
        destination.close();
        Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
    } catch(IOException e) {
           Toast.makeText(this, "error: " + e.getMessage(), Toast.LENGTH_LONG).show();

       }
}



}


