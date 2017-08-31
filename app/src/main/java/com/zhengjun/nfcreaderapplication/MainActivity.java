package com.zhengjun.nfcreaderapplication;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tv_hce_available;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_hce_available = (TextView) findViewById(R.id.tv_hce_available);
        PackageManager pm = MainActivity.this.getPackageManager();
        boolean hasNfcHce = pm.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION);
        tv_hce_available.setText(hasNfcHce?"恭喜,您的设备支持HCE功能":"抱歉,您的设备不支持HCE功能");
    }
}
