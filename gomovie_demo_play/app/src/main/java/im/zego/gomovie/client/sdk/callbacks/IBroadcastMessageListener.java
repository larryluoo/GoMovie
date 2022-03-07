package im.zego.gomovie.client.sdk.callbacks;

import java.util.ArrayList;

import im.zego.zegoexpress.entity.ZegoBroadcastMessageInfo;

public interface IBroadcastMessageListener {
    void onRecvBroadcastMessage(ArrayList<ZegoBroadcastMessageInfo> messageList, String roomID);
}
