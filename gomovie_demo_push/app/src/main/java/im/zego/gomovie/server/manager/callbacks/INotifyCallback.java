package im.zego.gomovie.server.manager.callbacks;

import im.zego.zegoexpress.entity.ZegoRoomExtraInfo;
import im.zego.zegoexpress.entity.ZegoUser;

public interface INotifyCallback {

    void onRoomExtraInfoUpdate(String roomID, ZegoRoomExtraInfo roomExtraInfo);

    void onRoomCloseNotify();

    void onUserAdd(ZegoUser zegoUser);

    void onUserRemove(ZegoUser zegoUser);
}
