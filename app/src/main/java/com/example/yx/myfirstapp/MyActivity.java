package com.example.yx.myfirstapp;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class MyActivity extends AppCompatActivity {
    //包名
    public final static String PkgName = "com.example.yx.myapplication";
    //包ａｃｔｉｖｉｔｙ名
    public final static String ActName = "com.example.yx.myapplication.MainActivity";

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    // 移除所有的msg.what为0等消息，保证只有一个循环消息队列再跑
                    handler.removeMessages(0);
//                     app的功能逻辑处理
                    if(isProessRunning(getBaseContext(),PkgName)){
                        killProcess();
                    }else{
                        openProcess();
                    }
                    // 再次发出msg，循环更新
                    handler.sendEmptyMessageDelayed(0, 5000);
                    break;

                case 1:
                    // 直接移除，定时器停止
                    handler.removeMessages(0);
                    break;

                default:
                    break;
            }
        };
    };

    /**
     * 进程运行状态
     * @param context
     * @param proessName    进程名
     * @return
     */
    public static boolean isProessRunning(Context context, String proessName) {

        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int apiver = Build.VERSION.SDK_INT;
        if (apiver > Build.VERSION_CODES.KITKAT){
            List<ProcessManager.Process> runningProcesses=new ArrayList<>();
            PackageManager pm = context.getPackageManager();
            runningProcesses = ProcessManager.getRunningProcesses();
            for (ProcessManager.Process runningProcesse : runningProcesses) {
                String packname = runningProcesse.getPackageName();
//                Log.e("进程---", packname);
                if(packname.equals(proessName)){
                    //Log.i("Service2进程", ""+info.processName);
                    isRunning = true;
                }
            }
        }else{
            List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
            for(ActivityManager.RunningAppProcessInfo info : lists){
                if(info.processName.equals(proessName)){
                    //Log.i("Service2进程", ""+info.processName);
                    isRunning = true;
                }
            }
        }

        return isRunning;
    }
//OPPO R9s Plus
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        String TAG = "系统参数";
        Log.e(TAG, "手机厂商：" + SystemUtil.getDeviceBrand());
        Log.e(TAG, "手机型号：" + SystemUtil.getSystemModel());
        Log.e(TAG, "手机当前系统语言：" + SystemUtil.getSystemLanguage());
        Log.e(TAG, "Android系统版本号：" + SystemUtil.getSystemVersion());
        Log.e(TAG, "手机IMEI：" + SystemUtil.getIMEI(getApplicationContext()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 开启定时器
     * @param view
     */
    public void startThread(View view){
        handler.sendEmptyMessage(0);
    }

    /**
     * 关闭定时器
     * @param view
     */
    public void stopThread(View view){
        handler.sendEmptyMessage(1);
    }

    /**
     * 开启进程
     */
    public void openProcess(){
        Intent intent = new Intent();
        try{
            ComponentName name = new ComponentName(PkgName,ActName);
            intent.setComponent(name);
            startActivity(intent);
            Log.e("启动进程：",PkgName);
        }catch (Exception e) {
            Toast.makeText(this, "请先安装该app", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 杀死进程
     */
    public  void killProcess(){
        ActivityManager am = (ActivityManager) getBaseContext()
                .getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        List<ProcessManager.Process> runningProcesses=new ArrayList<>();
        PackageManager pm = getBaseContext().getPackageManager();
        runningProcesses = ProcessManager.getRunningProcesses();

        for (ProcessManager.Process runningProcesse : runningProcesses) {
            String packname = runningProcesse.getPackageName();
            Log.e("进程---", packname);
            if(packname.equals(PkgName)){
                //Log.i("Service2进程", ""+info.processName);
                android.os.Process.killProcess(runningProcesse.pid);
                Log.e("杀死进程：",PkgName);
            }
        }
    }
}
