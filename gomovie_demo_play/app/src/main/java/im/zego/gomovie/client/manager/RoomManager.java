package im.zego.gomovie.client.manager;


import android.view.TextureView;

import java.util.ArrayList;

import im.zego.gomovie.client.Constants;
import im.zego.gomovie.client.manager.callbacks.INotifyCallback;
import im.zego.gomovie.client.manager.callbacks.IStreamCallback;
import im.zego.gomovie.client.model.CommandInfo;
import im.zego.gomovie.client.model.UserInfo;
import im.zego.gomovie.client.sdk.ZegoSDKManager;
import im.zego.gomovie.client.sdk.callbacks.IRTCCommonCallback;
import im.zego.gomovie.client.sdk.callbacks.IRTCEventCallback;
import im.zego.gomovie.client.sdk.callbacks.IRemoteDeviceStateListener;
import im.zego.gomovie.client.sdk.callbacks.IStreamActiveCallback;
import im.zego.gomovie.client.sdk.callbacks.IStreamCountListener;
import im.zego.gomovie.client.sdk.model.ZegoPlayStream;
import im.zego.gomovie.client.sdk.model.ZegoPublishStream;
import im.zego.gomovie.client.sdk.model.ZegoStream;
import im.zego.zegoexpress.entity.ZegoBroadcastMessageInfo;
import im.zego.zegoexpress.entity.ZegoRoomExtraInfo;

public class RoomManager {

    private static final String TAG = "RoomManager";
    private static volatile RoomManager singleton = null;

    private IStreamCallback iStreamCallback;
    private INotifyCallback mINotifyCallback;
    private IRemoteDeviceStateListener mRemoteDeviceStateListener = null;

    private UserInfo audienceInfo;

    public static RoomManager getInstance() {
        if (singleton == null) {
            synchronized (RoomManager.class) {
                if (singleton == null) {
                    singleton = new RoomManager();
                }
            }
        }
        return singleton;
    }

    private void setRoomInfo() {
        ZegoSDKManager.getInstance().setRtcEventCallback(mRTCEventCallback);
        ZegoSDKManager.getInstance().setStreamCountListener(mIStreamCountListener);
        ZegoSDKManager.getInstance().setRemoteDeviceListener(mRemoteDeviceListener);
    }

    private IRTCEventCallback mRTCEventCallback = new IRTCEventCallback() {
        @Override
        public void onRoomCloseNotify() {

        }

        @Override
        public void onRoomExtraInfoUpdate(String roomID, ZegoRoomExtraInfo roomExtraInfo) {
            //?????????????????????????????? ???????????????????????? ??? ??????????????????
            if (mINotifyCallback != null) {
                mINotifyCallback.onRoomExtraInfoUpdate(roomID, roomExtraInfo);
            }
        }

        @Override
        public void onRecvBroadcastMessage(ArrayList<ZegoBroadcastMessageInfo> messageList) {
            //????????????
            if (mINotifyCallback != null) {
                mINotifyCallback.onRecvBroadcastMessage(messageList);
            }
        }

        @Override
        public void onCommandInfo(CommandInfo info) {
            //???????????????
            if (mINotifyCallback != null) {
                mINotifyCallback.onCommandInfo(info);
            }
        }
    };

    private IRemoteDeviceStateListener mRemoteDeviceListener = new IRemoteDeviceStateListener() {
        @Override
        public void onRemoteCameraStatusUpdate(String streamID, int status) {
            //????????????????????????
            if (audienceInfo != null) {
                if (streamID.equals(audienceInfo.getStreamId())) {
                    if (mRemoteDeviceStateListener != null) {
                        mRemoteDeviceStateListener.onRemoteCameraStatusUpdate(streamID, status);
                    }
                }
            }
        }

        @Override
        public void onRemoteMicStatusUpdate(String streamID, int status) {
            //????????????????????????
            if (audienceInfo != null) {
                if (streamID.equals(audienceInfo.getStreamId())) {
                    if (mRemoteDeviceStateListener != null) {
                        mRemoteDeviceStateListener.onRemoteMicStatusUpdate(streamID, status);
                    }
                }
            }
        }
    };

    /**
     * ????????????
     *
     * @param roomId
     */
    public void loginRoom(final String roomId, IRTCCommonCallback commonCallback) {
        UserInfo userInfo = UserManager.getInstance().getUserInfo();
        ZegoSDKManager.getInstance().getRoomService().loginRoom(String.valueOf(userInfo.getUid()),
                userInfo.getNickName(),
                roomId,
                new IRTCCommonCallback() {
                    @Override
                    public void onRTCCallback(int errorCode) {
                        if (errorCode == 0) {
                            setRoomInfo();
                        }
                        if (commonCallback != null) {
                            commonCallback.onRTCCallback(errorCode);
                        }
                    }
                });
    }


    /**
     * ????????????
     */
    public void logoutRoom() {
        // ????????????
        setStreamStatusListener(null);
        setINotifyCallbackListener(null);
        setRemoteDeviceEventCallback(null);
        setAudienceInfo(null);
        ZegoSDKManager.getInstance().getRoomService().exitRoom();
        UserManager.getInstance().reset();
    }

    /**
     * ??????
     *
     * @param view
     * @param commonCallback
     */
    public void startPublishStream(TextureView view, IRTCCommonCallback commonCallback) {
        String streamId = ZegoSDKManager.getInstance().getStreamService().generatePublishStreamID(String.valueOf(UserManager.getInstance().getUserId()));
        UserManager.getInstance().getUserInfo().setStreamId(streamId);
        ZegoPublishStream zegoPublishStream = new ZegoPublishStream(String.valueOf(UserManager.getInstance().getUserId()), UserManager.getInstance().getUserInfo().getNickName(), streamId);
        zegoPublishStream.activeStream(view, new IStreamActiveCallback() {
            @Override
            public void onStreamActive(int errorCode, String streamID) {
                if (commonCallback != null) {
                    commonCallback.onRTCCallback(errorCode);
                }
            }
        });
    }

    /**
     * ??????????????????
     *
     * @param view
     * @param commonCallback
     */
    public void startPlayStream(TextureView view, ZegoStream zegostream, IRTCCommonCallback commonCallback) {
        if (zegostream == null) {
            return;
        }
        ZegoPlayStream playStream = new ZegoPlayStream(zegostream.mUserID, zegostream.mUserName, zegostream.mStreamID);
        playStream.activeStream(view, new IStreamActiveCallback() {
            @Override
            public void onStreamActive(int errorCode, String streamID) {
                if (commonCallback != null) {
                    commonCallback.onRTCCallback(errorCode);
                }
            }
        });
    }

    /**
     * ???????????????
     */
    private IStreamCountListener mIStreamCountListener = new IStreamCountListener() {
        @Override
        public void onStreamAdd(ZegoStream zegostream) {
            if (iStreamCallback != null) {
                iStreamCallback.onStreamAdd(zegostream);
            }
        }

        @Override
        public void onStreamRemove(ZegoStream zegostream) {
            if (iStreamCallback != null) {
                iStreamCallback.onStreamRemove(zegostream);
            }
        }
    };

    public void setRoomExtraInfo(String roomId, String status) {
        ZegoSDKManager.getInstance().getRoomService().setRoomExtraInfo(roomId, Constants.MOVIE_EXTRA_INFO, status, new IRTCCommonCallback() {
            @Override
            public void onRTCCallback(int errorCode) {
                if (errorCode == 0) {
                    if (mINotifyCallback != null) {
                        ZegoRoomExtraInfo roomExtraInfo = new ZegoRoomExtraInfo();
                        roomExtraInfo.key = Constants.MOVIE_EXTRA_INFO;
                        roomExtraInfo.value = status;
                        mINotifyCallback.onRoomExtraInfoUpdate(roomId, roomExtraInfo);
                    }
                }
            }
        });
    }

    public void setStreamStatusListener(IStreamCallback iStreamCallback) {
        this.iStreamCallback = iStreamCallback;
    }

    public void setINotifyCallbackListener(INotifyCallback iNotifyCallback) {
        this.mINotifyCallback = iNotifyCallback;
    }

    public void setRemoteDeviceEventCallback(IRemoteDeviceStateListener remoteDeviceStateListener) {
        mRemoteDeviceStateListener = remoteDeviceStateListener;
    }

    public UserInfo getAudienceInfo() {
        return audienceInfo;
    }

    public void setAudienceInfo(UserInfo audienceInfo) {
        this.audienceInfo = audienceInfo;
    }
}
