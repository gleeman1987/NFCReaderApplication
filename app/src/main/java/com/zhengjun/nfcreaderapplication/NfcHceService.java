package com.zhengjun.nfcreaderapplication;

import android.nfc.cardemulation.HostApduService;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class NfcHceService extends HostApduService {
    private static final String TAG = "NfcHceService";

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        Utils.showLog(TAG, "commandApdu = [" + commandApdu + "], extras = [" + extras + "]");
        byte[] bytes = new byte[0];
        return null;
    }

    @Override
    public void onDeactivated(int reason) {
        Utils.showLog(TAG, "reason = [" + reason + "]");
        switch (reason) {
            case HostApduService.DEACTIVATION_DESELECTED:
                //NFC读取器发送另一个“SELECT AID”APDU，系统将其解析为其他服务
                break;
            case HostApduService.DEACTIVATION_LINK_LOSS:
                //NFC链接断开

                break;
        }
    }
}