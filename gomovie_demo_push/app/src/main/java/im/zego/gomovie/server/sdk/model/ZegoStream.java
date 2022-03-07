package im.zego.gomovie.server.sdk.model;

import android.view.TextureView;

import im.zego.gomovie.server.sdk.IZegoVideoSDKProxy;
import im.zego.gomovie.server.sdk.ZegoSDKManager;
import im.zego.gomovie.server.sdk.ZegoStreamService;
import im.zego.gomovie.server.sdk.callbacks.IStreamActiveCallback;


abstract public class ZegoStream {

    public static final String TAG = "ZegoStream";

    public static final int STREAM_INIT = 1;
    public static final int STREAM_PENDING_START = 2;
    public static final int STREAM_START = 3;

    public int mSteamStatus = STREAM_INIT;
    public final String mUserID;
    public final String mUserName;
    public final String mStreamID;


    public String extraInfo = "";
    public int cameraState = IZegoVideoSDKProxy.DEVICE_STATUS_CLOSE;
    public int micPhoneState = IZegoVideoSDKProxy.DEVICE_STATUS_CLOSE;

    public ZegoStreamService mStreamService = ZegoSDKManager.getInstance().getStreamService();


    public ZegoStream(String userID,String userName,String streamID){
        mUserID = userID;
        mUserName = userName;
        mStreamID = streamID;
    }


    public boolean isCameraOpen() {
        return cameraState == IZegoVideoSDKProxy.DEVICE_STATUS_OPEN;
    }

    public boolean isMicPhoneOpen() {
        return micPhoneState == IZegoVideoSDKProxy.DEVICE_STATUS_OPEN;
    }

    public void setCameraStatus(boolean open) {
          if (open)
              cameraState = IZegoVideoSDKProxy.DEVICE_STATUS_OPEN;
            else
              cameraState = IZegoVideoSDKProxy.DEVICE_STATUS_CLOSE;
    }

    public void  setMicPhoneStatus(boolean open) {
          if (open)
              micPhoneState = IZegoVideoSDKProxy.DEVICE_STATUS_OPEN;
          else
              micPhoneState = IZegoVideoSDKProxy.DEVICE_STATUS_CLOSE;
    }

    public boolean isStreamActive() {
        return mSteamStatus == STREAM_START;
    }

    public void setStreamActive(boolean success) {
        if (success)
            mSteamStatus = STREAM_START;
        else
            mSteamStatus = STREAM_INIT;
    }

    abstract void enableVideo(boolean enable);

    abstract void enableAudio(boolean enable);

    abstract void activeStream(TextureView view, IStreamActiveCallback activeResult);

    abstract void inActiveStream();

    @Override
    public String toString() {
        return "ZegoStream(userID='"+mUserID+"', userName='"+mUserName+"', streamID='"+mStreamID+"',  extraInfo='"+extraInfo+"', cameraState="+cameraState+", micPhoneState="+micPhoneState+", streamService="+mStreamService+", steamStatus="+mSteamStatus+")";
    }
}
