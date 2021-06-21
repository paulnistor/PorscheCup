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
import android.widget.TextView;

import com.highmobility.autoapi.Capabilities;
import com.highmobility.autoapi.Charging;
import com.highmobility.autoapi.Command;
import com.highmobility.autoapi.CommandResolver;
import com.highmobility.autoapi.Diagnostics;
import com.highmobility.autoapi.Doors;
import com.highmobility.autoapi.FailureMessage;
import com.highmobility.autoapi.VehicleInformation;
import com.highmobility.autoapi.VehicleLocation;
import com.highmobility.autoapi.value.Location;
import com.highmobility.autoapi.value.Lock;
import com.highmobility.autoapi.value.LockState;
import com.highmobility.crypto.value.DeviceSerial;
import com.highmobility.hmkit.Broadcaster;
import com.highmobility.hmkit.BroadcasterListener;
import com.highmobility.hmkit.ConnectedLink;
import com.highmobility.hmkit.ConnectedLinkListener;
import com.highmobility.hmkit.HMKit;
import com.highmobility.hmkit.Link;
import com.highmobility.hmkit.Telematics;
import com.highmobility.hmkit.error.AuthenticationError;
import com.highmobility.hmkit.error.BroadcastError;
import com.highmobility.hmkit.error.DownloadAccessCertificateError;
import com.highmobility.hmkit.error.LinkError;
import com.highmobility.hmkit.error.TelematicsError;
import com.highmobility.value.Bytes;

import org.w3c.dom.Text;

import timber.log.Timber;

import static timber.log.Timber.d;
import static timber.log.Timber.e;

public class MainActivity extends Activity {

    Button btn;
    TextView txt;
    DeviceSerial xSerial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn);
        txt = findViewById(R.id.txt);
        Timber.plant(new Timber.DebugTree()); // enable HMKit logging
        /*
         * Before using HMKit, you'll have to initialise the HMKit singleton
         * with a snippet from the Platform Workspace:
         *
         *   1. Sign in to the workspace
         *   2. Go to the LEARN section and choose Android
         *   3. Follow the Getting Started instructions
         *
         * By the end of the tutorial you will have a snippet for initialisation,
         * that looks something like this:
         *
         *   HMKit.getInstance().initialise(
         *     Base64String,
         *     Base64String,
         *     Base64String,
         *     getApplicationContext()
         *   );
         */

        // PASTE THE SNIPPET HERE
        HMKit.getInstance().initialise(
                "dGVzdAQMe2ocy/rrvZueL9uvabpY1Cv/n30fRYWgukeOHcQCHLePdX1a33cgQ+6rKaR+E4l615VDjcMgHa/YR/p60n7pdEbZngdfJ1mFqOetQ6k1fc/UurMJNIACBQga9vCL2IgWKyJr164/5d8+tvLYXqAcubNas9vECAoqLHuYUoVTIdJzBLhJ9IR4VDb+WSn6Ssp/t8Ax",
                "zykD/yIDdysaOtWPJoPY4J+BlLahzfBorRW/VwrFm/c=",
                "28oiPZsx60QZbdiLTOHfFOOcPyVlsaufJhfsFRmynLXvfUviQ5oSOGLcDlxY17TKCn6sqO18HDoBa8nigW5ujQ==",
                getApplicationContext()
        );


        /*
         * Also, the access token from the emulator should be pasted here
         */
        String accessToken = "f80014e6-af21-4563-9e29-911c9934fad0";

        HMKit.getInstance().downloadAccessCertificate(accessToken, new HMKit.DownloadCallback() {
            @Override
            public void onDownloaded(DeviceSerial serial) {
                d("Certificate downloaded for vehicle: %s", serial);
                // Send command to the car through Telematics, make sure that the emulator is
                // opened for this to work, otherwise "Vehicle asleep" will be returned
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
        new CountDownTimer( 60000, 1000) {
            @Override
            public void onTick(long l) {
                //Log.d("Speed: ", String.valueOf(vehicle.getSpeed()));
                //Log.d("ChargingLevel: ", String.valueOf(vehicle.getBatteryLevel()));
                //Log.d("OutsideTemp: ", String.valueOf(vehicle.getOutsideTemperature()));
                Log.d("HvacStatus: ", vehicle.getHvacStatus());
            }

            @Override
            public void onFinish() {

            }
        }.start();

    }

}