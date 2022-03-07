package im.zego.gomovie.server.sdk.callbacks;

public interface IRemoteDeviceStateListener {
    void onRemoteCameraStatusUpdate(String streamID,int status);
    void onRemoteMicStatusUpdate(String streamID,int status);
}
