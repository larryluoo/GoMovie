package im.zego.gomovie.client.sdk.callbacks;

import im.zego.gomovie.client.sdk.model.ZegoRoomUser;

public interface IRoomUserListener {
    void onUserAdd(ZegoRoomUser zegoUser);
    void onUserRemove(ZegoRoomUser zegoUser);
}
