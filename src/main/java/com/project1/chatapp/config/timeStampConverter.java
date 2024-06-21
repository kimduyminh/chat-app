package com.project1.chatapp.config;

import org.springframework.context.annotation.Configuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
@Configuration
public class timeStampConverter {
    private static final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static String convertTimestamp(String inputTimestamp) {
        try {
            Date parsedDate = inputFormat.parse(inputTimestamp);
            return outputFormat.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
