package im.zego.gomovie.server.sdk.model;

import android.view.TextureView;

import im.zego.gomovie.server.sdk.callbacks.IRTCCommonCallback;
import im.zego.gomovie.server.sdk.callbacks.IStreamActiveCallback;


public class ZegoPublishStream extends ZegoStream {

    public ZegoPublishStream(String userID, String userName, String streamID) {
        super(userID, userName, streamID);
    }

    @Override
    public void enableVideo(boolean enable) {
        mStreamService.publishWithVideo(enable);
    }

    @Override
    public void enableAudio(boolean enable) {
        mStreamService.publishWithAudio(enable);
    }

    @Override
    public void activeStream(TextureView view, IStreamActiveCallback activeResult) {
        // 如果连续点击麦克风和摄像头，会导致创建两个流对象，因此先加进来，失败再移除
        if (mSteamStatus == STREAM_INIT) {
            mSteamStatus = STREAM_PENDING_START;
            mStreamService.addStream(this);
            mStreamService.startPublishStream(mStreamID, mUserName, new IRTCCommonCallback() {
                @Override
                public void onRTCCallback(int errorCode) {
                    setStreamActive(errorCode == 0);
                    if (errorCode == 0) {
                        if (mStreamService.getStream(mStreamID) == null) {
                            mStreamService.addStream(ZegoPublishStream.this);
                        }
                    } else {
                        mStreamService.removeStream(ZegoPublishStream.this);
                    }
                    activeResult.onStreamActive(errorCode, mStreamID);
                }
            });
        }
    }

    @Override
    public void inActiveStream() {
        if (mSteamStatus == STREAM_START) {
            // 成功或者失败都设置为初始状态
            mStreamService.stopPublishStream();
            mStreamService.removeStream(this);
        }
        setStreamActive(false);
    }
}
