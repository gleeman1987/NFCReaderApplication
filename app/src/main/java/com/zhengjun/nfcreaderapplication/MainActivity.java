package com.zhengjun.nfcreaderapplication;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tv_hce_available;
    private NfcAdapter nfcAdapter;
    private static final String TAG = "MainActivity";
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity.this);
        tv_hce_available = (TextView) findViewById(R.id.tv_hce_available);
        PackageManager pm = MainActivity.this.getPackageManager();
        boolean hasNfcHce = pm.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION);
        String text = hasNfcHce ? "恭喜,您的设备支持HCE功能" : "抱歉,您的设备不支持HCE功能";
        byte[] bytes = "您的设备支持HCE功能".toString().getBytes();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(bytes[i]);
        }
        tv_hce_available.setText(text+"\n"+sb.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(MainActivity.this, MainActivity.this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
        nfcAdapter.enableForegroundDispatch(MainActivity.this, pendingIntent,new IntentFilter[]{},new String[][]{{IsoDep.class.getName()},{NfcA.class.getName()}});
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            nfcAdapter.disableReaderMode(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Utils.showLog(TAG, "");
    }
}
