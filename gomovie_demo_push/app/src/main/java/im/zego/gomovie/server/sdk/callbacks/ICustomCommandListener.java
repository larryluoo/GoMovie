package im.zego.gomovie.server.sdk.callbacks;

public interface ICustomCommandListener {
    void onRecvCustomCommand(String userID,String userName,String content,String roomID);
}
