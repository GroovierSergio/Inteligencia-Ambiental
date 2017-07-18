package com.example.eider.bracadatabase;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

        public HashMap<String, BTLE_Device> mBTDevicesHashMap;
        private ArrayList<BTLE_Device> mBTDevicesArrayList;

    String  ArrayMacAddress[] = {"1","2", "3", "4", "5"};
    String ArrayRSSI[] = {"-110","-110",  "-110","-110", "-110"};
        // you must change this mac addres array with your beacons mac adresses
        String[] MyBeaconsMac = {"F1:82:F5:F1:79:E8", "C0:62:E9:C5:37:F5", "F7:D4:CF:09:98:F0" ,"DD:BE:F9:A1:D8:99","D4:88:E6:45:E1:2B"};
   private final String[][] VectoresSuavizados = new String[][] {

           {"-90,-88,-86,-90,-88" , "-90,-90,-90,-91,-90", "-85,-93,-96,-89,-88","-93,-104,-96,-81,-92"},
           {"-90,-87,-91,-91,-87" ,"-91,-91,-90,-92,-88","-92,-92,-90,-92,-93","-91,-92,-90,-88,-88"},
           {"-96,-93,-88,-92,-85","-94,-90,-89,-92,-84","-91,-92,-85,-91,-84","-100,-94,-89,-96,-87"},
           {"-92,-86,-88,-92,-94","-90,-90,-88,-91,-87","-90,-92,-88,-91,-88","-93,-99,-81,-95,-89"}

   };

    private ListAdapter_BTLE_Devices adapter;
        private ListView listView;
        private TextView err;

        private BroadcastReceiver_BTState mBTStateUpdateReceiver;
        private Scanner_BTLE mBTLeScanner;
    private Button export;
        private FloatingActionButton bConsulta;
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
        mBTLeScanner = new Scanner_BTLE(this, 15000, -95 );
        mBTDevicesHashMap = new HashMap<>();
        mBTDevicesArrayList = new ArrayList<>();

        adapter = new ListAdapter_BTLE_Devices(this, R.layout.btle_device_list_item, mBTDevicesArrayList);

        listView = new ListView(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        ((ScrollView) findViewById(R.id.scrollView)).addView(listView);
        err = (TextView) findViewById(R.id.errormessage);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Alizarin)));

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
                //ya se ordeno el vector  ^
                
                for (int i = 0; i < MyBeaconsMac.length; i++) {
                    if (ArrayMacAddress[i].equals(String.valueOf(i + 1))) {
                        ArrayMacAddress[i] = MyBeaconsMac[i];
                    }
                }
                
            } catch (Exception e) {
                Toast.makeText(this, "error men: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                //err.setText(" error men: " + e.getMessage());
            }
            
            //TODO: comparar el vector de arriba con la matriz estatica
            
            try {
                ArrayList<String> ArraySimilares = new ArrayList<>();
                int MayorIndiceComparacion = 0;
                for (int rows = 0; rows < VectoresSuavizados.length; rows++) {
                    for (int columns = 0; columns < VectoresSuavizados[rows].length; columns++) {
                       //de la mitad de la matriz en adelante
                        //TODO: aqui spliteo el el vector en atenuaciones para compararlos
                        String[] StaticVector = VectoresSuavizados[rows][columns].split(",");

                        int comparacion = 0;
                        for (int i = 0; i < 5; i++) {
                            if (StaticVector[i].equals(ArrayRSSI[i])) {
                                comparacion++;
                            }

                            int intvectorplus = Integer.valueOf(ArrayRSSI[i])+1 ;
                            int intvectorminus = Integer.valueOf(ArrayRSSI[i])-1 ;

                            if(Integer.valueOf(StaticVector[i]) == intvectorplus ||
                                    Integer.valueOf(StaticVector[i]) == intvectorminus  ){
                                comparacion++;
                            }

                            if (comparacion == 5  ) {
                            }
                            if(comparacion==4){
                                err.setText(Arrays.asList(StaticVector).toString());
                            }
                            if (comparacion == 2){
                                //Toast.makeText(this,"["+rows+"]"+"["+columns+"] :::"+ StaticVector[i]+" : "+ArrayRSSI[i]+" :: "+comparacion, Toast.LENGTH_SHORT).show();
                                err.setText(Arrays.asList(StaticVector).toString());
                            }

                        }

                        //TODO : Aqui comparar cual se parece mas, guardar el vector y el numero de indices parecidos
                        if (comparacion > MayorIndiceComparacion) {
                            ArraySimilares.clear();
                            MayorIndiceComparacion = comparacion;
                            ArraySimilares.add(Arrays.asList(StaticVector).toString());
                        }
                        if (comparacion == MayorIndiceComparacion) {
                            ArraySimilares.add(Arrays.asList(StaticVector).toString());
                        }

                    }
                }
                String similares = "";
                for(String elemento : ArraySimilares){
                    similares+= elemento +"\n";
                }
                err.setText(similares);
                //TODO: aqui imprimir el arreglo de los que mas se parecen
               /* for (int i = 0; i < ArraySimilares.length; i++) {
                    Toast.makeText(this, Arrays.toString(Arrays.asList(ArraySimilares).get(i)), Toast.LENGTH_SHORT).show();
                }*/
                //TODO : aqui mando el arreglo de los que mas se parecen
               // Intent intent = new Intent(getApplicationContext(), Canvas.class);
                //intent.putExtra("ñose", "nose");
                //        startActivity(intent);

            }
            catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                err.setText("error prro"+e.getMessage());
            }


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


