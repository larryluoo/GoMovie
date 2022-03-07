package im.zego.gomovie.client.sdk.callbacks;


import java.util.ArrayList;
import im.zego.gomovie.client.model.CommandInfo;
import im.zego.zegoexpress.entity.ZegoBroadcastMessageInfo;
import im.zego.zegoexpress.entity.ZegoRoomExtraInfo;

public interface IRTCEventCallback {

    /**
     * 房间关闭通知
     */
    void onRoomCloseNotify();

    void onRoomExtraInfoUpdate(String roomID, ZegoRoomExtraInfo roomExtraInfo);

    /**
     * 聊天信息
     * @param messageList
     */
    void onRecvBroadcastMessage(ArrayList<ZegoBroadcastMessageInfo> messageList);

    /**
     * 公共信息的传递 该项目主要用来通知 电影名称
     * @param info
     */
    void onCommandInfo(CommandInfo info);


}
