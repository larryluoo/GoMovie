package im.zego.gomovie.server.sdk.callbacks;

import im.zego.zegoexpress.entity.ZegoUser;

public interface IRoomUserListener {
    void onUserAdd(ZegoUser zegoUser);
    void onUserRemove(ZegoUser zegoUser);
}
