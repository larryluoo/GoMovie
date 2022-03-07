package im.zego.gomovie.client.sdk;

import android.app.Application;

import im.zego.gomovie.client.AuthConstants;
import im.zego.gomovie.client.sdk.callbacks.IRTCEventCallback;
import im.zego.gomovie.client.sdk.callbacks.IRemoteDeviceStateListener;
import im.zego.gomovie.client.sdk.callbacks.IRoomStateCallback;
import im.zego.gomovie.client.sdk.callbacks.IStreamCountListener;
import im.zego.gomovie.client.sdk.callbacks.IUserCountListener;
import im.zego.gomovie.client.sdk.callbacks.IZegoRoomConnectionStateListener;

public class ZegoSDKManager {

    public static final int DEVICE_STATUS_OPEN = 0;
    public static final int DEVICE_STATUS_CLOSE = 1;

    private Application application;

    private ZegoSDKManager() {
    }

    private static final class Holder {
        private static final ZegoSDKManager INSTANCE = new ZegoSDKManager();
    }

    public static ZegoSDKManager getInstance() {
        return Holder.INSTANCE;
    }

    private IZegoVideoSDKProxy zegoSDKProxy = new ZegoExpressWrapper();

    private ZegoStreamService streamService = new ZegoStreamService(zegoSDKProxy);
    private ZegoDeviceService deviceService = new ZegoDeviceService(zegoSDKProxy);
    private ZegoRoomService roomService = new ZegoRoomService(zegoSDKProxy);


    public void initSDK(Application application) {
        // 在这里面配置测试环境等开关
        initVideoSDK(application);
    }

    private void initVideoSDK(Application application) {
        this.application = application;
        zegoSDKProxy.initSDK(application, getAppID(), getAppSign());
    }

    public void uninitSDK() {
        zegoSDKProxy.unInitSDK();
    }

    public boolean isInit() {
        return zegoSDKProxy.isInit();
    }

    private long getAppID() {
        return AuthConstants.APP_ID;
    }

    private String getAppSign() {
        return AuthConstants.APP_SIGN;
    }

    public void uploadLog() {
        zegoSDKProxy.uploadLog();
    }

    public void setStreamCountListener(IStreamCountListener listener) {
        streamService.setStreamCountListener(listener);
    }

    public void setUserCountListener(IUserCountListener listener) {
        roomService.setUserCountListener(listener);
    }

    public void setRoomConnectionStateListener(IZegoRoomConnectionStateListener listener) {
        roomService.setZegoRoomConnectionStateListener(listener);
    }

    public void setRtcEventCallback(IRTCEventCallback listener) {
        roomService.setRtcEventCallback(listener);
    }

    public void setRoomStateCallback(IRoomStateCallback roomStateCallback) {
        roomService.setRoomStateCallback(roomStateCallback);
    }

    public ZegoStreamService getStreamService() {
        return streamService;
    }

    public ZegoDeviceService getDeviceService() {
        return deviceService;
    }

    public ZegoRoomService getRoomService() {
        return roomService;
    }

    public void setRemoteDeviceListener(IRemoteDeviceStateListener listener) {
        deviceService.setRemoteDeviceListener(listener);
    }

}
