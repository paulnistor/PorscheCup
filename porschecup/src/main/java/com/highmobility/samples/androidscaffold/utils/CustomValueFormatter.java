package com.highmobility.samples.androidscaffold.utils;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class CustomValueFormatter extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        if (value >= 10) {
            return String.valueOf((int)value);
        }

        return "";
    }
}
