package im.zego.gomovie.server.sdk.callbacks;

public interface IPublisherQualityUpdateCallback {
    void onPublisherQualityUpdate(double kbps, double fps, int rtt, double packetLostRate);
}
