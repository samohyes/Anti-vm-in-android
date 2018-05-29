package com.example.xudong_shao.vm_test;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = (TextView) findViewById(R.id.show);


        Button button_qemuprop = (Button) findViewById(R.id.qemuprop);
        button_qemuprop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hasQEmuProps(textView, getApplicationContext());
            }
        });

        Button device_id = (Button) findViewById(R.id.deviceid);
        device_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hasKnownDeviceId(textView,MainActivity.this, getApplicationContext());
            }
        });

        Button qemu_pipe = (Button)findViewById(R.id.qemupipe);
        qemu_pipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hasPipes(textView);
            }
        });

        Button df_number = (Button) findViewById(R.id.defaultnumber);
        df_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hasKnownPhoneNumber(textView,MainActivity.this,getApplicationContext());
            }
        });

        Button IMSI = (Button) findViewById(R.id.IMSI);
        IMSI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hasKnownImsi(textView,MainActivity.this, getApplicationContext());
            }
        });

        Button build = (Button) findViewById(R.id.build);
        build.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hasEmulatorBuild(textView, getApplicationContext());
            }
        });

        Button operator = (Button) findViewById(R.id.operator);
        operator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.isOperatorNameAndroid(textView,getApplicationContext());
            }
        });

        Button qudriver = (Button) findViewById(R.id.qudriver);
        qudriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hasQEmuDrivers(textView);
            }
        });

        Button qufile = (Button) findViewById(R.id.qufile);
        qufile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hasQEmuFiles(textView);
            }
        });

        Button gyfile = (Button) findViewById(R.id.gyfile);
        gyfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hasGenyFiles(textView);
            }
        });

        Button Monkey = (Button) findViewById(R.id.Monkey);
        Monkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.isUserAMonkey(textView);
            }
        });

        Button debugger = (Button) findViewById(R.id.debugger);
        debugger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.isBeingDebugged(textView);
            }
        });

        Button ptrace = (Button) findViewById(R.id.ptrace);
        ptrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Utils.hasTracerPid(textView);
                }catch (IOException e){
                    textView.setText("IOException!");
                }
            }
        });
        /*
        Button tcp = (Button) findViewById(R.id.tcp);
        tcp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Utils.hasAdbInEmulator(textView);
                }catch (IOException e){
                    textView.setText("IOException!");
                }
            }
        });
        */

        Button eth0 = (Button) findViewById(R.id.eth0);
        eth0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hasEth0Interface(textView);
            }
        });

        Button taintdroid = (Button) findViewById(R.id.taintdroid);
        taintdroid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hasAppAnalysisPackage(getApplicationContext(),textView);
                Utils.hasTaintClass(textView);
            }
        });

        Button sensor = (Button)findViewById(R.id.sensor);
        sensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.detectsensor(textView,getApplicationContext());
            }
        });

        Button qemu_task = (Button)findViewById(R.id.qemu_task);
        qemu_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test","main in java!");
                String threadresult = mainthread();
                Log.d("test","result:"+threadresult+" length: "+ threadresult.length());
                textView.setText(threadresult);
            }
        });

        Button SMC = (Button)findViewById(R.id.SMC);
        SMC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String smc = SMCdetection();
                textView.setText(smc);
            }
        });

        Button bkpt = (Button)findViewById(R.id.bkpt);
        bkpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean bkptresult = checkQemuBreakpoint();
                if(bkptresult){
                    textView.setText("bkpt success! An emulator!");
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Utils.hasKnownDeviceId((TextView)findViewById(R.id.show),MainActivity.this, getApplicationContext());
                }else{
                    Toast.makeText(this,"You denied",Toast.LENGTH_SHORT);
                }

        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

    public native String stringFromJNI();
    public native String mainthread();
    public native String SMCdetection();


    public native int qemuBkpt();

    public boolean checkQemuBreakpoint() {
        boolean hit_breakpoint = false;

        // Potentially you may want to see if this is a specific value
        int result = qemuBkpt();

        if (result > 0) {
            hit_breakpoint = true;
        }

        return hit_breakpoint;
    }

}
