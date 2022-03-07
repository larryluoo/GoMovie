package im.zego.gomovie.client.sdk.callbacks;

import im.zego.zegoexpress.entity.ZegoRoomExtraInfo;

public interface IRoomExtraInfoUpdateListener {
    void onRoomExtraInfoUpdate(String roomID, ZegoRoomExtraInfo roomExtraInfo);
}
