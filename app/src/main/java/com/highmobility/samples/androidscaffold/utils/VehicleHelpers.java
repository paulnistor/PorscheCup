package com.highmobility.samples.androidscaffold.utils;

public class VehicleHelpers {
    public static float getSpeedBatteryUsage(double speed) {
        if (speed > 90) {
            return 30f;
        }

        return 20f;
    }

    public static float getClimateBatterUsage(boolean isOn) {
        return isOn ? 10f : 0f;
    }

    public static float getOutsideTemperatureBatteryUsage(double outsideTemperature) {
        if (outsideTemperature > 5) {
            return 0f;
        }

        return 20f;
    }
}
