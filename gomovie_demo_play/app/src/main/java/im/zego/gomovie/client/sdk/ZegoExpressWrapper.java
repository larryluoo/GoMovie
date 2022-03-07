package im.zego.gomovie.client.sdk;

import android.app.Application;
import android.view.TextureView;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import im.zego.gomovie.client.sdk.callbacks.IBroadcastMessageListener;
import im.zego.gomovie.client.sdk.callbacks.ICustomCommandListener;
import im.zego.gomovie.client.sdk.callbacks.IRTCCommonCallback;
import im.zego.gomovie.client.sdk.callbacks.IRemoteDeviceStateListener;
import im.zego.gomovie.client.sdk.callbacks.IRoomExtraInfoUpdateListener;
import im.zego.gomovie.client.sdk.callbacks.IRoomUserListener;
import im.zego.gomovie.client.sdk.callbacks.IStreamCountListener;
import im.zego.gomovie.client.sdk.callbacks.IZegoIMSendMessageCallback;
import im.zego.gomovie.client.sdk.callbacks.IZegoRoomConnectionStateListener;
import im.zego.gomovie.client.sdk.model.ZegoPlayStream;
import im.zego.gomovie.client.sdk.model.ZegoRoomUser;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.callback.IZegoEventHandler;
import im.zego.zegoexpress.callback.IZegoIMSendBroadcastMessageCallback;
import im.zego.zegoexpress.callback.IZegoRoomSetRoomExtraInfoCallback;
import im.zego.zegoexpress.constants.ZegoAECMode;
import im.zego.zegoexpress.constants.ZegoANSMode;
import im.zego.zegoexpress.constants.ZegoOrientation;
import im.zego.zegoexpress.constants.ZegoPlayerState;
import im.zego.zegoexpress.constants.ZegoPublisherState;
import im.zego.zegoexpress.constants.ZegoRemoteDeviceState;
import im.zego.zegoexpress.constants.ZegoRoomState;
import im.zego.zegoexpress.constants.ZegoScenario;
import im.zego.zegoexpress.constants.ZegoUpdateType;
import im.zego.zegoexpress.constants.ZegoViewMode;
import im.zego.zegoexpress.entity.ZegoBroadcastMessageInfo;
import im.zego.zegoexpress.entity.ZegoCanvas;
import im.zego.zegoexpress.entity.ZegoEngineConfig;
import im.zego.zegoexpress.entity.ZegoPlayStreamQuality;
import im.zego.zegoexpress.entity.ZegoPublishStreamQuality;
import im.zego.zegoexpress.entity.ZegoRoomConfig;
import im.zego.zegoexpress.entity.ZegoRoomExtraInfo;
import im.zego.zegoexpress.entity.ZegoStream;
import im.zego.zegoexpress.entity.ZegoUser;
import im.zego.zegoexpress.entity.ZegoVideoConfig;

import static im.zego.zegoexpress.constants.ZegoRoomState.CONNECTED;
import static im.zego.zegoexpress.constants.ZegoUpdateType.ADD;
import static im.zego.zegoexpress.constants.ZegoUpdateType.DELETE;

public class ZegoExpressWrapper implements IZegoVideoSDKProxy {

    private String TAG = "ZegoExpressWrapper";

    /**
     * 房间内最大用户数
     */
    public static final int MAX_USER_COUNT = 3;

    private ZegoExpressEngine expressEngine;

    private Map<String, IRTCCommonCallback> publishResultMap = new HashMap<>();
    private Map<String, IRTCCommonCallback> playResultMap = new HashMap<>();
    private IRTCCommonCallback mLoginResult = null;
    private IRoomUserListener mUserListener = null;
    private IRemoteDeviceStateListener mRemoteDeviceStateListener = null;
    private ICustomCommandListener mCustomCommandListener = null;
    private IBroadcastMessageListener mBroadcastMessageListener = null;
    private IRoomExtraInfoUpdateListener mRoomExtraInfoUpdateListener = null;
    private IZegoRoomConnectionStateListener mZegoRoomConnectionStateListener = null;
    private IStreamCountListener mStreamCountListener;

    @Override
    public void initSDK(Application application, long appID, String appSign) {
        ZegoEngineConfig config = new ZegoEngineConfig();

        config.advancedConfig.put("allow_verbose_print_high_frequency_content", "true");
        config.advancedConfig.put("enable_callback_verbose", "true");
        config.advancedConfig.put("use_data_record", "true");
        ZegoExpressEngine.setEngineConfig(config);
        ZegoExpressEngine engine = ZegoExpressEngine.createEngine(
                appID, appSign, false,
                ZegoScenario.COMMUNICATION, application, mEventHandler
        );

        expressEngine = engine;

    }

    @Override
    public void unInitSDK() {
        ZegoExpressEngine.destroyEngine(null);
        expressEngine = null;
    }

    @Override
    public boolean isInit() {
        return expressEngine != null;
    }

    @Override
    public void startPublishing(String streamID, String userName, @NonNull IRTCCommonCallback callback) {
        expressEngine.startPublishingStream(streamID);
        publishResultMap.put(streamID, callback);
    }

    @Override
    public void muteVideoPublish(boolean mute) {
        expressEngine.mutePublishStreamVideo(mute);
    }

    @Override
    public void muteAudioPublish(boolean mute) {
        expressEngine.mutePublishStreamAudio(mute);
    }

    @Override
    public void stopPublishing() {
        expressEngine.stopPublishingStream();
    }

    @Override
    public void activateVideoPlayStream(String streamID, boolean enable) {
        expressEngine.mutePlayStreamVideo(streamID, !enable);
    }

    @Override
    public void activateAudioPlayStream(String streamID, boolean enable) {
        expressEngine.mutePlayStreamAudio(streamID, !enable);
    }

    @Override
    public void startPlayingStream(String streamID, TextureView view, @NonNull IRTCCommonCallback callback) {
        ZegoCanvas zegoCanvas = new ZegoCanvas(view);
        zegoCanvas.viewMode = ZegoViewMode.ASPECT_FILL;
        expressEngine.startPlayingStream(streamID, zegoCanvas);
        playResultMap.put(streamID, callback);
    }

    @Override
    public void updatePlayView(String streamID, TextureView view) {
        expressEngine.startPlayingStream(streamID, new ZegoCanvas(view));
    }

    @Override
    public void stopPlayingStream(String streamID) {
        expressEngine.stopPlayingStream(streamID);
    }

    @Override
    public void enableCamera(boolean enable) {
        expressEngine.enableCamera(enable);
    }

    @Override
    public void setFrontCam(boolean front) {
        expressEngine.useFrontCamera(front);
    }

    @Override
    public void enableMic(boolean enable) {
        expressEngine.muteMicrophone(!enable);
    }

    @Override
    public void enableSpeaker(boolean enable) {
        expressEngine.muteSpeaker(!enable);
    }

    @Override
    public void enableAudio3a(boolean enable) {
        // 开启 AEC 回声消除
        expressEngine.enableAEC(true);
        // 在使用耳机时开启 AEC
        expressEngine.enableHeadphoneAEC(true);
        // 设置 AEC 模式为 MEDIUM
        expressEngine.setAECMode(ZegoAECMode.MEDIUM);

        // 开启 AGC (自动增益）
        expressEngine.enableAGC(true);

        // 开启 ANS (噪声抑制）
        expressEngine.enableANS(true);
        // 开启瞬态噪声抑制
        expressEngine.enableTransientANS(true);
        // 设置 ANS 模式为 MEDIUM
        expressEngine.setANSMode(ZegoANSMode.SOFT);
    }

    @Override
    public void setAppOrientation(int rotation) {
        expressEngine.setAppOrientation(ZegoOrientation.getZegoOrientation(rotation));
    }

    @Override
    public void startPreview(TextureView view) {
        ZegoCanvas zegoCanvas = new ZegoCanvas(view);
        zegoCanvas.viewMode = ZegoViewMode.ASPECT_FILL;
        expressEngine.startPreview(zegoCanvas);
    }

    @Override
    public void stopPreview() {
        expressEngine.stopPreview();
    }

    @Override
    public void setVideoConfig(int width, int height, int bitrate, int fps) {
        ZegoVideoConfig config = new ZegoVideoConfig();
        config.setCaptureResolution(width, height);
        config.setEncodeResolution(width, height);
        config.setVideoBitrate(bitrate / 1000);
        config.setVideoFPS(fps);
        expressEngine.setVideoConfig(config);
    }

    @Override
    public void loginRoom(String userID, String userName, String roomID, IRTCCommonCallback callback) {
        ZegoUser user = new ZegoUser(userID, userName);
        ZegoRoomConfig roomConfig = new ZegoRoomConfig();
        roomConfig.maxMemberCount = MAX_USER_COUNT;
        roomConfig.isUserStatusNotify = true;

        mLoginResult = callback;
        expressEngine.loginRoom(roomID, user, roomConfig);
        mIsLoginRoom = true;
    }

    @Override
    public void logoutRoom(String roomID) {
        expressEngine.logoutRoom(roomID);
        mIsLoginRoom = false;
    }

    @Override
    public void requireHardwareDecoder(boolean require) {
        expressEngine.enableHardwareDecoder(require);
    }

    @Override
    public void requireHardwareEncoder(boolean require) {
        expressEngine.enableHardwareEncoder(require);
    }

    @Override
    public String getVersion() {
        return ZegoExpressEngine.getVersion();
    }

    @Override
    public void uploadLog() {
        expressEngine.uploadLog();
    }

    @Override
    public void sendRoomMsg(ZegoBroadcastMessageInfo messageInfo, String roomID, IZegoIMSendMessageCallback callback) {
        // 发送广播消息，每个登录房间的用户都会通过 onIMRecvBroadcastMessage 回调收到此消息【发送方不会收到该回调】
        expressEngine.sendBroadcastMessage(roomID, messageInfo.message, new IZegoIMSendBroadcastMessageCallback() {
            /** 发送广播消息结果回调处理 */
            @Override
            public void onIMSendBroadcastMessageResult(int errorCode, long messageID) {
                //发送消息结果成功或失败的处理
                if (callback != null) {
                    messageInfo.messageID = messageID;
                    callback.onIMSendMessageResult(errorCode, messageInfo);
                }
            }
        });
    }

    @Override
    public void setRoomExtraInfo(String roomID, String key, String value, IRTCCommonCallback callback) {
        expressEngine.setRoomExtraInfo(roomID, key, value, new IZegoRoomSetRoomExtraInfoCallback() {
            @Override
            public void onRoomSetRoomExtraInfoResult(int code) {
                if (callback != null) {
                    callback.onRTCCallback(code);
                }
            }
        });
    }

    @Override
    public void setRoomUserListener(IRoomUserListener userListener) {
        mUserListener = userListener;
    }

    @Override
    public void setRemoteDeviceEventCallback(IRemoteDeviceStateListener remoteDeviceStateListener) {
        mRemoteDeviceStateListener = remoteDeviceStateListener;
    }

    @Override
    public void setCustomCommandListener(ICustomCommandListener listener) {
        mCustomCommandListener = listener;
    }

    @Override
    public void setBroadcastMessageListener(IBroadcastMessageListener listener) {
        mBroadcastMessageListener = listener;
    }

    @Override
    public void setRoomExtraInfoUpdateListener(IRoomExtraInfoUpdateListener listener) {
        mRoomExtraInfoUpdateListener = listener;
    }

    @Override
    public void setStreamCountListener(IStreamCountListener streamCountListener) {
        this.mStreamCountListener = streamCountListener;
    }


    @Override
    public void setZegoRoomConnectionStateListener(IZegoRoomConnectionStateListener zegoRoomStateListener) {
        this.mZegoRoomConnectionStateListener = zegoRoomStateListener;
    }

    private boolean mIsLoginRoom = false;
    private final IZegoEventHandler mEventHandler = new IZegoEventHandler() {
        @Override
        public void onDebugError(int errorCode, String funcName, String info) {
            super.onDebugError(errorCode, funcName, info);
        }

        @Override
        public void onRoomUserUpdate(
                String roomID, ZegoUpdateType updateType, ArrayList<ZegoUser> userList
        ) {

            if (updateType == ZegoUpdateType.ADD) {
                for (ZegoUser user : userList) {
                    ZegoRoomUser roomUser = new ZegoRoomUser(user.userID, user.userName);
                    if (mUserListener != null) {
                        mUserListener.onUserAdd(roomUser);
                    }

                }
            } else {
                for (ZegoUser user : userList) {
                    ZegoRoomUser roomUser = new ZegoRoomUser(user.userID, user.userName);
                    if (mUserListener != null) {
                        mUserListener.onUserRemove(roomUser);
                    }

                }
            }
        }

        @Override
        public void onPublisherStateUpdate(String streamID, ZegoPublisherState state, int errorCode, JSONObject extendedData) {
            if (state == ZegoPublisherState.PUBLISHING) {

                IRTCCommonCallback streamPublishCallback = publishResultMap.remove(streamID);
                if (streamPublishCallback != null) {
                    streamPublishCallback.onRTCCallback(errorCode);
                }
            }

        }

        @Override
        public void onRoomStreamUpdate(String roomID, ZegoUpdateType updateType, ArrayList<ZegoStream> streamList, JSONObject extendedData) {

            if (expressEngine == null) {
                // ERROR_RTC_SDK_NOT_INITIALIZED
                return;
            }

            for (ZegoStream stream : streamList) {
                if (updateType == ADD) {
                    ZegoPlayStream playStream = new ZegoPlayStream(stream.user.userID, stream.user.userName, stream.streamID);
                    if (mStreamCountListener != null) {
                        mStreamCountListener.onStreamAdd(playStream);
                    }

                } else if (updateType == DELETE) {
                    ZegoPlayStream playStream = new ZegoPlayStream(stream.user.userID, stream.user.userName, stream.streamID);
                    if (mStreamCountListener != null) {
                        mStreamCountListener.onStreamRemove(playStream);
                    }

                }
            }
        }

        @Override
        public void onPlayerStateUpdate(String streamID, ZegoPlayerState state,
                                        int errorCode, JSONObject extendedData) {

            if (state == ZegoPlayerState.PLAYING) {
                IRTCCommonCallback streamPlayCallback = playResultMap.remove(streamID);
                if (streamPlayCallback != null) {
                    streamPlayCallback.onRTCCallback(errorCode);
                }
            }
        }

        @Override
        public void onPublisherQualityUpdate(String streamID, ZegoPublishStreamQuality quality) {
            super.onPublisherQualityUpdate(streamID, quality);
        }

        @Override
        public void onPlayerQualityUpdate(String streamID, ZegoPlayStreamQuality quality) {
            super.onPlayerQualityUpdate(streamID, quality);
        }

        @Override
        public void onRoomExtraInfoUpdate(String roomID, ArrayList<ZegoRoomExtraInfo> roomExtraInfoList) {
            if (roomExtraInfoList == null) {
                return;
            }
            for (ZegoRoomExtraInfo extraInfo : roomExtraInfoList) {
                mRoomExtraInfoUpdateListener.onRoomExtraInfoUpdate(roomID, extraInfo);
            }
        }

        @Override
        public void onIMRecvCustomCommand(String roomID, ZegoUser fromUser, String command) {

            if (command == null || command.length() == 0) {
                return;
            }

            if (mCustomCommandListener != null) {
                mCustomCommandListener.onRecvCustomCommand(
                        fromUser.userID, fromUser.userName,
                        command, roomID);
            }

        }

        @Override
        public void onIMRecvBroadcastMessage(String roomID, ArrayList<ZegoBroadcastMessageInfo> messageList) {

            if (messageList == null) {
                return;
            }

            if (mBroadcastMessageListener != null) {
                mBroadcastMessageListener.onRecvBroadcastMessage(messageList, roomID);
            }

        }


        @Override
        public void onRoomStateUpdate(String roomID, ZegoRoomState state, int errorCode, JSONObject extendedData) {
            super.onRoomStateUpdate(roomID, state, errorCode, extendedData);
            if (state == ZegoRoomState.DISCONNECTED) {
                if (mIsLoginRoom) {
                    if (mLoginResult != null) {
                        mLoginResult.onRTCCallback(errorCode);
                    }
                    mIsLoginRoom = false;
                } else {

                    if (mZegoRoomConnectionStateListener != null) {
                        mZegoRoomConnectionStateListener.onDisconnect(errorCode, roomID);
                    }

                }
            } else if (state == CONNECTED) {
                if (mIsLoginRoom) {
                    if (mLoginResult != null) {
                        mLoginResult.onRTCCallback(errorCode);
                    }
                    mIsLoginRoom = false;
                } else {
                    if (mZegoRoomConnectionStateListener != null) {
                        mZegoRoomConnectionStateListener.onConnected(errorCode, roomID);
                    }
                }

            } else if (state == ZegoRoomState.CONNECTING) {
                if (mZegoRoomConnectionStateListener != null) {
                    mZegoRoomConnectionStateListener.connecting(errorCode, roomID);
                }
            }
        }

        @Override
        public void onRemoteCameraStateUpdate(String streamID, ZegoRemoteDeviceState state) {
            int status;
            if (state == ZegoRemoteDeviceState.OPEN) {
                status = DEVICE_STATUS_OPEN;
            } else {
                status = DEVICE_STATUS_CLOSE;
            }

            if (mRemoteDeviceStateListener != null) {
                mRemoteDeviceStateListener.onRemoteCameraStatusUpdate(streamID, status);
            }

        }

        @Override
        public void onRemoteMicStateUpdate(String streamID, ZegoRemoteDeviceState state) {
            int status;
            if (state == ZegoRemoteDeviceState.OPEN) {
                status = DEVICE_STATUS_OPEN;
            } else {
                status = DEVICE_STATUS_CLOSE;
            }

            if (mRemoteDeviceStateListener != null) {
                mRemoteDeviceStateListener.onRemoteMicStatusUpdate(streamID, status);
            }
        }

        @Override
        public void onPlayerRecvAudioFirstFrame(String streamID) {
            super.onPlayerRecvAudioFirstFrame(streamID);
        }

        @Override
        public void onPlayerRenderVideoFirstFrame(String streamID) {
            super.onPlayerRenderVideoFirstFrame(streamID);
        }
    };

}
