package im.zego.gomovie.client.sdk.callbacks;

import im.zego.gomovie.client.sdk.model.ZegoRoomUser;

public interface IUserCountListener {

    void onUserAdd(ZegoRoomUser zegoUse);

    void onUserRemove(ZegoRoomUser zegoUser);
}
