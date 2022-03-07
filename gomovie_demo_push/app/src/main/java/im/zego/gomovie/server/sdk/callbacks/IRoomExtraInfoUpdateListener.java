package im.zego.gomovie.server.sdk.callbacks;

import im.zego.zegoexpress.entity.ZegoRoomExtraInfo;

public interface IRoomExtraInfoUpdateListener {
    void onRoomExtraInfoUpdate(String roomID, ZegoRoomExtraInfo roomExtraInfo);
}
