package im.zego.gomovie.client.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import im.zego.gomovie.client.R;
import im.zego.gomovie.client.manager.RoomManager;
import im.zego.gomovie.client.manager.UserManager;
import im.zego.gomovie.client.model.UserInfo;
import im.zego.gomovie.client.sdk.model.ZegoStream;
import im.zego.gomovie.client.utils.GlideLoader;

import static im.zego.gomovie.client.manager.UserManager.MIC_CAMERA_CLOSE;
import static im.zego.gomovie.client.manager.UserManager.MIC_CAMERA_OPEN;

/**
 * 观看电影的观众显示View
 */
public class TextureViewLayout extends ConstraintLayout {

    private TextureView mtvAudienceVideo;
    private ImageView mIvAudienceVideo;
    private TextView mTvTipCam;
    private TextView mTvName;
    private ImageView mIvMicOff;
    private ImageView mIvHead;
    private ConstraintLayout headInfo;
    private TextView mTvApplyTip;
    private ConstraintLayout mClContainer;

    public TextureViewLayout(Context context) {
        super(context);
        init();
    }

    public TextureViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.movie_audience_item, this, true);

        mtvAudienceVideo = findViewById(R.id.tv_audience_video);
        mIvAudienceVideo = findViewById(R.id.iv_audience_video);
        mTvTipCam = findViewById(R.id.tv_tip_cam);
        mTvName = findViewById(R.id.tv_name);
        mIvMicOff = findViewById(R.id.iv_mic_off);
        mIvHead = findViewById(R.id.iv_head_portrait);
        headInfo = findViewById(R.id.head_info);
        mTvApplyTip = findViewById(R.id.tv_apply_tip);
        mClContainer = findViewById(R.id.cl_container);
    }

    public void leftSetData(Context context) {
        UserInfo userInfo = UserManager.getInstance().getUserInfo();
        headInfo.setVisibility(VISIBLE);
        mTvName.setText(userInfo.getNickName());
        if (!((Activity) context).isFinishing()) {
            GlideLoader.displayCircleImageByAvatar(context, userInfo.getAvatar(), mIvHead);
        }
    }

    public void leftSetMIc(boolean mIsMicMuted) {
        UserInfo userInfo = UserManager.getInstance().getUserInfo();
        userInfo.setMic(mIsMicMuted ? MIC_CAMERA_OPEN : MIC_CAMERA_CLOSE);
        mIvMicOff.setVisibility(mIsMicMuted ? GONE : VISIBLE);
    }

    public void leftSetCamera(Context context, boolean mIsCameraMuted) {
        UserInfo userInfo = UserManager.getInstance().getUserInfo();
        userInfo.setCamera(mIsCameraMuted ? MIC_CAMERA_OPEN : MIC_CAMERA_CLOSE);

        if (mIsCameraMuted) {
            mtvAudienceVideo.setVisibility(View.VISIBLE);
            mIvAudienceVideo.setVisibility(View.GONE);
            mTvTipCam.setVisibility(View.GONE);
        } else {
            mIvAudienceVideo.setVisibility(View.VISIBLE);
            mTvTipCam.setVisibility(View.VISIBLE);
            mtvAudienceVideo.setVisibility(View.GONE);
            if (!((Activity) context).isFinishing()) {
                GlideLoader.displayRoundImageByAvatar(context, userInfo.getAvatar(), mIvAudienceVideo);
            }
        }

    }

    public void rightSetData(Context context, ZegoStream zegostream) {
        mTvApplyTip.setVisibility(GONE);
        mClContainer.setVisibility(VISIBLE);
        UserInfo userInfo = new UserInfo(Long.valueOf(zegostream.mUserID), UserManager.getInstance().getAvatar(zegostream.mUserName), zegostream.mUserName, MIC_CAMERA_OPEN, MIC_CAMERA_OPEN);
        userInfo.setStreamId(zegostream.mStreamID);
        RoomManager.getInstance().setAudienceInfo(userInfo);
        headInfo.setVisibility(VISIBLE);
        mTvName.setText(userInfo.getNickName());
        if (!((Activity) context).isFinishing()) {
            GlideLoader.displayCircleImageByAvatar(context, userInfo.getAvatar(), mIvHead);
        }

        rightSetMIc(zegostream.isMicPhoneOpen());
        rightSetCamera(context, zegostream.isCameraOpen());
    }

    public void rightSetMIc(boolean mIsMicMuted) {
        UserInfo userInfo = RoomManager.getInstance().getAudienceInfo();
        userInfo.setMic(mIsMicMuted ? MIC_CAMERA_OPEN : MIC_CAMERA_CLOSE);
        mIvMicOff.setVisibility(mIsMicMuted ? GONE : VISIBLE);
    }

    public void rightSetCamera(Context context, boolean mIsCameraMuted) {
        UserInfo userInfo = RoomManager.getInstance().getAudienceInfo();
        userInfo.setCamera(mIsCameraMuted ? MIC_CAMERA_OPEN : MIC_CAMERA_CLOSE);

        if (mIsCameraMuted) {
            mtvAudienceVideo.setVisibility(View.VISIBLE);
            mIvAudienceVideo.setVisibility(View.GONE);
            mTvTipCam.setVisibility(View.GONE);
        } else {
            mIvAudienceVideo.setVisibility(View.VISIBLE);
            mTvTipCam.setVisibility(View.VISIBLE);
            mtvAudienceVideo.setVisibility(View.GONE);
            if (!((Activity) context).isFinishing()) {
                GlideLoader.displayRoundImageByAvatar(context, userInfo.getAvatar(), mIvAudienceVideo);
            }
        }
    }

    public void rightGone() {
        mTvApplyTip.setVisibility(VISIBLE);
        mClContainer.setVisibility(GONE);
    }

    public TextureView getMtvAudienceVideo() {
        return mtvAudienceVideo;
    }

}
