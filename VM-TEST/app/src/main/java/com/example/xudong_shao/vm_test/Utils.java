package com.example.xudong_shao.vm_test;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Debug;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class Utils {
    // system properties
    private static Property[] known_props = {new Property("init.svc.qemud", null),
            new Property("init.svc.qemu-props", null), new Property("qemu.hw.mainkeys", null),
            new Property("qemu.sf.fake_camera", null), new Property("qemu.sf.lcd_density", null),
            new Property("ro.bootloader", "unknown"), new Property("ro.bootmode", "unknown"),
            new Property("ro.hardware", "goldfish"), new Property("ro.kernel.android.qemud", null),
            new Property("ro.kernel.qemu.gles", null), new Property("ro.kernel.qemu", "1"),
            new Property("ro.product.device", "generic"), new Property("ro.product.model", "sdk"),
            new Property("ro.product.name", "sdk"),
            new Property("ro.serialno", null)};
    private static int MIN_PROPERTIES_THRESHOLD = 0x5;
    private static String qemu_property;
    public static void hasQEmuProps(TextView textView, Context context) {
        int found_props = 0;
        for (Property property : known_props) {
            String property_value = Syspro.getProp(context, property.name);
            /*
             * See if we expected just a non-null
             * if the property value is not null, emulator!
             * the source code it provides has some problem,
             * I change the property_value.equals("")
             */
            Log.d("test",property.name+":"+property_value );
            if ((property.seek_value == null) && (!property_value.equals(""))) {
                //Log.d("test",property.name+":"+property_value );
                qemu_property += (property.name+":"+property_value+"##");
                //return true;
            }
            // See if we expected a value to seek
            // the expected value is there, emulator!
            if ((property.seek_value != null) && (property_value.indexOf(property.seek_value) != -1)) {
                //Log.d("test",property.name+":"+property_value);
                qemu_property += (property.name+":"+property_value+"##");
                //return true;
            }
        }
        textView.setText(qemu_property);
        //return false;
    }

    // Device ID (In Nexus 5X API 25 Android 7.1.1 we can't detect any Device ID of this)

    private static String[] known_device_ids = {"000000000000000", // Default emulator id
            "e21833235b6eef10", // VirusTotal id
            "012345678912345"};
    public static boolean hasKnownDeviceId(TextView textView, AppCompatActivity act, Context context) {

        if(PermissionUtils.isLacksOfPermission(context, PermissionUtils.PERMISSION[0])) {
            ActivityCompat.requestPermissions(act, PermissionUtils.PERMISSION, 0x12);
            Log.d("test","No permission");
            return false;
        }else{
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            String deviceId = telephonyManager.getDeviceId();
            Log.d("deviceId:",deviceId);

            for (String known_deviceId : known_device_ids) {
                if (known_deviceId.equalsIgnoreCase(deviceId)) {
                    Log.d("test","find : "+known_deviceId);
                    textView.setText("deviceid: "+ deviceId);
                    return true;
                }
            }
            Log.d("test","No deviceID!");
            textView.setText("Not find deviceid: "+deviceId);
            return false;
        }
    }

    // qemu pipes testing
    private static String[] known_pipes = {"/dev/socket/qemud", "/dev/qemu_pipe"};
    public static boolean hasPipes(TextView textView) {
        for (String pipe : known_pipes) {
            File qemu_socket = new File(pipe);
            if (qemu_socket.exists()) {
                Log.d("test","find qemu pipes");
                textView.setText("find qemu pipes: "+pipe);
                return true;
            }
        }
        textView.setText("Not find qemu pipes!");
        return false;
    }

    // Default Number
    private static String[] known_numbers = {
            "+15555215554", // default number + VirusTotal
            "+15555215556", "+15555215558", "+15555215560", "+15555215562", "+15555215564", "+15555215566",
            "+15555215568", "+15555215570", "+15555215572", "+15555215574", "+15555215576", "+15555215578",
            "+15555215580", "+15555215582", "+15555215584",};
    public static boolean hasKnownPhoneNumber(TextView textView, AppCompatActivity act, Context context) {
        if (PermissionUtils.isLacksOfPermission(context, PermissionUtils.PERMISSION[0])) {
            ActivityCompat.requestPermissions(act, PermissionUtils.PERMISSION, 0x12);
            Log.d("test", "No permission");
            return false;
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String phoneNumber = telephonyManager.getLine1Number();
            Log.d("phoneNumber:",phoneNumber);
            for (String number : known_numbers) {
                if (number.equalsIgnoreCase(phoneNumber)) {
                    Log.d("test", "find : " + number);
                    textView.setText("find phone number: " + phoneNumber);
                    return true;
                }
            }
            Log.d("test", "No default number!");
            textView.setText("Not find phonenumber! : " +phoneNumber);
            return false;
        }
    }

    // IMSI
    private static String[] known_imsi_ids = {"310260000000000" // default IMSI
    };
    public static boolean hasKnownImsi(TextView textView, AppCompatActivity act, Context context) {
        if (PermissionUtils.isLacksOfPermission(context, PermissionUtils.PERMISSION[0])) {
            ActivityCompat.requestPermissions(act, PermissionUtils.PERMISSION, 0x12);
            Log.d("test", "No permission");
            return false;
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imsi = telephonyManager.getSubscriberId();
            for (String known_imsi : known_imsi_ids) {
                if (known_imsi.equalsIgnoreCase(imsi)) {
                    Log.d("find:",known_imsi);
                    textView.setText("find IMSI: " + imsi);
                    return true;
                }
            }
            Log.d("test","No default IMSI!");
            textView.setText("Not find IMSI : " + imsi);
            return false;
        }
    }

    //Build
    public static boolean hasEmulatorBuild(TextView textView, Context context) {
        String BOARD = android.os.Build.BOARD; // The name of the underlying board, like "unknown".
        // This appears to occur often on real hardware... that's sad
        // String BOOTLOADER = android.os.Build.BOOTLOADER; // The system bootloader version number.
        String BRAND = android.os.Build.BRAND; // The brand (e.g., carrier) the software is customized for, if any.
        // "generic"
        String DEVICE = android.os.Build.DEVICE; // The name of the industrial design. "generic"
        String HARDWARE = android.os.Build.HARDWARE; // The name of the hardware (from the kernel command line or
        // /proc). "goldfish"
        String MODEL = android.os.Build.MODEL; // The end-user-visible name for the end product. "sdk"
        String PRODUCT = android.os.Build.PRODUCT; // The name of the overall product.
        if ((BOARD.compareTo("unknown") == 0) /* || (BOOTLOADER.compareTo("unknown") == 0) */
                || (BRAND.compareTo("generic") == 0) || (DEVICE.compareTo("generic") == 0)
                || (MODEL.compareTo("sdk") == 0) || (PRODUCT.compareTo("sdk") == 0)
                || (HARDWARE.compareTo("goldfish") == 0)) {
            Log.d("find:","Build");
            textView.setText("BUILD TESTING:" + "\n" + "BOARD:" + BOARD + "\n" + "BRAND:" + BRAND + "\n" +
                    "DEVICE:" + DEVICE + "\n" + "MODEL:" + MODEL + "\n"
                    + "PRODUCT:" + PRODUCT + "\n" + "HARDWARE:" + HARDWARE + "\n");
            return true;
        }
        Log.d("test:","No build");
        textView.setText("No emulator build!" + "BUILD TESTING:" + "\n" + "BOARD:" + BOARD + "\n" + "BRAND:" + BRAND + "\n" +
                "DEVICE:" + DEVICE + "\n" + "MODEL:" + MODEL + "\n"
                + "PRODUCT:" + PRODUCT + "\n" + "HARDWARE:" + HARDWARE + "\n");
        return false;
    }

    //Operator
    public static boolean isOperatorNameAndroid(TextView textView, Context paramContext) {
        String szOperatorName = ((TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperatorName();
        boolean isAndroid = szOperatorName.equalsIgnoreCase("android");
        textView.setText("Operator: " + szOperatorName);
        return isAndroid;
    }

    //qemu drivers
    private static String[] known_qemu_drivers = {"goldfish"};
    public static boolean hasQEmuDrivers(TextView textView) {
        for (File drivers_file : new File[]{new File("/proc/tty/drivers"), new File("/proc/cpuinfo")}) {
            if (drivers_file.exists() && drivers_file.canRead()) {
                // We don't care to read much past things since info we care about should be inside here
                byte[] data = new byte[1024];
                try {
                    InputStream is = new FileInputStream(drivers_file);
                    is.read(data);
                    is.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                String driver_data = new String(data);
                Log.d("test",driver_data);
                for (String known_qemu_driver : Utils.known_qemu_drivers) {
                    if (driver_data.indexOf(known_qemu_driver) != -1) {
                        textView.setText("qemu drivers exits!");
                        return true;
                    }
                }
            }
        }
        textView.setText("qemu drivers not exits!");
        return false;
    }

    //qemu files
    private static String[] known_files = {"/system/lib/libc_malloc_debug_qemu.so", "/sys/qemu_trace",
            "/system/bin/qemu-props"};
    public static boolean hasQEmuFiles(TextView textView) {
        for (String pipe : known_files) {
            File qemu_file = new File(pipe);
            if (qemu_file.exists()) {
                textView.setText("find qemu file: " + pipe);
                return true;
            }
        }
        return false;
    }

    //genymotion file
    private static String[] known_geny_files = {"/dev/socket/genyd", "/dev/socket/baseband_genyd"};
    public static boolean hasGenyFiles(TextView textView) {
        for (String file : known_geny_files) {
            File geny_file = new File(file);
            if (geny_file.exists()) {
                textView.setText("genymotion file: " + file);
                return true;
            }
        }
        textView.setText("No genymotion file!");
        return false;
    }


    //Monkey
    public static void isUserAMonkey(TextView textView) {
        if(ActivityManager.isUserAMonkey()){
            textView.setText("A monkey!");
        }else{
            textView.setText("Not qemu");
        }
    }

    //debugger
    public static void isBeingDebugged(TextView textView) {
        if(Debug.isDebuggerConnected()){
            textView.setText("Detect debugger!");
        }else{
            textView.setText("No debug!");
        }
    }

    //ptrace
    private static String tracerpid = "TracerPid";
    /**
     * @return
     * @throws IOException
     */
    public static boolean hasTracerPid(TextView textView)throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/self/status")), 1000);
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.length() > tracerpid.length()) {
                    if (line.substring(0, tracerpid.length()).equalsIgnoreCase(tracerpid)) {
                        if (Integer.decode(line.substring(tracerpid.length() + 1).trim()) > 0) {
                            textView.setText("Tracerpid:"+line.substring(tracerpid.length() + 1).trim());
                            return true;
                        }
                        break;
                    }
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            reader.close();
        }
        textView.setText("No detect Tracerpid");
        return false;
    }


    //TCP connection
    /*
    public static boolean hasAdbInEmulator(TextView textView) throws IOException {
        boolean adbInEmulator = false;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/net/tcp")), 1000);
            String line;
            // Skip column names
            reader.readLine();

            ArrayList<tcp> tcpList = new ArrayList<tcp>();

            while ((line = reader.readLine()) != null) {
                tcpList.add(tcp.create(line.split("\\W+")));
            }

            reader.close();

            // Adb is always bounce to 0.0.0.0 - though the port can change
            // real devices should be != 127.0.0.1
            int adbPort = -1;
            for (tcp tcpItem : tcpList) {
                if (tcpItem.localIp == 0) {
                    adbPort = tcpItem.localPort;
                    break;
                }
            }

            if (adbPort != -1) {
                for (tcp tcpItem : tcpList) {
                    if ((tcpItem.localIp != 0) && (tcpItem.localPort == adbPort)) {
                        adbInEmulator = true;
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            reader.close();
        }

        if(adbInEmulator){
            textView.setText("adb in emulator!");
            return true;
        }else{
            textView.setText("Not adb in emulator!");
            return false;
        }
    }

    public static class tcp {

        public int id;
        public long localIp;
        public int localPort;
        public int remoteIp;
        public int remotePort;

        static tcp create(String[] params) {
            return new tcp(params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8],
                    params[9], params[10], params[11], params[12], params[13], params[14]);
        }

        public tcp(String id, String localIp, String localPort, String remoteIp, String remotePort, String state,
                   String tx_queue, String rx_queue, String tr, String tm_when, String retrnsmt, String uid,
                   String timeout, String inode) {
            this.id = Integer.parseInt(id, 16);
            this.localIp = Long.parseLong(localIp, 16);
            this.localPort = Integer.parseInt(localPort, 16);
        }
    }
    */
    //eth0
    public static boolean hasEth0Interface(TextView textView) {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                Log.d("test","Interface: "+intf.getName());
                if (intf.getName().equals("eth0")) {
                    textView.setText("Find eth0");
                    return true;
                }
            }
        } catch (SocketException ex) {
            Log.d("test","error!");
        }
        textView.setText("No eth0");
        return false;
    }


    /*TaintDroid*/

    public static boolean hasPackageNameInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        // In theory, if the package installer does not throw an exception, package exists
        try {
            packageManager.getInstallerPackageName(packageName);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
    public static void hasAppAnalysisPackage(Context context,TextView textView) {
        if(Utils.hasPackageNameInstalled(context, "org.appanalysis")){
            textView.setText("Find taint");
        }else{
            textView.setText("No taint");
        }
    }
    public static boolean hasTaintClass(TextView textView) {
        try {
            Class.forName("dalvik.system.Taint");
            textView.setText("Find taint");
            return true;
        }
        catch (ClassNotFoundException exception) {
            textView.setText("No taint");
            return false;
        }
    }


    //sensor
    private static float oldsensor;
    private static float sensor_count;
    public static boolean detectsensor(final TextView textView, final Context context){
        try {
            final SensorManager smanger = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            final Sensor sensor = smanger.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            SensorEventListener sensorEvent = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    sensor_count++;
                    if((sensor_count == 2) && (event.values[0] == oldsensor)  ){
                        textView.setText("The sensor has constant value: "+ Float.toString(event.values[0]));

                    }
                    oldsensor = event.values[0];
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };
            smanger.registerListener(sensorEvent, sensor, SensorManager.SENSOR_DELAY_UI);
            return false;
        }catch (Exception e){
            textView.setText("Can't detect sensor!");
            return true;
        }
    }

}