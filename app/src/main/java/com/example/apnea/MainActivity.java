package com.example.apnea;

import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

public class MainActivity extends AppCompatActivity{

    private BluetoothAdapter ba;
    private Set<BluetoothDevice> pairedDevices;
    private LineChart chart;
    private List<Entry> entries;
    private LineDataSet dataSet;

    private Calendar c, now;
    double noon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button scanner = (Button)findViewById(R.id.scanner);
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFunc();
            }
        });

        chart = (LineChart) findViewById(R.id.chart);
        chart.getLegend().setEnabled(false);

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

