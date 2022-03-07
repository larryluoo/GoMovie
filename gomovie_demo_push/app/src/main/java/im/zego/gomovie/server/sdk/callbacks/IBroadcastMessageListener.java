package im.zego.gomovie.server.sdk.callbacks;

public interface IBroadcastMessageListener {
    void onRecvBroadcastMessage(String userID,String userName,String content,String roomID);
}
