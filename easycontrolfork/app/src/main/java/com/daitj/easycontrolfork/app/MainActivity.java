package com.daitj.easycontrolfork.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;

import java.io.IOException;
import java.io.InputStream;

import com.daitj.easycontrolfork.app.client.Client;
import com.daitj.easycontrolfork.app.client.tools.AdbTools;
import com.daitj.easycontrolfork.app.databinding.ActivityMainBinding;
import com.daitj.easycontrolfork.app.entity.AppData;
import com.daitj.easycontrolfork.app.entity.Device;
import com.daitj.easycontrolfork.app.helper.DeviceListAdapter;
import com.daitj.easycontrolfork.app.helper.MyBroadcastReceiver;
import com.daitj.easycontrolfork.app.helper.ViewTools;

public class MainActivity extends Activity {

  private ActivityMainBinding activityMainBinding;
  public DeviceListAdapter deviceListAdapter;

  // 广播
  private final MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();

  @SuppressLint("SourceLockedOrientationActivity")
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AppData.init(this);
    ViewTools.setStatusAndNavBar(this);
    ViewTools.setLocale(this);
    activityMainBinding = ActivityMainBinding.inflate(this.getLayoutInflater());
    setContentView(activityMainBinding.getRoot());
    // 设置设备列表适配器
    deviceListAdapter = new DeviceListAdapter(this);
    activityMainBinding.devicesList.setAdapter(deviceListAdapter);
    myBroadcastReceiver.setDeviceListAdapter(deviceListAdapter);
    // 设置按钮监听
    setButtonListener();
    // 注册广播监听
    myBroadcastReceiver.register(this);
    // 重置已连接设备
    myBroadcastReceiver.resetUSB();
    // 自启动设备
    AppData.uiHandler.postDelayed(() -> {
      for (Device device : AdbTools.devicesList) if (device.connectOnStart) Client.startDevice(device);
    }, 2000);
  }

  @Override
  protected void onDestroy() {
    myBroadcastReceiver.unRegister(this);
    super.onDestroy();
  }

  // 设置按钮监听
  private void setButtonListener() {
    activityMainBinding.buttonAdd.setOnClickListener(v -> startActivity(new Intent(this, DeviceDetailActivity.class)));
    activityMainBinding.buttonSet.setOnClickListener(v -> startActivity(new Intent(this, SetActivity.class)));
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK && requestCode == 1) {
      Uri uri = data.getData();
      if (uri == null) deviceListAdapter.pushFile(null, null);
      ;
      try {
        String fileName = "easycontrolfork_push_file";
        ContentResolver contentProvider = getContentResolver();
        InputStream inputStream = contentProvider.openInputStream(uri);
        //根据Uri查询文件名
        try (Cursor cursor = contentProvider.query(uri, null, null, null, null)) {
          if (cursor != null) {
            cursor.moveToFirst();
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            fileName = cursor.getString(nameIndex);
          }
        }
        deviceListAdapter.pushFile(inputStream, fileName);
      } catch (IOException ignored) {
        deviceListAdapter.pushFile(null, null);
        ;
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

}