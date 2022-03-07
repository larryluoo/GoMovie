package im.zego.gomovie.server.sdk.model;

import android.view.TextureView;

import im.zego.gomovie.server.sdk.callbacks.IRTCCommonCallback;
import im.zego.gomovie.server.sdk.callbacks.IStreamActiveCallback;


public class ZegoPlayStream extends ZegoStream{

    public ZegoPlayStream(String userID,String userName,String streamID){
       super(userID,userName,streamID);

        setCameraStatus(true);
        setMicPhoneStatus(true);
        setStreamActive(false);
    }

    @Override
    void enableVideo(boolean enable) {
        mStreamService.playWithVideo(mStreamID, enable);
    }

    @Override
    void enableAudio(boolean enable) {
        mStreamService.playWithAudio(mStreamID, enable);
    }

    @Override
    public void activeStream(TextureView view, IStreamActiveCallback activeResult) {
        if (mSteamStatus == STREAM_INIT) {
            mStreamService.startPlayStream(mStreamID, view, new IRTCCommonCallback() {
                @Override
                public void onRTCCallback(int errorCode) {
                    setStreamActive(errorCode == 0);
                    activeResult.onStreamActive(errorCode, mStreamID);
                }
            });
        }
    }

    @Override
    void inActiveStream() {
        if (mSteamStatus == STREAM_START) {
            mStreamService.stopPlayStream(mStreamID);
        }
        setStreamActive(false);
    }
}
