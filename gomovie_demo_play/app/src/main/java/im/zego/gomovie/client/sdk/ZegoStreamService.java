package im.zego.gomovie.client.sdk;

import android.util.Log;
import android.view.TextureView;

import java.util.ArrayList;
import java.util.List;

import im.zego.gomovie.client.sdk.callbacks.IRTCCommonCallback;
import im.zego.gomovie.client.sdk.callbacks.IStreamCountListener;
import im.zego.gomovie.client.sdk.model.ZegoStream;

public class ZegoStreamService {

    private static final String TAG = "ZegoStreamService";

    private IZegoVideoSDKProxy mSDKProxy = null;
    /**
     * 包含自己的流,
     */
    private List<ZegoStream> mStreamList = new ArrayList<ZegoStream>();
    private String mGenerateStreamID = null;

    private IStreamCountListener mStreamCountListener = null;

    public ZegoStreamService(IZegoVideoSDKProxy sdkProxy) {
        mSDKProxy = sdkProxy;
    }

    public String generatePublishStreamID(String userID) {

        if (mGenerateStreamID == null) {
            mGenerateStreamID = "a_movie_" + userID;
        }
        return mGenerateStreamID;
    }

    void registerCallback() {

        mSDKProxy.setStreamCountListener(new IStreamCountListener() {
            @Override
            public void onStreamAdd(ZegoStream zegostream) {
                addStream(zegostream);
                zegostream.setCameraStatus(true);
                zegostream.setMicPhoneStatus(true);
                if (mStreamCountListener != null) {
                    mStreamCountListener.onStreamAdd(zegostream);
                }
            }

            @Override
            public void onStreamRemove(ZegoStream zegostream) {
                ZegoStream streamTemp = null;
                for (ZegoStream stream : mStreamList) {
                    if (stream.mStreamID.equals(zegostream.mStreamID)) {
                        streamTemp = stream;
                        break;
                    }
                }

                if (streamTemp != null) {
                    removeStream(streamTemp);
                    if (mStreamCountListener != null) {
                        mStreamCountListener.onStreamRemove(streamTemp);
                    }
                }
            }
        });
    }

    void unRegisterCallback() {
        mSDKProxy.setStreamCountListener(null);
    }

    /**
     * 推流的时候，推流成功就表示流已经激活了
     */
    public void startPublishStream(
            String streamID, String userName,
            IRTCCommonCallback publishResult
    ) {
        mSDKProxy.startPublishing(streamID, userName, new IRTCCommonCallback() {
            @Override
            public void onRTCCallback(int errorCode) {
                publishResult.onRTCCallback(errorCode);
            }
        });
        Log.i(TAG, "startPublishStream:streamID:" + streamID + ":userName:" + userName);
    }

    public void publishWithVideo(boolean enable) {
        mSDKProxy.muteVideoPublish(!enable);
        Log.i(TAG, "publishWithVideo:" + enable);
    }

    public void publishWithAudio(boolean enable) {
        mSDKProxy.muteAudioPublish(!enable);
        Log.i(TAG, "publishWithAudio:" + enable);
    }

    public void stopPublishStream() {
        mSDKProxy.stopPublishing();
        Log.i(TAG, "stopPublishStream");
    }

    public void playWithVideo(String streamID, boolean enable) {
        mSDKProxy.activateVideoPlayStream(streamID, enable);
        Log.i(TAG, "playWithVideo:streamID:" + streamID + ":enable:" + enable);
    }

    public void playWithAudio(String streamID, boolean enable) {
        mSDKProxy.activateAudioPlayStream(streamID, enable);
        Log.i(TAG, "playWithAudio:streamID:" + streamID + ":enable:" + enable);
    }

    public void startPlayStream(
            String streamID,
            TextureView view,
            IRTCCommonCallback playResult
    ) {
        mSDKProxy.startPlayingStream(streamID, view, new IRTCCommonCallback() {
            @Override
            public void onRTCCallback(int errorCode) {
                playResult.onRTCCallback(errorCode);
            }
        });
        Log.i(TAG, "startPlayStream:streamID:" + streamID + ":view:" + view);
    }

    public void stopPlayStream(String streamID) {
        mSDKProxy.stopPlayingStream(streamID);
        Log.i(TAG, "stopPlayStream:streamID:" + streamID);
    }

    public void addStream(ZegoStream streamInfo) {
        if (!mStreamList.contains(streamInfo)) {
            mStreamList.add(streamInfo);
        }
    }

    public void removeStream(ZegoStream stream) {
        mStreamList.remove(stream);
    }

    public ZegoStream getStream(String streamID) {
        for (ZegoStream stream : mStreamList) {
            if (stream.mStreamID.equals(streamID)) {
                return stream;
            }
        }

        return null;
    }

    public ZegoStream getStreamByUser(String userID) {
        for (ZegoStream stream : mStreamList) {
            if (stream.mUserID.equals(userID)) {
                return stream;
            }
        }
        return null;
    }

    public List<ZegoStream> getStreamList() {
        return mStreamList;
    }

    public int getStreamCount() {
        return mStreamList.size();
    }

    public void clearAll() {
        clearRoomData();
        unRegisterCallback();
    }

    public void clearRoomData() {
        Log.i(TAG, "clearRoomData:");
        stopPublishStream();
        mStreamList.clear();
        mGenerateStreamID = null;
    }

    public void setStreamCountListener(IStreamCountListener streamCountListener) {
        this.mStreamCountListener = streamCountListener;
    }

}
