package im.zego.gomovie.server.sdk.callbacks;

public interface IRoomStateCallback {

    void onPendingEnter();
    void onEntered();
    void onExitRoom();

}
