package com.financial.api.util;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberFormatter {

    // Private constructor to prevent instantiation
    private NumberFormatter() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String format(Number amount) {
        NumberFormat formatter = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);
        formatter.setMaximumFractionDigits(2);
        return formatter.format(amount);
    }
}