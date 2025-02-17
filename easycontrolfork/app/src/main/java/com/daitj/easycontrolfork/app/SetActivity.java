package com.daitj.easycontrolfork.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.daitj.easycontrolfork.app.databinding.ActivitySetBinding;
import com.daitj.easycontrolfork.app.entity.AppData;
import com.daitj.easycontrolfork.app.helper.PublicTools;
import com.daitj.easycontrolfork.app.helper.ViewTools;

public class SetActivity extends Activity {
  private ActivitySetBinding activitySetBinding;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ViewTools.setStatusAndNavBar(this);
    ViewTools.setLocale(this);
    activitySetBinding = ActivitySetBinding.inflate(this.getLayoutInflater());
    setContentView(activitySetBinding.getRoot());
    // 设置页面
    drawUi();
    setButtonListener();
  }

  // 设置默认值
  private void drawUi() {
    // 其他
    activitySetBinding.setOther.addView(ViewTools.createTextCard(this, getString(R.string.set_other_ip), () -> startActivity(new Intent(this, IpActivity.class))).getRoot());
    activitySetBinding.setOther.addView(ViewTools.createTextCard(this, getString(R.string.set_other_custom_key), () -> startActivity(new Intent(this, AdbKeyActivity.class))).getRoot());
    activitySetBinding.setOther.addView(ViewTools.createTextCard(this, getString(R.string.set_other_reset_key), () -> {
      AppData.keyPair = PublicTools.reGenerateAdbKeyPair();
      Toast.makeText(this, getString(R.string.toast_success), Toast.LENGTH_SHORT).show();
    }).getRoot());
    activitySetBinding.setOther.addView(ViewTools.createTextCard(this, getString(R.string.set_other_locale), () -> {
      AppData.setting.setLocale(AppData.setting.getLocale().equals("en") ? "zh" : "en");
      Toast.makeText(this, getString(R.string.toast_change_locale), Toast.LENGTH_SHORT).show();
    }).getRoot());
   }

  // 设置按钮监听
  private void setButtonListener() {
    activitySetBinding.backButton.setOnClickListener(v -> finish());
  }
}
