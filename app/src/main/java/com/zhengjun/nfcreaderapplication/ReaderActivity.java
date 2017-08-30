package com.zhengjun.nfcreaderapplication;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ReaderActivity extends AppCompatActivity {
    private static final String TAG = "ReaderActivity";
    private TextView text_nfc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        text_nfc = (TextView) findViewById(R.id.text_nfc);

        Intent intent = getIntent();
        processNfcIntent(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        processNfcIntent(intent);
    }

    private void processNfcIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Utils.showLog(TAG, "Extra tag = "+tag);
        text_nfc.setText(tag.toString());
        String[] techList = tag.getTechList();
        for (String s : techList) {
            Utils.showLog(TAG, "tag tech : "+s);
        }
        String action = intent.getAction();
        Utils.showLog(TAG, "intent action = "+action);
        switch (action) {
            case NfcAdapter.ACTION_NDEF_DISCOVERED:
                Parcelable[] rawMessages =
                        intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if (rawMessages != null) {
                    NdefMessage[] messages = new NdefMessage[rawMessages.length];
                    for (int i = 0; i < rawMessages.length; i++) {
                        messages[i] = (NdefMessage) rawMessages[i];
                    }
                    if (messages != null && messages.length != 0) {
                        for (NdefMessage message : messages) {
                            Utils.showLog(TAG, "message item: "+message.toString());
                        }
                    }
                }
                break;
            case NfcAdapter.ACTION_TECH_DISCOVERED:
                IsoDep isoDep = IsoDep.get(tag);
                if (isoDep != null) {
                    Utils.showLog(TAG, "IsoDep.get(tag) == "+isoDep);
                }
                NfcA nfcA = NfcA.get(tag);
                if (nfcA != null) {
                    Utils.showLog(TAG, "NfcA.get(tag) == "+nfcA);
                }
                try {
                    MifareClassic mifareClassic = MifareClassic.get(tag);
                    if (mifareClassic != null) {
                        Utils.showLog(TAG, "MifareClassic.get(tag) == "+mifareClassic);
                    }
                } catch (RuntimeException e) {
                    Utils.showLog(TAG, "MifareClassic.get(tag) error");
                }
                NdefFormatable ndefFormatable = NdefFormatable.get(tag);
                if (ndefFormatable != null) {
                    Utils.showLog(TAG, "NdefFormatable.get(tag) == "+ndefFormatable);
                }

                break;
            case NfcAdapter.ACTION_TAG_DISCOVERED:

                break;
        }
    }
}
