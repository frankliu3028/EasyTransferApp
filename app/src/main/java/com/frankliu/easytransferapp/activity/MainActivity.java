package com.frankliu.easytransferapp.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.frankliu.easytransferapp.fragment.DeviceFragment;
import com.frankliu.easytransferapp.fragment.FileFragment;
import com.frankliu.easytransferapp.fragment.TaskFragment;
import com.frankliu.easytransferapp.service.TaskService;
import com.frankliu.easytransferapp.utils.Config;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.frankliu.easytransferapp.R;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private final String TAG = MainActivity.class.getSimpleName();

    private final int EXTERNAL_STORAGE_REQUEST_CODE = 203;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_scanner:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_task:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_file:
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            navigation.getMenu().getItem(position).setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private TaskService.TaskBinder taskBinder;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            taskBinder = (TaskService.TaskBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbar.setTitle("EasyTransfer");
        setSupportActionBar(toolbar);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        viewPager.addOnPageChangeListener(onPageChangeListener);

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;
                switch (position){
                    case 0:
                        fragment = new DeviceFragment();
                        break;
                    case 1:
                        fragment = new TaskFragment();
                        break;
                    case 2:
                        fragment = new FileFragment();
                        break;
                        default:
                            fragment = new Fragment();
                            break;
                }
                return fragment;
            }

            @Override
            public int getCount() {
                return 3;
            }
        };

        viewPager.setAdapter(adapter);
        Config.fileSaveDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/EasyTransfer";
        requestExternalStorage();
        createEasyTransferDir();

        Intent intent = new Intent(this, TaskService.class);
        startService(intent);
        //bindService(intent,serviceConnection, BIND_AUTO_CREATE);
        //Log.w(TAG, "bind");
    }

    private void createEasyTransferDir(){
        File file = new File(Config.fileSaveDir);
        if(!file.exists()){
            if(!file.mkdir()){
                Log.e(TAG, "create dirs error!");
                Toast.makeText(this, "create dirs error", Toast.LENGTH_SHORT).show();
            }
        }
        Log.w(TAG, "fileSaveDir:" + Config.fileSaveDir);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unbindService(serviceConnection);
    }

    private void requestExternalStorage(){
        if(EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){

        }else{
            String[] perms = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            EasyPermissions.requestPermissions(this, "需要获取外部存储权限", EXTERNAL_STORAGE_REQUEST_CODE, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        //Toast.makeText(this, "granted" + requestCode, Toast.LENGTH_SHORT).show();
        if(requestCode == EXTERNAL_STORAGE_REQUEST_CODE){
            createEasyTransferDir();
        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        //Toast.makeText(this, "denied" + requestCode, Toast.LENGTH_SHORT).show();
        if(requestCode == EXTERNAL_STORAGE_REQUEST_CODE){
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("警告")
                    .setMessage("外部存储权限未取得，即将退出应用")
                    .setPositiveButton("再试一次", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestExternalStorage();
                        }
                    })
                    .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
    }
}
