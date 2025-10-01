package com.shiyunjin.easycontrolnext.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.documentfile.provider.DocumentFile;

import java.io.IOException;
import java.io.InputStream;

import com.shiyunjin.easycontrolnext.app.client.Client;
import com.shiyunjin.easycontrolnext.app.client.tools.AdbTools;
import com.shiyunjin.easycontrolnext.app.databinding.ActivityMainBinding;
import com.shiyunjin.easycontrolnext.app.entity.AppData;
import com.shiyunjin.easycontrolnext.app.entity.Device;
import com.shiyunjin.easycontrolnext.app.helper.DeviceListAdapter;
import com.shiyunjin.easycontrolnext.app.helper.MyBroadcastReceiver;
import com.shiyunjin.easycontrolnext.app.helper.ViewTools;

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
        DocumentFile documentFile = DocumentFile.fromSingleUri(this, uri);
        String fileName = "easycontrolnext_push_file";
        if (documentFile != null && documentFile.getName() != null) {
          fileName = documentFile.getName();
        }
        InputStream inputStream = getContentResolver().openInputStream(uri);
        deviceListAdapter.pushFile(inputStream, fileName);
      } catch (IOException ignored) {
        deviceListAdapter.pushFile(null, null);
        ;
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

}