package com.daitj.easycontrolfork.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Pair;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import com.daitj.easycontrolfork.app.adb.AdbKeyPair;
import com.daitj.easycontrolfork.app.databinding.ActivityAdbKeyBinding;
import com.daitj.easycontrolfork.app.entity.AppData;
import com.daitj.easycontrolfork.app.helper.PublicTools;
import com.daitj.easycontrolfork.app.helper.ViewTools;

public class AdbKeyActivity extends Activity {
  private ActivityAdbKeyBinding activityAdbKeyBinding;
  private Pair<File, File> adbKeyFile;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ViewTools.setStatusAndNavBar(this);
    ViewTools.setLocale(this);
    activityAdbKeyBinding = ActivityAdbKeyBinding.inflate(this.getLayoutInflater());
    setContentView(activityAdbKeyBinding.getRoot());
    adbKeyFile = PublicTools.getAdbKeyFile(this);
    readKey();
    activityAdbKeyBinding.backButton.setOnClickListener(v -> finish());
    activityAdbKeyBinding.ok.setOnClickListener(v -> writeKey());
  }

  // 读取旧的密钥公钥文件
  private void readKey() {
    try {
      byte[] publicKeyBytes = new byte[(int) adbKeyFile.first.length()];
      byte[] privateKeyBytes = new byte[(int) adbKeyFile.second.length()];

      try (FileInputStream stream = new FileInputStream(adbKeyFile.first)) {
        stream.read(publicKeyBytes);
        activityAdbKeyBinding.adbKeyPub.setText(new String(publicKeyBytes));
      }
      try (FileInputStream stream = new FileInputStream(adbKeyFile.second)) {
        stream.read(privateKeyBytes);
        activityAdbKeyBinding.adbKeyPri.setText(new String(privateKeyBytes));
      }
    } catch (IOException ignored) {
    }
  }

  // 写入新的密钥公钥文件
  private void writeKey() {
    try {
      try (FileWriter publicKeyWriter = new FileWriter(adbKeyFile.first)) {
        publicKeyWriter.write(String.valueOf(activityAdbKeyBinding.adbKeyPub.getText()));
        publicKeyWriter.flush();
      }
      try (FileWriter privateKeyWriter = new FileWriter(adbKeyFile.second)) {
        privateKeyWriter.write(String.valueOf(activityAdbKeyBinding.adbKeyPri.getText()));
        privateKeyWriter.flush();
      }
      AppData.keyPair = AdbKeyPair.read(adbKeyFile.first, adbKeyFile.second);
      Toast.makeText(this, getString(R.string.toast_success), Toast.LENGTH_SHORT).show();
    } catch (Exception ignored) {
    }
  }
}