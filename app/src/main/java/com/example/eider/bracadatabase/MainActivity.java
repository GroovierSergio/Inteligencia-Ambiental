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
//// TODO: 24/07/2017 agregar matriz de restas cuadratica
           /*[1]*/      {"4,6.76,0.64 ,1.44 ,5.76","4,12.96,0.36,0.04,17.64","33.64,14.44,0.04,33.64,0.04","17.64,36,14.44,9,0.04"},
           /*[2]*/     {"2.56,16,2.56,14.44,3.24","16,77.44,31.36,2.56,11.56","12.96,57.76,1.96,0.36,5.76","1.96,7.84,6.76,17.64,0.64"},
           /*[3]*/   {"2.56,54.76,0.64,3.24,0.16","2.56,0.36,1.96,17.64,9","4,0.64,43.56,0.16,19.36","1,9,2.56,1.96,46.24"},
           /*[4]*/    {"1.96,10.24,0.16,1.44,14.44","0.64,33.64,0.04,3.24,4.84","6.76,0.04,4,25,21.16","1.96,4.84,36,3.24,10.24"}

   };
    private final String[][] MatrizAtenuaciones = new String[][] {
//// TODO: 24/07/2017 matriz de atenuaciones
           /*[1]*/      {"-89,-85.6,-90.8,-90.8,-89.6","-90,-82.4,-93.4,-92.8,-88.8","-91.8,-89.8,-91.2,-88.2,-89.8","-89.8,-89,-92.8,-90,-93.2"},
           /*[2]*/     {"-92.6,-82,-87.6,-92.8,-88.8","-92,-81.2,-88.4,-91.6,-89.4","-92.6,-90.6,-92.6,-93.4,-90.6","-90.6,-89.2,-89.6,-88.8,-91.8"},
           /*[3]*/   {"-92.4,-85.4,-90.2,-92.2,-87.6","-92.4,-85.6,-92.6,-89.8,-88","-92,-85.2,-92.6,-90.6,-91.4","-91,-89,-89.4,-92.4,-85.2"},
           /*[4]*/    {"-92.6,-81.8,-91.4,-92.8,-86.2","-93.8,-81.2,-89.8,-88.2,-89.8","-91.4,-88.2,-87,-92,-89.4","-92.4,-87.8,-85,-91.2,-90.8"}

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

                    try {

                            ArrayList<String> ArraySimilares = new ArrayList<>();
                            int contador = 0;
                            for (int rows = 0; rows < MatrizRestaCuadratica.length; rows++) {
                                for (int columns = 0; columns < MatrizRestaCuadratica[rows].length; columns++) {
                                    //de la mitad de la matriz en adelante
                                    String[] StaticVector = MatrizRestaCuadratica[rows][columns].split(",");
                                    String[] atenuacionVector = MatrizAtenuaciones[rows][columns].split(",");


                                    //// TODO: 24/07/2017 aqui se hace la comparacion de vectores de resta cuadratica
                                    int comparacion = 0;
                                    String resultadorestacuadratica= "";
                                    Double res = 0.0;
                                    for (int i = 0; i < 5; i++) {
                                        res +=  Math.pow(Double.valueOf(atenuacionVector[i]) - Double.valueOf(ArrayRSSI[i]),2);
                                        res = Double.valueOf(Math.round(res));
                                        Double umbralSuperior = res + 2;
                                        Double umbralInferior = res - 2;
                                        if (Double.valueOf(StaticVector[i]) <= umbralSuperior &&
                                                Double.valueOf(StaticVector[i]) >= umbralInferior) {
                                            comparacion++;
                                        }
                                        resultadorestacuadratica+=res+", ";

                                    }
                                 if (comparacion>=2){
                                     ArraySimilares.add("s:"+Arrays.asList(StaticVector).toString());
                                 }

                                } // end innner loop
                            } // end upper loop
                        String similares = "";
                        for(String elemento : ArraySimilares){
                            similares+= elemento +"\n";
                        }
                        err.setText(similares);


                    }catch (Exception e){
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
    }

        Buttonflag=0;
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


