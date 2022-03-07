package im.zego.gomovie.client.sdk.callbacks;

public interface IRoomStateCallback {

    void onPendingEnter();
    void onEntered();
    void onExitRoom();

}
