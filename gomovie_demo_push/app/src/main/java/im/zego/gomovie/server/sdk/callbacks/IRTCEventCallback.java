package im.zego.gomovie.server.sdk.callbacks;

import im.zego.zegoexpress.entity.ZegoRoomExtraInfo;
import im.zego.zegoexpress.entity.ZegoUser;


public interface IRTCEventCallback {

    /**
     * 房间关闭通知
     */
    void onRoomCloseNotify();

    void onRoomExtraInfoUpdate(String roomID, ZegoRoomExtraInfo roomExtraInfo);

    void onUserAdd(ZegoUser zegoUser);

    void onUserRemove(ZegoUser zegoUser);


}
