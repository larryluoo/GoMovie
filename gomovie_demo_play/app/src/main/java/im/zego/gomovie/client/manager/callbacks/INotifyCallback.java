package im.zego.gomovie.client.manager.callbacks;

import java.util.ArrayList;

import im.zego.gomovie.client.model.CommandInfo;
import im.zego.zegoexpress.entity.ZegoBroadcastMessageInfo;
import im.zego.zegoexpress.entity.ZegoRoomExtraInfo;

public interface INotifyCallback {

    void onRoomCloseNotify();

    void onRoomExtraInfoUpdate(String roomID, ZegoRoomExtraInfo roomExtraInfo);

    void onRecvBroadcastMessage(ArrayList<ZegoBroadcastMessageInfo> messageList);

    void onCommandInfo(CommandInfo info);
}
