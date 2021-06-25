/*
 * The MIT License
 *
 * Copyright (c) 2014- High-Mobility GmbH (https://high-mobility.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.highmobility.samples.androidscaffold;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.highmobility.autoapi.Climate;
import com.highmobility.autoapi.Command;
import com.highmobility.autoapi.CommandResolver;
import com.highmobility.crypto.value.DeviceSerial;
import com.highmobility.hmkit.HMKit;
import com.highmobility.hmkit.Telematics;
import com.highmobility.hmkit.error.DownloadAccessCertificateError;
import com.highmobility.hmkit.error.TelematicsError;
import com.highmobility.samples.androidscaffold.utils.Vehicle;
import com.highmobility.samples.androidscaffold.utils.CustomValueFormatter;
import com.highmobility.samples.androidscaffold.utils.VehicleHelpers;
import com.highmobility.value.Bytes;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static timber.log.Timber.d;
import static timber.log.Timber.e;

public class EvRangeConsumersActivity extends Activity {

    Button btn;
    ProgressBar progressBarClima;
    ProgressBar progressBarSpeed;
    ProgressBar progressBarOutsideTemp;
    ProgressBar progressBarBatteryLevel;
    TextView txtKmH;
    TextView txtOutsideTemperature;
    TextView txtEstimatedRange;
    TextView txtBatteryLevelValue;
    HorizontalBarChart hBarChart;
    Switch switchClima;

    DeviceSerial xSerial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ev_range_consumers);
        btn = findViewById(R.id.btnStart);
        progressBarClima = findViewById(R.id.progressBarClima3);
        progressBarSpeed = findViewById(R.id.progressBarSpeed);
        progressBarOutsideTemp = findViewById(R.id.progressBarOutsideTemp);
        txtKmH = findViewById(R.id.txtKmH);
        txtOutsideTemperature = findViewById(R.id.txtOutsideTemp);
        progressBarBatteryLevel = findViewById(R.id.progressBarBatteryLevel);
        txtEstimatedRange = findViewById(R.id.txtEstimatedRange);
        switchClima = findViewById(R.id.switchClima);
        txtBatteryLevelValue = findViewById(R.id.textView8);



        Timber.plant(new Timber.DebugTree());
        HMKit.getInstance().initialise(
                "dGVzdAQMe2ocy/rrvZueL9uvabpY1Cv/n30fRYWgukeOHcQCHLePdX1a33cgQ+6rKaR+E4l615VDjcMgHa/YR/p60n7pdEbZngdfJ1mFqOetQ6k1fc/UurMJNIACBQga9vCL2IgWKyJr164/5d8+tvLYXqAcubNas9vECAoqLHuYUoVTIdJzBLhJ9IR4VDb+WSn6Ssp/t8Ax",
                "zykD/yIDdysaOtWPJoPY4J+BlLahzfBorRW/VwrFm/c=",
                "28oiPZsx60QZbdiLTOHfFOOcPyVlsaufJhfsFRmynLXvfUviQ5oSOGLcDlxY17TKCn6sqO18HDoBa8nigW5ujQ==",
                getApplicationContext()
        );

        String accessToken = "f80014e6-af21-4563-9e29-911c9934fad0";

        HMKit.getInstance().downloadAccessCertificate(accessToken, new HMKit.DownloadCallback() {
            @Override
            public void onDownloaded(DeviceSerial serial) {
                d("Certificate downloaded for vehicle: %s", serial);
                xSerial= serial;

            }

            @Override
            public void onDownloadFailed(DownloadAccessCertificateError error) {
                e("Could not download a certificate with token: %s", error.getMessage());
            }
        });

    }

    public void onClimaSwitch(View v) {
        if (switchClima.isChecked())
            progressBarClima.setProgress(10);
        else
            progressBarClima.setProgress(0);
    }

    public void onClick(View v){
        Vehicle vehicle = new Vehicle(xSerial);
        DecimalFormat df2 = new DecimalFormat("#.##");
        progressBarClima.setProgress(10);
        progressBarBatteryLevel.setProgress(100);
        progressBarOutsideTemp.setProgress(20);
        createHorizontalBarChart();
        new CountDownTimer( 60000, 1000) {
            @Override
            public void onTick(long l) {
                double vehicleSpeed = vehicle.getSpeed();
                progressBarSpeed.setProgress((int) vehicleSpeed);
                txtKmH.setText(String.valueOf(vehicleSpeed)+ " Km/h");


                double outsideTemperature = vehicle.getOutsideTemperature();
                txtOutsideTemperature.setText(df2.format(outsideTemperature) + " °C");

                double btLevel = vehicle.getBatteryLevel();
                double btLevelMultiplied = btLevel * 100;
                int batteryLevelFinal = (int) btLevelMultiplied;

                progressBarBatteryLevel.setProgress(batteryLevelFinal);
                txtBatteryLevelValue.setText(String.valueOf(batteryLevelFinal) + "%");

                txtEstimatedRange.setText(String.valueOf(vehicle.getBatteryRange())+ " Km/h");

                float climateUsage = VehicleHelpers.getClimateBatterUsage(switchClima.isChecked());
                float speedUsage = VehicleHelpers.getSpeedBatteryUsage(vehicleSpeed);
                float outsideTemperatureUsage = VehicleHelpers.getOutsideTemperatureBatteryUsage(outsideTemperature);
                float batteryUsed = 100 - batteryLevelFinal;
                float batteryAvailable = 100 - (climateUsage + speedUsage + outsideTemperatureUsage + batteryUsed);


                float values[] = { batteryAvailable, climateUsage, speedUsage, outsideTemperatureUsage, batteryUsed };

                updateHorizontalBarChart(values);
            }

            @Override
            public void onFinish() {

            }
        }.start();

    }

    private void createHorizontalBarChart() {
        hBarChart = findViewById(R.id.activity_main_hbarchart);
        configureHorizontalBarChart();
    }

    private void updateHorizontalBarChart(float[] entryValues) {
        createHorizontalBarChartData(entryValues);
        hBarChart.invalidate();
    }

    private void configureHorizontalBarChart() {
        hBarChart.getDescription().setEnabled(false);
        hBarChart.getLegend().setEnabled(false);
        hBarChart.getAxisLeft().setDrawLabels(false);
        hBarChart.getAxisLeft().setDrawAxisLine(false);
        hBarChart.getAxisLeft().setDrawGridLines(false);
        hBarChart.getAxisRight().setDrawLabels(false);
        hBarChart.getAxisRight().setDrawAxisLine(false);
        hBarChart.getAxisRight().setDrawGridLines(false);
        hBarChart.getXAxis().setDrawLabels(false);
        hBarChart.getXAxis().setDrawAxisLine(false);
        hBarChart.getXAxis().setDrawGridLines(false);
        hBarChart.setDrawValueAboveBar(false);
        hBarChart.setFitBars(true);
    }

    private void createHorizontalBarChartData(float[] entryValues) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, entryValues));

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(getApplicationContext(), R.color.chart_available));
        colors.add(ContextCompat.getColor(getApplicationContext(), R.color.chart_climate));
        colors.add(ContextCompat.getColor(getApplicationContext(), R.color.chart_speed));
        colors.add(ContextCompat.getColor(getApplicationContext(), R.color.chart_hwac));
        colors.add(ContextCompat.getColor(getApplicationContext(), R.color.chart_used));

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(14f);
        dataSet.setValueFormatter(new CustomValueFormatter());
        dataSet.setDrawIcons(false);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        hBarChart.setData(barData);
    }
}