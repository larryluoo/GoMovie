package im.zego.gomovie.client.sdk;

import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import im.zego.gomovie.client.sdk.callbacks.IRemoteDeviceStateListener;
import im.zego.gomovie.client.sdk.model.ZegoStream;

public class ZegoDeviceService {

    private static final String TAG = "ZegoDeviceService";

    private IZegoVideoSDKProxy mSDKProxy = null;

    private IRemoteDeviceStateListener mRemoteDeviceListener = null;

    public ZegoDeviceService(IZegoVideoSDKProxy sdkProxy) {
        mSDKProxy = sdkProxy;
    }

    public void registerCallback() {
        mSDKProxy.setRemoteDeviceEventCallback(new IRemoteDeviceStateListener() {
            @Override
            public void onRemoteCameraStatusUpdate(String streamID, int status) {
                boolean enable = status == IZegoVideoSDKProxy.DEVICE_STATUS_OPEN;
                ZegoStreamService streamService = ZegoSDKManager.getInstance().getStreamService();
                ZegoStream stream = streamService.getStream(streamID);
                if (stream != null) {
                    stream.setCameraStatus(enable);
                }
                if (mRemoteDeviceListener != null) {
                    mRemoteDeviceListener.onRemoteCameraStatusUpdate(streamID, status);
                }
            }

            @Override
            public void onRemoteMicStatusUpdate(String streamID, int status) {
                boolean enable = status == IZegoVideoSDKProxy.DEVICE_STATUS_OPEN;
                ZegoStreamService streamService = ZegoSDKManager.getInstance().getStreamService();
                ZegoStream stream = streamService.getStream(streamID);
                if (stream != null) {
                    stream.setMicPhoneStatus(enable);
                }
                if (mRemoteDeviceListener != null) {
                    mRemoteDeviceListener.onRemoteMicStatusUpdate(streamID, status);
                }
            }

        });
    }

    public void unRegisterCallback() {
        mSDKProxy.setRemoteDeviceEventCallback(null);
    }

    public void clearAll() {
        unRegisterCallback();
        clearRoomData();
    }

    private void clearRoomData() {
        Log.i(TAG, "clearRoomData");
        enableMic(false);
        enableCamera(false);
        enableSpeaker(false);
    }

    public void setRoomData(boolean enable) {
        enableMic(enable);
        enableCamera(enable);
        Log.i(TAG, "setRoomData:" + enable);
    }

    public void setFrontCamera(boolean front) {
        mSDKProxy.setFrontCam(front);
        Log.i(TAG, "setFrontCamera:" + front);
    }

    public void enableMic(boolean enable) {
        mSDKProxy.enableMic(enable);
        Log.i(TAG, "enableMic:" + enable);
    }

    public void enableCamera(boolean enable) {
        mSDKProxy.enableCamera(enable);
        Log.i(TAG, "enableCamera:" + enable);
    }

    public void enableSpeaker(boolean enable) {
        mSDKProxy.enableSpeaker(enable);
        Log.i(TAG, "enableSpeaker:" + enable);
    }

    public void enableAudio3a(boolean enable) {
        mSDKProxy.enableAudio3a(enable);
        Log.i(TAG, "enableAudio3a:" + enable);
    }

    public void startPreview(TextureView view) {
        mSDKProxy.setAppOrientation(Surface.ROTATION_0);
        mSDKProxy.startPreview(view);
        Log.i(TAG, "startPreview");
    }

    public void stopPreview() {
        mSDKProxy.stopPreview();
    }


    public void setRemoteDeviceListener(IRemoteDeviceStateListener remoteDeviceListener) {
        this.mRemoteDeviceListener = remoteDeviceListener;
    }

    public void setVideoConfig(int width, int height, int bitrate, int fps) {
        mSDKProxy.setVideoConfig(width, height, bitrate, fps);
        Log.i(TAG, "setVideoConfig:width:" + width + ":height:" + height);
    }
}
