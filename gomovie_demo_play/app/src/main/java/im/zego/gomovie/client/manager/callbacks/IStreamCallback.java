package im.zego.gomovie.client.manager.callbacks;

import im.zego.gomovie.client.sdk.model.ZegoStream;

public interface IStreamCallback {

    void onStreamAdd(ZegoStream zegostream);

    void onStreamRemove(ZegoStream zegostream);
}
