package im.zego.gomovie.client.sdk.callbacks;

public interface ICustomCommandListener {
    void onRecvCustomCommand(String userID,String userName,String content,String roomID);
}
