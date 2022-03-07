package im.zego.gomovie.client.sdk.callbacks;

import im.zego.zegoexpress.entity.ZegoBroadcastMessageInfo;

public interface IZegoIMSendMessageCallback {

    void onIMSendMessageResult(int errorCode, ZegoBroadcastMessageInfo messageInfo);

}
