package im.zego.gomovie.server.sdk.callbacks;

import im.zego.gomovie.server.sdk.model.ZegoStream;

public interface IStreamCountListener {

    void onStreamAdd(ZegoStream zegostream);
    void onStreamRemove(ZegoStream zegostream);
}
