package com.example.apnea;

import android.Manifest;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.View.OnClickListener;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

public class MainActivity extends BlunoLibrary {

    private Set<BluetoothDevice> pairedDevices;
    private LineChart chart;
    private List<Entry> entries;
    private LineDataSet dataSet;

    private UUID blunoServiceID = UUID.fromString("0000dfb0-0000-1000-8000-00805f9b34fb");
    private UUID blunoCharacteristicID = UUID.fromString("0000dfb1-0000-1000-8000-00805f9b34fb");
    private UUID blunoCommandID = UUID.fromString("0000dfb2-0000-1000-8000-00805f9b34fb");

    private Calendar c, now;
    double noon;
/*
    private BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback(){
        @Override
        public void onLeScan(final BluetoothDevice dev, int rssi, byte[] scanRecord){
            if(!scannedDevices.contains(dev) && dev.getName() != null){

            }
        }
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect beacons.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            });
            builder.show();
        }

        onCreateProcess();
        serialBegin(9600);

        Button scanner = (Button)findViewById(R.id.scanner);
        scanner.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonScanOnClickProcess();
            }
        });

/*
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();

        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()){
            Intent btEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(btEnable, 1);
        }

        mBluetoothAdapter.startLeScan(mLeScanCallback);
*/

        //Grab Beetle MAC address 1C:BA:8C:1D:33:84

        chart = (LineChart) findViewById(R.id.chart);
        chart.getLegend().setEnabled(false);
        //chart.setDescription(Description.setText(" "));

        Vector<dataPoint> dataObjects = new Vector<dataPoint>();

        c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        noon = c.getTimeInMillis();

        LineData lineData = new LineData();

        chart.setData(lineData);
    }

    protected void onResume(){
        super.onResume();
        onResumeProcess();
    }

    @Override
    protected void onPause(){
        super.onPause();
        onPauseProcess();
    }

    protected void onStop(){
        super.onStop();
        onStopProcess();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        onDestroyProcess();
    }

    public void addPoint(float d) {
        double n = System.currentTimeMillis() - noon;
        //Random rand = new Random();
        //float d = rand.nextFloat();
        //float d = Float.parseFloat(theString);

        LineData dSet = chart.getData();

        if(dSet != null){
            ILineDataSet set = dSet.getDataSetByIndex(0);
            if(set == null){
                set = createSet();
                dSet.addDataSet(set);
            }

            dSet.addEntry(new Entry((float)n, d), 0);
            dSet.notifyDataChanged();
            chart.notifyDataSetChanged();
            chart.setMaxVisibleValueCount(150);
            chart.moveViewToX(dSet.getEntryCount());
        }

        TextView timeText = (TextView) findViewById(R.id.centraln);
        timeText.setText(String.valueOf(n));

        TextView dataText = (TextView) findViewById(R.id.obstructiven);
        dataText.setText(String.valueOf(d));
    }

    private LineDataSet createSet(){
        LineDataSet set = new LineDataSet(null, "Resistance Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(R.color.nord7);
        set.setValueTextSize(0.0f);
        //set.setCircleHoleColor(R.color.nord7);
        //set.setDrawFilled(true);
        set.setValueTextColor(R.color.nord4);
        return set;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        onActivityResultProcess(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSerialReceived(String theString){
        //String delims = "#";
        //String tokens[] = theString.split(delims);
        //for(int i = 0; i < tokens.length; ++i){
        //    System.out.println(tokens[i]);
        //}
        //System.out.println(theString);
        ///System.out.println(theString.length());
        //System.out.println(tokens[1]);
        if(theString.length() == 18) {
            addPoint(Float.parseFloat(theString.substring(3, 8)));
        }
    }

    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {
        Button scanner = (Button)findViewById(R.id.scanner);

        switch (theConnectionState) {
            case isConnected:
                System.out.println("Connected");
                scanner.setText("Disconnect");
                break;
            case isConnecting:
                System.out.println("Connecting");
                break;
            case isToScan:
                System.out.println("To Scan");
                break;
            case isScanning:
                System.out.println("Scanning");
                break;
            case isDisconnecting:
                System.out.println("Disconnecting");
                //Button scanner = (Button)findViewById(R.id.scanner);
                scanner.setText("Scan");
                // Write to CSV?
                break;
            default:
                break;
        }
    }


}

