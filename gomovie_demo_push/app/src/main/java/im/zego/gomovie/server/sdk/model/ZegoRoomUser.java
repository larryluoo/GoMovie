package im.zego.gomovie.server.sdk.model;

import java.util.Objects;

/**
 *
 * 摄像头，麦克风状态等等，都是属于流的属性
 * 如果要查询一个用户的流的状态，需要去 streamService 里面去查
 */
public class ZegoRoomUser {

    private String mUserID;
    private String mUserName;

    public ZegoRoomUser(String userID,String userName){
        mUserID = userID;
        mUserName = userName;
    }

    public String getUserID() {
        return mUserID;
    }

    public void setUserID(String userID) {
        this.mUserID = userID;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        this.mUserName = userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZegoRoomUser roomUser = (ZegoRoomUser) o;
        return Objects.equals(mUserID, roomUser.mUserID) &&
                Objects.equals(mUserName, roomUser.mUserName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mUserID, mUserName);
    }
}
