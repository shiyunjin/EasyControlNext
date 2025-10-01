package com.shiyunjin.easycontrolnext.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.util.Pair;
import android.widget.Toast;

import java.util.ArrayList;

import com.shiyunjin.easycontrolnext.app.R;
import com.shiyunjin.easycontrolnext.app.databinding.ActivityIpBinding;
import com.shiyunjin.easycontrolnext.app.databinding.ItemTextBinding;
import com.shiyunjin.easycontrolnext.app.entity.AppData;
import com.shiyunjin.easycontrolnext.app.helper.PublicTools;
import com.shiyunjin.easycontrolnext.app.helper.ViewTools;

public class IpActivity extends Activity {
  private ActivityIpBinding activityIpBinding;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ViewTools.setStatusAndNavBar(this);
    ViewTools.setLocale(this);
    activityIpBinding = ActivityIpBinding.inflate(this.getLayoutInflater());
    setContentView(activityIpBinding.getRoot());
    setButtonListener();
    // 绘制UI
    drawUi();
  }

  private void drawUi() {
    // 添加IP
    Pair<ArrayList<String>, ArrayList<String>> listPair = PublicTools.getLocalIp();
    for (String i : listPair.first) {
      ItemTextBinding text = ViewTools.createTextCard(this, i, () -> {
        AppData.clipBoard.setPrimaryClip(ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_PLAIN, i));
        Toast.makeText(this, getString(R.string.toast_copy), Toast.LENGTH_SHORT).show();
      });
      activityIpBinding.ipv4.addView(text.getRoot());
    }
    for (String i : listPair.second) {
      ItemTextBinding text = ViewTools.createTextCard(this, i, () -> {
        AppData.clipBoard.setPrimaryClip(ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_PLAIN, i));
        Toast.makeText(this, getString(R.string.toast_copy), Toast.LENGTH_SHORT).show();
      });
      activityIpBinding.ipv6.addView(text.getRoot());
    }
  }

  // 设置返回按钮监听
  private void setButtonListener() {
    activityIpBinding.backButton.setOnClickListener(v -> finish());
  }

}
