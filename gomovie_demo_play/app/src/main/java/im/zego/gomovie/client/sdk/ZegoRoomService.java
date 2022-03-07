package im.zego.gomovie.client.sdk;

import android.util.Log;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import im.zego.gomovie.client.model.CommandInfo;
import im.zego.gomovie.client.sdk.callbacks.IBroadcastMessageListener;
import im.zego.gomovie.client.sdk.callbacks.ICustomCommandListener;
import im.zego.gomovie.client.sdk.callbacks.IRTCCommonCallback;
import im.zego.gomovie.client.sdk.callbacks.IRTCEventCallback;
import im.zego.gomovie.client.sdk.callbacks.IRoomExtraInfoUpdateListener;
import im.zego.gomovie.client.sdk.callbacks.IRoomStateCallback;
import im.zego.gomovie.client.sdk.callbacks.IRoomUserListener;
import im.zego.gomovie.client.sdk.callbacks.IUserCountListener;
import im.zego.gomovie.client.sdk.callbacks.IZegoIMSendMessageCallback;
import im.zego.gomovie.client.sdk.callbacks.IZegoRoomConnectionStateListener;
import im.zego.gomovie.client.sdk.model.ZegoRoomUser;
import im.zego.zegoexpress.entity.ZegoBroadcastMessageInfo;
import im.zego.zegoexpress.entity.ZegoRoomExtraInfo;

public class ZegoRoomService {

    public static final int OUTSIDE = 0;
    public static final int PENDING_ENTER = 1;
    public static final int ENTERED = 2;

    private static final String TAG = "ZegoRoomService";

    private int mState = OUTSIDE;
    private String mRoomID = "";
    private Gson gson = new Gson();
    private IZegoVideoSDKProxy mSDKProxy = null;
    private IZegoRoomConnectionStateListener mZegoRoomConnectionStateListener = null;
    private IRTCEventCallback mRtcEventCallback = null;
    private IRTCCommonCallback mLoginResult = null;
    private IRoomStateCallback mRoomStateCallback = null;
    private IUserCountListener mUserCountListener = null;

    private List<ZegoRoomUser> mUserList = new ArrayList<ZegoRoomUser>();


    public ZegoRoomService(IZegoVideoSDKProxy sdkProxy) {
        mSDKProxy = sdkProxy;
    }

    private void registerCallback() {

        mSDKProxy.setBroadcastMessageListener(new IBroadcastMessageListener() {
            @Override
            public void onRecvBroadcastMessage(ArrayList<ZegoBroadcastMessageInfo> messageList, String roomID) {
                if (mRtcEventCallback != null) {
                    mRtcEventCallback.onRecvBroadcastMessage(messageList);
                }
            }
        });

        mSDKProxy.setCustomCommandListener(new ICustomCommandListener() {
            @Override
            public void onRecvCustomCommand(String userID, String userName, String content, String roomID) {
                if (mRtcEventCallback != null) {
                    mRtcEventCallback.onCommandInfo(gson.fromJson(content, CommandInfo.class));
                }
            }
        });

        mSDKProxy.setRoomExtraInfoUpdateListener(new IRoomExtraInfoUpdateListener() {
            @Override
            public void onRoomExtraInfoUpdate(String roomID, ZegoRoomExtraInfo roomExtraInfo) {
                if (mRtcEventCallback != null) {
                    mRtcEventCallback.onRoomExtraInfoUpdate(roomID, roomExtraInfo);
                }
            }
        });

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

        mSDKProxy.setRoomUserListener(new IRoomUserListener() {
            @Override
            public void onUserAdd(ZegoRoomUser zegoUser) {
                addUser(zegoUser);
                if (mUserCountListener != null) {
                    mUserCountListener.onUserAdd(zegoUser);
                }
            }

            @Override
            public void onUserRemove(ZegoRoomUser zegoUser) {
                ZegoRoomUser userTemp = null;
                for (ZegoRoomUser user : mUserList) {
                    if (user.getUserID().equals(zegoUser.getUserID())) {
                        userTemp = user;
                        break;
                    }
                }

                if (userTemp != null) {
                    removeUser(userTemp);
                    if (mUserCountListener != null) {
                        mUserCountListener.onUserRemove(userTemp);
                    }
                }
            }
        });

    }

    public void addUser(ZegoRoomUser zegoUser) {
        if (!mUserList.contains(zegoUser)) {
            mUserList.add(zegoUser);
        }
    }

    public void removeUser(ZegoRoomUser zegoUser) {
        mUserList.remove(zegoUser);
    }

    void unRegisterCallback() {
        mSDKProxy.setBroadcastMessageListener(null);
        mSDKProxy.setCustomCommandListener(null);
        mSDKProxy.setZegoRoomConnectionStateListener(null);
        mSDKProxy.setRoomUserListener(null);
        mSDKProxy.setRoomExtraInfoUpdateListener(null);

    }

    public void clearAll() {
        unRegisterCallback();
        mUserList.clear();
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

    private void onRoomEntered() {
        Log.i(TAG, "onRoomEntered");
        ZegoDeviceService deviceService = ZegoSDKManager.getInstance().getDeviceService();
        mSDKProxy.requireHardwareDecoder(true);
        mSDKProxy.requireHardwareEncoder(true);
        deviceService.enableSpeaker(true);
        deviceService.enableMic(true);
        deviceService.enableCamera(true);
        deviceService.setFrontCamera(true);
        deviceService.enableAudio3a(true);
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

    /**
     * 发送消息
     *
     * @param roomID
     */
    public void sendRoomMsg(ZegoBroadcastMessageInfo messageInfo, String roomID, IZegoIMSendMessageCallback callback) {
        mSDKProxy.sendRoomMsg(messageInfo, roomID, callback);
    }

    boolean isInRoom() {
        return mState == ENTERED;
    }

    public List<ZegoRoomUser> getmUserList() {
        return mUserList;
    }

    public Boolean getUserIsService() {
        for (ZegoRoomUser user : mUserList) {
            if (user.getUserName().indexOf("userName_") != -1) {
                return true;
            }
        }
        return false;
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

    public void setUserCountListener(IUserCountListener userCountListener) {
        this.mUserCountListener = userCountListener;
    }

}
