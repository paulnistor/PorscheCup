package com.highmobility.samples.androidscaffold;

import android.util.Log;

import com.highmobility.autoapi.Charging;
import com.highmobility.autoapi.Climate;
import com.highmobility.autoapi.Command;
import com.highmobility.autoapi.CommandResolver;
import com.highmobility.autoapi.Diagnostics;
import com.highmobility.crypto.value.DeviceSerial;
import com.highmobility.hmkit.HMKit;
import com.highmobility.hmkit.Telematics;
import com.highmobility.hmkit.error.TelematicsError;
import com.highmobility.value.Bytes;

public class Vehicle {

    private DeviceSerial serial;
    private double batteryLevel;
    private double speed;
    private double outsideTemperature;
    private String hvacStatus= "test";

    Vehicle(DeviceSerial serial){

        this.serial = serial;
    }
    ////////////////////////////////Block Start: memberAccess///////////////////////////////////////
    public double getBatteryLevel(){
        updateBatteryLevel();
        return this.batteryLevel;
    };

    public double getSpeed(){
        updateSpeed();
        return this.speed;
    };

    public double getOutsideTemperature(){
        updateOutsideTemperature();
        return this.outsideTemperature;
    }

    public String getHvacStatus(){
        updateHvacStatus();
        return this.hvacStatus;
    }
    //////////////////////////////////Block End: memberAccess///////////////////////////////////////

    //////////////////////////////////Block Start: setNewValues/////////////////////////////////////
    private void setBatteryLevel(double batteryLevel){
        this.batteryLevel = batteryLevel;
    }

    private void setOutsideTemperature(double outsideTemperature){
        this.outsideTemperature = outsideTemperature;
    }

    private void setSpeed(double speed){
        this.speed = speed;
    }

    private void setHvacStatus(String hvacStatus){
        this.hvacStatus = hvacStatus;
    }
    //////////////////////////////////Block End: setNewValues///////////////////////////////////////

    ////////////////////////////////Block Start: findNewValues//////////////////////////////////////
    private void updateBatteryLevel() {
        HMKit.getInstance().getTelematics().sendCommand(new Charging.GetState(), this.serial, new Telematics.CommandCallback() {
            @Override
            public void onCommandResponse(Bytes bytes) {
                Command command = CommandResolver.resolve(bytes);
                Charging.State charging = (Charging.State) command;
                setBatteryLevel(charging.getBatteryLevel().getValue().floatValue());
            }

            @Override
            public void onCommandFailed(TelematicsError error) {
                Log.d("commandError", error.getMessage());
            }
        });
    }

    private void updateOutsideTemperature() {
        HMKit.getInstance().getTelematics().sendCommand(new Climate.GetState(), this.serial, new Telematics.CommandCallback() {
            @Override
            public void onCommandResponse(Bytes bytes) {
                Command command = CommandResolver.resolve(bytes);
                Climate.State climate = (Climate.State) command;
                setOutsideTemperature(climate.getOutsideTemperature().getValue().inCelsius());
            }

            @Override
            public void onCommandFailed(TelematicsError error) {
                Log.d("commandError", error.getMessage());
            }
        });

    }

    private void updateSpeed() {
        HMKit.getInstance().getTelematics().sendCommand(new Diagnostics.GetState(), this.serial, new Telematics.CommandCallback() {
            @Override
            public void onCommandResponse(Bytes bytes) {
                Command command = CommandResolver.resolve(bytes);
                Diagnostics.State diagnostic = (Diagnostics.State) command;
                setSpeed(diagnostic.getSpeed().getValue().getValue());
            }

            @Override
            public void onCommandFailed(TelematicsError error) {
                Log.d("commandError", error.getMessage());
            }
        });
    }

    private void updateHvacStatus() {
        HMKit.getInstance().getTelematics().sendCommand(new Climate.GetState(), this.serial, new Telematics.CommandCallback() {
            @Override
            public void onCommandResponse(Bytes bytes) {
                Command command = CommandResolver.resolve(bytes);
                Climate.State climate = (Climate.State) command;
                setHvacStatus(climate.getHvacState().getValue().toString());
            }

            @Override
            public void onCommandFailed(TelematicsError error) {
                Log.d("commandError", error.getMessage());
            }
        });
    }
    ////////////////////////////////Block End: findNewValues/////////////////////////////////////
}
