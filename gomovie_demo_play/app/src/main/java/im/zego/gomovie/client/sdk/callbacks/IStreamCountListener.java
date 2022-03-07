package im.zego.gomovie.client.sdk.callbacks;


import im.zego.gomovie.client.sdk.model.ZegoStream;

public interface IStreamCountListener {

    void onStreamAdd(ZegoStream zegostream);
    void onStreamRemove(ZegoStream zegostream);
}
