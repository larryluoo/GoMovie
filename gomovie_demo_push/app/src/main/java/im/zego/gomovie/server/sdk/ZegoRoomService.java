package im.zego.gomovie.server.sdk;

import android.util.Log;
import java.util.ArrayList;

import im.zego.gomovie.server.sdk.callbacks.IRoomExtraInfoUpdateListener;
import im.zego.gomovie.server.sdk.callbacks.IRTCCommonCallback;
import im.zego.gomovie.server.sdk.callbacks.IRTCEventCallback;
import im.zego.gomovie.server.sdk.callbacks.IRoomStateCallback;
import im.zego.gomovie.server.sdk.callbacks.IRoomUserListener;
import im.zego.gomovie.server.sdk.callbacks.IZegoRoomConnectionStateListener;
import im.zego.zegoexpress.callback.IZegoIMSendCustomCommandCallback;
import im.zego.zegoexpress.entity.ZegoRoomExtraInfo;
import im.zego.zegoexpress.entity.ZegoUser;

public class ZegoRoomService {

    public static final int OUTSIDE = 0;
    public static final int PENDING_ENTER = 1;
    public static final int ENTERED = 2;

    private static final String TAG = "ZegoRoomService";

    private int mState = OUTSIDE;
    private String mRoomID = "";
    private IZegoVideoSDKProxy mSDKProxy = null;
    private IZegoRoomConnectionStateListener mZegoRoomConnectionStateListener = null;
    private IRTCEventCallback mRtcEventCallback = null;
    private IRTCCommonCallback mLoginResult = null;
    private IRoomStateCallback mRoomStateCallback = null;

    public ZegoRoomService(IZegoVideoSDKProxy sdkProxy) {
        mSDKProxy = sdkProxy;
    }

    private void registerCallback() {

        mSDKProxy.setZegoRoomConnectionStateListener(new IZegoRoomConnectionStateListener() {
            @Override
            public void onConnected(int errorCode, String roomID) {
                Log.i(TAG, "onConnected:errorCode:" + errorCode + ":roomID:" + roomID);
                if (mZegoRoomConnectionStateListener != null) {
                    mZegoRoomConnectionStateListener.onConnected(errorCode, roomID);
                }
            }

            @Override
            public void onDisconnect(int errorCode, String roomID) {
                Log.i(TAG, "onDisconnect:errorCode:" + errorCode + ":roomID:" + roomID);
                if (mZegoRoomConnectionStateListener != null) {
                    mZegoRoomConnectionStateListener.onDisconnect(errorCode, roomID);
                }
            }

            @Override
            public void connecting(int errorCode, String roomID) {
                Log.i(TAG, "connecting:errorCode:" + errorCode + ":roomID:" + roomID);
                if (mZegoRoomConnectionStateListener != null) {
                    mZegoRoomConnectionStateListener.connecting(errorCode, roomID);
                }
            }
        });

        mSDKProxy.setRoomExtraInfoUpdateListener(new IRoomExtraInfoUpdateListener() {
            @Override
            public void onRoomExtraInfoUpdate(String roomID, ZegoRoomExtraInfo roomExtraInfo) {
                Log.i(TAG, "onRoomExtraInfoUpdate:" + roomExtraInfo.key + "----" + roomExtraInfo.value + ":roomID:" + roomID);
                if (mRtcEventCallback != null) {
                    mRtcEventCallback.onRoomExtraInfoUpdate(roomID, roomExtraInfo);
                }
            }
        });

        mSDKProxy.setRoomUserListener(new IRoomUserListener() {
            @Override
            public void onUserAdd(ZegoUser zegoUser) {
                if (mRtcEventCallback != null) {
                    mRtcEventCallback.onUserAdd(zegoUser);
                }
            }

            @Override
            public void onUserRemove(ZegoUser zegoUser) {
                if (mRtcEventCallback != null) {
                    mRtcEventCallback.onUserRemove(zegoUser);
                }
            }
        });


    }

    void unRegisterCallback() {
        mSDKProxy.setBroadcastMessageListener(null);
        mSDKProxy.setCustomCommandListener(null);
        mSDKProxy.setZegoRoomConnectionStateListener(null);

    }

    public void clearAll() {
        unRegisterCallback();
    }


    public void loginRoom(
            String userID,
            String userName,
            String roomID,
            final IRTCCommonCallback callback
    ) {
        Log.i(TAG, "loginRoom:userID:" + userID + ":userName:" + userName + ":roomID:" + roomID);
        mLoginResult = callback;
        if (mState != OUTSIDE) {
            return;
        }
        registerCallback();
        ZegoSDKManager.getInstance().getDeviceService().registerCallback();
        ZegoSDKManager.getInstance().getStreamService().registerCallback();

        mState = PENDING_ENTER;
        if (mRoomStateCallback != null) {
            mRoomStateCallback.onPendingEnter();
        }

        mSDKProxy.loginRoom(userID, userName, roomID, new IRTCCommonCallback() {
            @Override
            public void onRTCCallback(int errorCode) {

                if (errorCode == 0) {
                    mRoomID = roomID;
                    mState = ENTERED;
                    if (mRoomStateCallback != null) {
                        mRoomStateCallback.onEntered();
                    }
                    onRoomEntered();
                } else {
                    mState = OUTSIDE;
                    exitRoom();
                }

                if (mLoginResult != null) {
                    mLoginResult.onRTCCallback(errorCode);
                    mLoginResult = null;
                }
            }
        });
    }

    public void setRoomExtraInfo(String roomID, String key, String value, IRTCCommonCallback callback) {
        mSDKProxy.setRoomExtraInfo(roomID, key, value, callback);
    }

    public void sendCustomCommand(String roomID, String command, ArrayList<ZegoUser> toUserList, IZegoIMSendCustomCommandCallback callback){
        mSDKProxy.sendCustomCommand(roomID,command,toUserList,callback);
    }

    private void onRoomEntered() {
        Log.i(TAG, "onRoomEntered");
        ZegoDeviceService deviceService = ZegoSDKManager.getInstance().getDeviceService();
        mSDKProxy.requireHardwareDecoder(true);
        mSDKProxy.requireHardwareEncoder(true);
        deviceService.enableSpeaker(true);
        deviceService.enableMic(false);
        deviceService.enableCamera(true);
    }

    public void exitRoom() {
        Log.i(TAG, "exitRoom");
        ZegoSDKManager.getInstance().getStreamService().clearAll();
        ZegoSDKManager.getInstance().getDeviceService().clearAll();
        mSDKProxy.logoutRoom(mRoomID);
        mState = OUTSIDE;
        if (mRoomStateCallback != null) {
            mRoomStateCallback.onExitRoom();
        }
        mRoomStateCallback = null;
        mZegoRoomConnectionStateListener = null;
        mRoomID = "";
        clearAll();
    }

    boolean isInRoom() {
        return mState == ENTERED;
    }


    public void setZegoRoomConnectionStateListener(IZegoRoomConnectionStateListener zegoRoomConnectionStateListener) {
        this.mZegoRoomConnectionStateListener = zegoRoomConnectionStateListener;
    }

    public void setRtcEventCallback(IRTCEventCallback rtcEventCallback) {
        this.mRtcEventCallback = rtcEventCallback;
    }

    public void setRoomStateCallback(IRoomStateCallback roomStateCallback) {
        this.mRoomStateCallback = roomStateCallback;
    }
}
