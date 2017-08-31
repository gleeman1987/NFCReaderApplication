package com.zhengjun.nfcreaderapplication;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.IOException;

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

    private String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long getReversed(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    //一般公家卡，扫描的信息
    private String dumpTagData(Parcelable p) {
        StringBuilder sb = new StringBuilder();
        Tag tag = (Tag) p;
        byte[] id = tag.getId();
        sb.append("Tag ID (hex): ").append(getHex(id)).append("\n");
        sb.append("Tag ID (dec): ").append(getDec(id)).append("\n");
        sb.append("ID (reversed): ").append(getReversed(id)).append("\n");

        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                MifareClassic mifareTag = MifareClassic.get(tag);
                String type = "Unknown";
                switch (mifareTag.getType()) {
                    case MifareClassic.TYPE_CLASSIC:
                        type = "Classic";
                        break;
                    case MifareClassic.TYPE_PLUS:
                        type = "Plus";
                        break;
                    case MifareClassic.TYPE_PRO:
                        type = "Pro";
                        break;
                }
                sb.append("Mifare Classic type: ");
                sb.append(type);
                sb.append('\n');

                sb.append("Mifare size: ");
                sb.append(mifareTag.getSize() + " bytes");
                sb.append('\n');

                sb.append("Mifare sectors: ");
                sb.append(mifareTag.getSectorCount());
                sb.append('\n');

                sb.append("Mifare blocks: ");
                sb.append(mifareTag.getBlockCount());
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }

        return sb.toString();
    }

    private void processNfcIntent(Intent intent) {
        Parcelable[] rawMsgs = intent
                .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage[] msgs;
        if (rawMsgs != null) {
            msgs = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++) {
                msgs[i] = (NdefMessage) rawMsgs[i];
            }
        } else {
            // Unknown tag type
            byte[] empty = new byte[0];
            byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            Parcelable tag = intent
                    .getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String dumpTagData = dumpTagData(tag);
            Utils.showLog(TAG, dumpTagData);
            byte[] payload = dumpTagData.getBytes();
            NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
                    empty, id, payload);
            NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
            msgs = new NdefMessage[] { msg };
        }

        Utils.showLog(TAG, "++++++++++++"+msgs+"++++++++++++++");
        for (NdefMessage msg : msgs) {
            Utils.showLog(TAG, msg.toString());
        }
        Utils.showLog(TAG, "++++++++++++"+msgs+"++++++++++++++");

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
                    Utils.showLog(TAG, "----------------IsoDep.get(tag) == "+isoDep);
                    byte[] hiLayerResponse = isoDep.getHiLayerResponse();
                    Utils.showLog(TAG, "hiLayerResponse = "+hiLayerResponse);
                    byte[] historicalBytes = isoDep.getHistoricalBytes();
                    Utils.showLog(TAG, "historicalBytes = "+historicalBytes);
                    for (byte historicalByte : historicalBytes) {
                        System.out.print(historicalByte);
                    }
                    System.out.println();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        Utils.showLog(TAG, "isExtendedLengthApduSupported = "+isoDep.isExtendedLengthApduSupported());
                    }
                    Utils.showLog(TAG, "getMaxTransceiveLength = "+isoDep.getMaxTransceiveLength());
                    Utils.showLog(TAG, "getTimeout = "+isoDep.getTimeout());
                }
                NfcA nfcA = NfcA.get(tag);
                if (nfcA != null) {
                    Utils.showLog(TAG, "----------------NfcA.get(tag) == "+nfcA);
                    Utils.showLog(TAG, "NfcA sak = "+nfcA.getSak());
                    byte[] atqa = nfcA.getAtqa();
                    Utils.showLog(TAG, "nfcA.getAtqa = "+atqa);
                    for (byte b : atqa) {
                        System.out.print(b);
                    }
                    System.out.println();
                    int maxTransceiveLength = nfcA.getMaxTransceiveLength();
                    Utils.showLog(TAG, "maxTransceiveLength = "+maxTransceiveLength);
                    int timeout = nfcA.getTimeout();
                    Utils.showLog(TAG, "NfcA timeout = "+timeout);
                }
                try {
                    MifareClassic mifareClassic = MifareClassic.get(tag);
                    if (mifareClassic != null) {
                        Utils.showLog(TAG, "----------------MifareClassic.get(tag) == "+mifareClassic);
                        try {
                            mifareClassic.connect();
                        } catch (IOException e) {

                        }
                        Utils.showLog(TAG, "getMaxTransceiveLength = "+mifareClassic.getMaxTransceiveLength());
                        for (int i = 0; i < mifareClassic.getBlockCount(); i++) {
                            try {
                                byte[] bytes = mifareClassic.readBlock(i);
                                for (int i1 = 0; i1 < bytes.length; i1++) {
                                    System.out.print(bytes[i]+" ");
                                }
                                System.out.println();
                            } catch (IOException e) {

                            }
                        }
                        int type = mifareClassic.getType();
                        Utils.showLog(TAG, "mifareClassic.getType = "+type);
                    }
                } catch (RuntimeException e) {
                    Utils.showLog(TAG, "MifareClassic.get(tag) error");
                }
                NdefFormatable ndefFormatable = NdefFormatable.get(tag);
                if (ndefFormatable != null) {
                    Utils.showLog(TAG, "----------------NdefFormatable.get(tag) == "+ndefFormatable);
                }

                break;
            case NfcAdapter.ACTION_TAG_DISCOVERED:

                break;
        }
    }
}
