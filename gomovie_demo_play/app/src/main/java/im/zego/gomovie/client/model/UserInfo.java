package im.zego.gomovie.client.model;

public class UserInfo {

    private long uid;
    private String nickName;
    private int mic;
    private int camera;
    private String avatar;
    private String streamId;

    public UserInfo(long uid, String avatar, String nickName, int mic, int camera) {
        this.uid = uid;
        this.nickName = nickName;
        this.mic = mic;
        this.camera = camera;
        this.avatar = avatar;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getMic() {
        return mic;
    }

    public void setMic(int mic) {
        this.mic = mic;
    }

    public int getCamera() {
        return camera;
    }

    public void setCamera(int camera) {
        this.camera = camera;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }
}
