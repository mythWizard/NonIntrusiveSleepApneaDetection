package com.example.apnea;

import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

public class MainActivity extends AppCompatActivity{

    private BluetoothAdapter ba;
    private Set<BluetoothDevice> pairedDevices;
    private LineChart chart;
    //private Calendar cal = Calendar.getInstance();
    //private SimpleDateFormat sdf;
    private List<Entry> entries;
    private LineDataSet dataSet;

    private Calendar c, now;
    double noon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //onCreateProcess();

        Button scanner = (Button)findViewById(R.id.scanner);
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFunc();
            }
        });

        //serialBegin(9600);

        chart = (LineChart) findViewById(R.id.chart);
        //chart.getXAxis().setDrawGridLines(false);
        chart.getLegend().setEnabled(false);

        //sdf = new SimpleDateFormat("HH:mm:ss");

        Vector<dataPoint> dataObjects = new Vector<dataPoint>();
        //Date d = new Date();

        c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        noon = c.getTimeInMillis();
        /*
        for (int i = 0; i < 10; ++i) {
            dataPoint dp = new dataPoint();
            dp.res = (float) 55.7 * i;
            dp.time = (System.currentTimeMillis() / 1000 - noon);
            dataObjects.add(dp);
        }*/

        //chart.moveViewToX(noon);
        //chart.zoom(1, 0, noon, 0);

        /*dataSet = new LineDataSet(entries, "data"); // add entries to dataset
        dataSet.setColor(R.color.nord7);
        dataSet.setValueTextColor(R.color.nord4); // styling, ...
        */
        LineData lineData = new LineData();
        //dataSet.setValueTextColor(R.color.nord4);
        //dataSet.setCircleColor(R.color.nord7);

        chart.setData(lineData);
        //chart.invalidate(); // refresh
    }

    public void clickFunc() {
        double n = System.currentTimeMillis() - noon;
        Random rand = new Random();
        float d = rand.nextFloat();
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

        /*dataSet.addEntry(new Entry(dataSet.getEntryCount(), d));
        chart.notifyDataSetChanged();
        chart.setVisibleXRangeMaximum(6);
        chart.moveViewToX(dataSet.getEntryCount() - 7);

        for(int i = 0; i < dataSet.getEntryCount(); ++i){
            System.out.println(dataSet.getEntryForIndex(i).getX());
            //System.out.println(dataSet.getEntryForIndex(i).getY());
            //System.out.println(System.currentTimeMillis() - noon);
        }
        System.out.println("\n");
        */
        TextView timeText = (TextView) findViewById(R.id.time);
        timeText.setText(String.valueOf(n));

        TextView dataText = (TextView) findViewById(R.id.data);
        dataText.setText(String.valueOf(d));
    }

    private LineDataSet createSet(){
        LineDataSet set = new LineDataSet(null, "Resistance Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(R.color.nord7);
        set.setValueTextColor(R.color.nord4);
        return set;
    }
}

