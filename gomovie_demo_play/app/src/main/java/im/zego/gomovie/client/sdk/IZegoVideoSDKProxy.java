package im.zego.gomovie.client.sdk;

import android.app.Application;
import android.view.TextureView;

import androidx.annotation.NonNull;

import im.zego.gomovie.client.sdk.callbacks.IBroadcastMessageListener;
import im.zego.gomovie.client.sdk.callbacks.ICustomCommandListener;
import im.zego.gomovie.client.sdk.callbacks.IRTCCommonCallback;
import im.zego.gomovie.client.sdk.callbacks.IRemoteDeviceStateListener;
import im.zego.gomovie.client.sdk.callbacks.IRoomExtraInfoUpdateListener;
import im.zego.gomovie.client.sdk.callbacks.IRoomUserListener;
import im.zego.gomovie.client.sdk.callbacks.IStreamCountListener;
import im.zego.gomovie.client.sdk.callbacks.IZegoIMSendMessageCallback;
import im.zego.gomovie.client.sdk.callbacks.IZegoRoomConnectionStateListener;
import im.zego.zegoexpress.entity.ZegoBroadcastMessageInfo;

public interface IZegoVideoSDKProxy {

    public static final int DEVICE_STATUS_OPEN = 0;
    public static final int DEVICE_STATUS_CLOSE = 1;

    void initSDK(Application application, long appID, String appSign);

    void unInitSDK();

    boolean isInit();

    /**
     * 开启推流
     */
    void startPublishing(String streamID, String userName, @NonNull IRTCCommonCallback callback);

    /**
     * 停止或恢复发送视频流
     *
     * @param mute 是否停止发送视频流；true 表示不发送视频流；false 表示发送视频流
     */
    void muteVideoPublish(boolean mute);

    /**
     * 停止或恢复发送音频流
     *
     * @param mute 是否停止发送音频流；true 表示不发送音频流；false 表示发送音频流
     */
    void muteAudioPublish(boolean mute);

    /**
     * 停止推流
     */
    void stopPublishing();

    /**
     * 停止或恢复拉取视频流
     *
     * @param enable 禁用标识；true 表示禁止拉取视频流；false 表示恢复拉取视频流
     */
    void activateVideoPlayStream(String streamID, boolean enable);

    /**
     * 停止或恢复拉取音频流
     *
     * @param enable 禁用标识；true 表示禁止拉取音频流；false 表示恢复拉取音频流
     */
    void activateAudioPlayStream(String streamID, boolean enable);

    /**
     * 开始拉流（从 ZEGO RTC 服务器）
     *
     * @param streamID
     * @param view
     */
    void startPlayingStream(String streamID, TextureView view, @NonNull IRTCCommonCallback callback);

    /**
     * 更新拉流（从 ZEGO RTC 服务器）
     *
     * @param streamID
     * @param view
     */
    void updatePlayView(String streamID, TextureView view);

    /**
     * 停止拉流
     *
     * @param streamID
     */
    void stopPlayingStream(String streamID);

    /**
     * 开/关摄像头
     *
     * @param enable 是否打开摄像头；true 表示打开摄像头；false 表示关闭摄像头
     */
    void enableCamera(boolean enable);

    /**
     * 切换前后摄像头
     *
     * @param front 是否采用前置摄像头；true 表示使用前置摄像头；false 表示使用后置摄像头
     */
    void setFrontCam(boolean front);

    /**
     * 是否开启麦克风
     *
     * @param enable 是否开启麦克风；true 表示开启麦克风；false 表示静音（关闭）麦克风
     */
    void enableMic(boolean enable);

    /**
     * 是否开启音频输出
     *
     * @param enable rue 表示开启音频输出；false 表示静音（关闭）音频输出
     */
    void enableSpeaker(boolean enable);

    /**
     *音频 3A 处理
     * @param enable
     */
    void enableAudio3a(boolean enable);

    /**
     * 设置视频的朝向
     * 相比与手机正立的正向，将采集到的数据向逆时针方向分别旋转90，180或270度。旋转后会自动进行调整，以适配编码后的图像分辨率。
     *
     * @param rotation
     */
    void setAppOrientation(int rotation);

    /**
     * 启动/更新本地预览
     *
     * @param view
     */
    void startPreview(TextureView view);

    /**
     * 停止本地预览
     */
    void stopPreview();

    /**
     * 设置视频配置
     *
     * @param width   分辨率的宽
     * @param height  分辨率的高
     * @param bitrate 码率
     * @param fps     帧率
     */
    void setVideoConfig(int width, int height, int bitrate, int fps);

    /**
     * 登录房间，推拉流前必须登录房间
     *
     * @param userID
     * @param userName
     * @param roomID
     * @param callback
     */
    void loginRoom(
            String userID,
            String userName,
            String roomID,
            IRTCCommonCallback callback
    );

    /**
     * 退出房间
     *
     * @param roomID
     */
    void logoutRoom(String roomID);

    /**
     * 开/关硬件解码
     *
     * @param require 是否开启硬解开关；true 表示开启硬解；false 表示关闭硬解
     */
    void requireHardwareDecoder(boolean require);

    /**
     * 开/关硬件编码
     *
     * @param require 是否开启硬件编码；true 表示开启硬编；false 表示关闭硬编
     */
    void requireHardwareEncoder(boolean require);

    /**
     * 设置房间信息
     * @param roomID
     * @param key
     * @param value
     * @param callback
     */
    void setRoomExtraInfo(String roomID, String key, String value, IRTCCommonCallback callback);

    String getVersion();

    void uploadLog();

    /**
     * 发送消息
     * @param messageInfo
     * @param roomID
     * @param callback
     */
    void sendRoomMsg(ZegoBroadcastMessageInfo messageInfo, String roomID, IZegoIMSendMessageCallback callback);

    void setRoomUserListener(IRoomUserListener userListener);

    void setRemoteDeviceEventCallback(IRemoteDeviceStateListener remoteDeviceStateListener);

    void setCustomCommandListener(ICustomCommandListener listener);

    void setBroadcastMessageListener(IBroadcastMessageListener listener);

    void setRoomExtraInfoUpdateListener(IRoomExtraInfoUpdateListener listener);

    void setStreamCountListener(IStreamCountListener streamCountListener);

    void setZegoRoomConnectionStateListener(IZegoRoomConnectionStateListener zegoRoomConnectionStateListener);

}
