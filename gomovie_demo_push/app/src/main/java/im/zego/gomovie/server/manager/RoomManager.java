package im.zego.gomovie.server.manager;

import android.os.SystemClock;
import android.view.TextureView;

import im.zego.gomovie.server.sdk.ZegoSDKManager;
import im.zego.gomovie.server.Constants;
import im.zego.gomovie.server.manager.callbacks.INotifyCallback;
import im.zego.gomovie.server.sdk.callbacks.IRTCCommonCallback;
import im.zego.gomovie.server.sdk.callbacks.IRTCEventCallback;
import im.zego.gomovie.server.sdk.callbacks.IStreamActiveCallback;
import im.zego.gomovie.server.sdk.callbacks.IZegoVideoCaptureCallback;
import im.zego.gomovie.server.sdk.model.ZegoPublishStream;
import im.zego.zegoexpress.constants.ZegoPublishChannel;
import im.zego.zegoexpress.entity.ZegoRoomExtraInfo;
import im.zego.zegoexpress.entity.ZegoUser;

public class RoomManager {

    private static final String TAG = "RoomManager";
    private static volatile RoomManager singleton = null;

    private long usserId;
    private String userName;
    private boolean canSenRawData = false;

    private INotifyCallback mINotifyCallback;

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
    }

    private IRTCEventCallback mRTCEventCallback = new IRTCEventCallback() {
        @Override
        public void onRoomCloseNotify() {

        }

        @Override
        public void onRoomExtraInfoUpdate(String roomID, ZegoRoomExtraInfo roomExtraInfo) {
            if (mINotifyCallback != null) {
                mINotifyCallback.onRoomExtraInfoUpdate(roomID, roomExtraInfo);
            }
        }

        @Override
        public void onUserAdd(ZegoUser zegoUser) {
            if (mINotifyCallback != null) {
                mINotifyCallback.onUserAdd(zegoUser);
            }
        }

        @Override
        public void onUserRemove(ZegoUser zegoUser) {
            if (mINotifyCallback != null) {
                mINotifyCallback.onUserRemove(zegoUser);
            }
        }
    };

    /**
     * 创建房间
     *
     * @param roomId
     * @param commonCallback
     */
    public void createRoom(String roomId, IRTCCommonCallback commonCallback) {
        usserId = getBoostTimeMillis();
        userName = "userName_" + usserId;
        ZegoSDKManager.getInstance().getRoomService().loginRoom(String.valueOf(usserId),
                userName,
                roomId,
                new IRTCCommonCallback() {
                    @Override
                    public void onRTCCallback(int errorCode) {
                        if(errorCode == 0){
                            setRoomInfo();
                        }
                        if (commonCallback != null) {
                            commonCallback.onRTCCallback(errorCode);
                        }
                    }
                });
    }

    public void setCustomVideoCaptureHandler() {
        ZegoSDKManager.getInstance().getDeviceService().setCustomVideoCaptureHandler(new IZegoVideoCaptureCallback() {
            @Override
            public void onStart(ZegoPublishChannel channel) {
                canSenRawData = true;
            }

            @Override
            public void onStop(ZegoPublishChannel channel) {
                canSenRawData = false;
            }
        });
    }


    /**
     * 推流
     *
     * @param view
     * @param commonCallback
     */
    public void startPublishStream(TextureView view, IRTCCommonCallback commonCallback) {
        String streamId = ZegoSDKManager.getInstance().getStreamService().generatePublishStreamID(String.valueOf(usserId));
        ZegoPublishStream zegoPublishStream = new ZegoPublishStream(String.valueOf(usserId), userName, streamId);
        zegoPublishStream.activeStream(view, new IStreamActiveCallback() {
            @Override
            public void onStreamActive(int errorCode, String streamID) {
                if (commonCallback != null) {
                    commonCallback.onRTCCallback(errorCode);
                }
            }
        });
    }

    public void setRoomExtraInfo(String roomId, String status) {
        ZegoSDKManager.getInstance().getRoomService().setRoomExtraInfo(roomId, Constants.MOVIE_EXTRA_INFO, status, new IRTCCommonCallback() {
            @Override
            public void onRTCCallback(int errorCode) {
                if(errorCode == 0){
                    if(mINotifyCallback != null && status.equals(Constants.MOVIE_ROOM_CLOSE)){
                        mINotifyCallback.onRoomCloseNotify();
                    }
                }
            }
        });
    }

    public long getBoostTimeMillis() {
        return SystemClock.elapsedRealtime();
    }

    public void logoutRoom() {
        setINotifyCallbackListener(null);
        setCanSenRawData(false);
        ZegoSDKManager.getInstance().getStreamService().stopPublishStream();
        ZegoSDKManager.getInstance().getRoomService().exitRoom();
    }

    public boolean isCanSenRawData() {
        return canSenRawData;
    }

    public void setCanSenRawData(boolean canSenRawData) {
        this.canSenRawData = canSenRawData;
    }

    public void setINotifyCallbackListener(INotifyCallback iNotifyCallback) {
        this.mINotifyCallback = iNotifyCallback;
    }

    public long getUsserId() {
        return usserId;
    }


}
