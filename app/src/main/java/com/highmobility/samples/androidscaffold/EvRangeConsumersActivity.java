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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.highmobility.autoapi.Climate;
import com.highmobility.autoapi.Command;
import com.highmobility.autoapi.CommandResolver;
import com.highmobility.crypto.value.DeviceSerial;
import com.highmobility.hmkit.HMKit;
import com.highmobility.hmkit.Telematics;
import com.highmobility.hmkit.error.DownloadAccessCertificateError;
import com.highmobility.hmkit.error.TelematicsError;
import com.highmobility.samples.androidscaffold.utils.Vehicle;
import com.highmobility.value.Bytes;

import java.text.DecimalFormat;

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



    public void onClick(View v){
        Vehicle vehicle = new Vehicle(xSerial);
        DecimalFormat df2 = new DecimalFormat("#.##");
        progressBarClima.setProgress(10);
        progressBarBatteryLevel.setProgress(100);
        progressBarOutsideTemp.setProgress(20);
        new CountDownTimer( 60000, 1000) {
            @Override
            public void onTick(long l) {

                progressBarSpeed.setProgress((int) vehicle.getSpeed());
                txtKmH.setText(String.valueOf(vehicle.getSpeed())+ " Km/h");


                String outsideTemp = df2.format(vehicle.getOutsideTemperature());
                txtOutsideTemperature.setText(outsideTemp + " °C");

                double btLevel = vehicle.getBatteryLevel();
                double btLevelMultiplied = btLevel * 100;
                int batteryLevelFinal = (int) btLevelMultiplied;

                progressBarBatteryLevel.setProgress(batteryLevelFinal);

                txtEstimatedRange.setText(String.valueOf(vehicle.getBatteryRange())+ " Km/h");


            }

            @Override
            public void onFinish() {

            }
        }.start();

    }



}