package im.zego.gomovie.client.movieroom;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.util.ArrayList;
import java.util.List;

import im.zego.gomovie.client.Constants;
import im.zego.gomovie.client.model.CommandInfo;
import im.zego.gomovie.client.movielist.JoinMovieRoomActivity;
import im.zego.gomovie.client.R;
import im.zego.gomovie.client.manager.callbacks.IStreamCallback;
import im.zego.gomovie.client.manager.RoomManager;
import im.zego.gomovie.client.manager.UserManager;
import im.zego.gomovie.client.manager.callbacks.INotifyCallback;
import im.zego.gomovie.client.model.UserInfo;
import im.zego.gomovie.client.movieroom.adapter.ChatAdapter;
import im.zego.gomovie.client.sdk.IZegoVideoSDKProxy;
import im.zego.gomovie.client.sdk.ZegoSDKManager;
import im.zego.gomovie.client.sdk.callbacks.IRemoteDeviceStateListener;
import im.zego.gomovie.client.sdk.callbacks.IZegoIMSendMessageCallback;
import im.zego.gomovie.client.sdk.model.ZegoStream;
import im.zego.gomovie.client.utils.GlobalCloseInputHelper;
import im.zego.gomovie.client.utils.ToastUtil;
import im.zego.gomovie.client.view.MoreSettingDialog;
import im.zego.gomovie.client.view.TextureViewLayout;
import im.zego.zegoexpress.entity.ZegoBroadcastMessageInfo;
import im.zego.zegoexpress.entity.ZegoRoomExtraInfo;
import im.zego.zegoexpress.entity.ZegoUser;

public class MovieRoomActivity extends AppCompatActivity implements INotifyCallback, MoreSettingDialog.ISettingMoreCallBack,
        IRemoteDeviceStateListener {

    private ImageView mIvFinish;
    private TextView mTvTitle;
    private ImageView mIvMoreSetting; //更多设置按钮（摄像头翻转、开关摄像头、开关麦克风）
    private TextureView mMovieVideo;  //电影播放的View

    private TextureViewLayout mLeftTextureview; //左边为本人的直播画面（view）
    private TextureViewLayout mRightTextureview; //右边为进房的观众直播画面（view）

    private TextView mTvPlayMovie; //电影播放区域的第一次播放按钮
    private TextView mTvMovieName; //电影昵称
    private TextView mTvPlayPause; //播放中电影的 暂停/播放 按钮

    private RecyclerView mChatList; //聊天信息显示
    private EditText mEditTextMessage; //聊天信息的输入框
    private TextView mTvSendMsg;  //聊天的发送按钮
    private ChatAdapter mChatAdapter; //聊天的 adapter

    private MoreSettingDialog moreSettingDialog; //设置弹窗

    private boolean mFront = true;//是否使用前置摄像头
    private boolean mIsMicMuted = true; //麦克风是否开启
    private boolean mIsCameraMuted = true; //摄像头是否开启

    private String roomId; //房间ID
    private boolean moviePlayStatus = false; //当前电影的播放状态 true：播放 false ：暂停

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_room_activity);
        iniView();
        initData();
        initRecycleView();
        initListener();
        initTimerTask();
    }

    private void iniView() {
        mIvFinish = findViewById(R.id.iv_finish);
        mTvTitle = findViewById(R.id.tv_title);
        mIvMoreSetting = findViewById(R.id.iv_more_setting);
        mMovieVideo = findViewById(R.id.movie_video);
        mChatList = findViewById(R.id.chat_list);
        mEditTextMessage = findViewById(R.id.editTextMessage);
        mTvSendMsg = findViewById(R.id.tv_send_msg);
        mLeftTextureview = findViewById(R.id.left_textureview);
        mRightTextureview = findViewById(R.id.right_textureview);
        mTvPlayMovie = findViewById(R.id.tv_play_movie);
        mTvMovieName = findViewById(R.id.tv_movie_name);
        mTvPlayPause = findViewById(R.id.tv_play_pause);
    }

    private void initData() {

        if (!ZegoSDKManager.getInstance().isInit()) {
            finish();
            return;
        }

        roomId = getIntent().getStringExtra(JoinMovieRoomActivity.JOIN_ROOM_ID);
        mTvTitle.setText(String.format(getString(R.string.movie_room_id), roomId));

        UserInfo userInfo = UserManager.getInstance().getUserInfo();

        ZegoSDKManager.getInstance().getDeviceService().startPreview(mLeftTextureview.getMtvAudienceVideo());
        ZegoSDKManager.getInstance().getDeviceService().setVideoConfig(720, 1280, 1500 * 1000, 15);
        RoomManager.getInstance().startPublishStream(mLeftTextureview.getMtvAudienceVideo(), null);
        mLeftTextureview.leftSetData(this);
        mRightTextureview.rightGone();

        List<ZegoStream> zegoStreamList = ZegoSDKManager.getInstance().getStreamService().getStreamList();
        if (zegoStreamList != null && zegoStreamList.size() > 0) {
            for (ZegoStream zegoStream : zegoStreamList) {
                //根据流信息，显示对应的view
                setZegoStream(zegoStream, userInfo.getUid());
            }
        }

        RoomManager.getInstance().setStreamStatusListener(new IStreamCallback() {
            @Override
            public void onStreamAdd(ZegoStream zegostream) {
                if (zegostream != null) {
                    //根据流信息，显示对应的view
                    setZegoStream(zegostream, userInfo.getUid());
                }
            }

            @Override
            public void onStreamRemove(ZegoStream zegostream) {
                if (zegostream != null) {
                    if (!zegostream.mUserID.equals(String.valueOf(userInfo.getUid()))) {
                        mRightTextureview.rightGone();
                        ZegoSDKManager.getInstance().getStreamService().stopPlayStream(zegostream.mStreamID);
                    }
                }
            }
        });

    }

    /**
     * adapter 的初始化
     */
    private void initRecycleView() {
        mChatAdapter = new ChatAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mChatList.setLayoutManager(linearLayoutManager);
        mChatList.setAdapter(mChatAdapter);
        //解决刷新闪烁问题
        ((SimpleItemAnimator) mChatList.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    private void initListener() {

        mIvFinish.setOnClickListener(v -> finish());
        mIvMoreSetting.setOnClickListener(v -> moreSetting());

        RoomManager.getInstance().setINotifyCallbackListener(this);
        RoomManager.getInstance().setRemoteDeviceEventCallback(this);

        /**
         * 电影播放的初始按钮
         */
        mTvPlayMovie.setOnClickListener(v -> RoomManager.getInstance().setRoomExtraInfo(roomId, Constants.MOVIE_STAR_STATUS));

        /**
         * 电影播放中的 暂停/播放按钮
         */
        mTvPlayPause.setOnClickListener(v ->
                RoomManager.getInstance().setRoomExtraInfo(roomId, moviePlayStatus ? Constants.MOVIE_PAUSE_STATUS : Constants.MOVIE_PLAY_STATUS)
        );

        /**
         * 消息的发送
         */
        mTvSendMsg.setOnClickListener(v -> {
            UserInfo userInfo = UserManager.getInstance().getUserInfo();
            String content = mEditTextMessage.getText().toString();
            if (!TextUtils.isEmpty(content)) {
                //组装消息体
                ZegoBroadcastMessageInfo messageInfo = new ZegoBroadcastMessageInfo();
                ZegoUser fromUser = new ZegoUser(String.valueOf(userInfo.getUid()), userInfo.getNickName());
                messageInfo.message = content;
                messageInfo.fromUser = fromUser;

                //发送
                ZegoSDKManager.getInstance().getRoomService().sendRoomMsg(messageInfo, roomId, new IZegoIMSendMessageCallback() {
                    @Override
                    public void onIMSendMessageResult(int errorCode, ZegoBroadcastMessageInfo messageInfo) {
                        if (errorCode == 0) {
                            mEditTextMessage.setText("");
                            mChatAdapter.addBeanData(messageInfo);
                        } else {
                            ToastUtil.showToast(getString(R.string.movie_msg_send_fail));
                        }
                    }
                });
            }
        });

    }

    /**
     * 1。判断不是本人的流 2.再判断是不是电影流 3.都不是的话那就是另一个观众流（前提条件是：房间内只能有三个流）
     *
     * @param zegoStream 流体
     * @param userId     本人的用户id
     */
    private void setZegoStream(ZegoStream zegoStream, long userId) {
        if (!zegoStream.mUserID.equals(String.valueOf(userId))) {
            // != -1 是表示该字符串包含
            if (zegoStream.mStreamID.indexOf(Constants.MOVIE_STREAM_CONTAIN) != -1) {
                mMovieVideo.setVisibility(View.VISIBLE);
                mTvPlayMovie.setVisibility(View.GONE);
                mTvMovieName.setVisibility(View.VISIBLE);
                mTvPlayPause.setVisibility(View.VISIBLE);
                RoomManager.getInstance().startPlayStream(mMovieVideo, zegoStream, null);
            } else {
                mRightTextureview.setVisibility(View.VISIBLE);
                mRightTextureview.rightSetData(this, zegoStream);
                RoomManager.getInstance().startPlayStream(mRightTextureview.getMtvAudienceVideo(), zegoStream, null);
            }
        }
    }

    /**
     * 本人的 翻转摄像头、开关摄像头、开关麦克风 弹窗
     */
    private void moreSetting() {
        if (moreSettingDialog == null) {
            moreSettingDialog = new MoreSettingDialog();
            moreSettingDialog.setCallBack(this);
        }
        moreSettingDialog.show(getSupportFragmentManager(), moreSettingDialog.getTag());
        moreSettingDialog.setMicAndCamara(mIsMicMuted, mIsCameraMuted);
    }

    /**
     * 翻转摄像头
     */
    @Override
    public void onClickFlip() {
        mFront = !mFront;
        ZegoSDKManager.getInstance().getDeviceService().setFrontCamera(mFront);
    }

    /**
     * 开关摄像头
     */
    @Override
    public void onClickCamera() {
        mIsCameraMuted = !mIsCameraMuted;
        moreSettingDialog.setCamara(mIsCameraMuted);
        ZegoSDKManager.getInstance().getDeviceService().enableCamera(mIsCameraMuted);
        mLeftTextureview.leftSetCamera(this, mIsCameraMuted);

    }

    /**
     * 开关麦克风
     */
    @Override
    public void onClickMic() {
        mIsMicMuted = !mIsMicMuted;
        moreSettingDialog.setMic(mIsMicMuted);
        ZegoSDKManager.getInstance().getDeviceService().enableMic(mIsMicMuted);
        mLeftTextureview.leftSetMIc(mIsMicMuted);
    }

    /**
     * 自定义的房间内信息的更新
     *
     * @param roomID
     * @param roomExtraInfo
     */
    @Override
    public void onRoomExtraInfoUpdate(String roomID, ZegoRoomExtraInfo roomExtraInfo) {
        if (roomExtraInfo.key.equals(Constants.MOVIE_EXTRA_INFO)) {
            if (roomExtraInfo.value.equals(Constants.MOVIE_PLAY_STATUS) || roomExtraInfo.value.equals(Constants.MOVIE_STAR_STATUS)) {
                moviePlayStatus = true;
                mTvPlayPause.setText(getString(R.string.movie_pause));
            } else if (roomExtraInfo.value.equals(Constants.MOVIE_PAUSE_STATUS)) {
                moviePlayStatus = false;
                mTvPlayPause.setText(getString(R.string.movie_play));
            } else if (roomExtraInfo.value.equals(Constants.MOVIE_ROOM_CLOSE)) {
                ToastUtil.showToast(getString(R.string.movie_room_close));
                finish();
            }
        }
    }

    /**
     * 自定义的信令消息
     *
     * @param info
     */
    @Override
    public void onCommandInfo(CommandInfo info) {
        //501 是电影昵称的自定义信令
        if (info.getCmd() == 501) {
            mTvMovieName.setText(info.getContent());
        }
    }

    /**
     * 延迟判断该进入的房间是否存在
     */
    private void initTimerTask() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //判断该房间是否已经先创建了电影播放房间
                if (ZegoSDKManager.getInstance().getRoomService().getmUserList().size() == 0) {
                    onRoomCloseNotify();
                } else {
                    if (!ZegoSDKManager.getInstance().getRoomService().getUserIsService()) {
                        onRoomCloseNotify();
                    }
                }
            }
        }, 1200);
    }

    /**
     * 聊天信息的回掉
     *
     * @param messageList
     */
    @Override
    public void onRecvBroadcastMessage(ArrayList<ZegoBroadcastMessageInfo> messageList) {
        mChatAdapter.addListData(messageList);
    }

    /**
     * 摄像头的状态变更
     *
     * @param streamID
     * @param status
     */
    @Override
    public void onRemoteCameraStatusUpdate(String streamID, int status) {
        mRightTextureview.rightSetCamera(this, status == IZegoVideoSDKProxy.DEVICE_STATUS_OPEN);
    }

    /**
     * 麦克风的状态变更
     *
     * @param streamID
     * @param status
     */
    @Override
    public void onRemoteMicStatusUpdate(String streamID, int status) {
        mRightTextureview.rightSetMIc(status == IZegoVideoSDKProxy.DEVICE_STATUS_OPEN);
    }

    @Override
    public void onRoomCloseNotify() {
        ToastUtil.showToast(getString(R.string.movie_room_no_exist));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RoomManager.getInstance().logoutRoom();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            new GlobalCloseInputHelper.ActivityDispatchTouchEvent().dispatchTouchEventCloseInput(ev, this);
        }
        return super.dispatchTouchEvent(ev);
    }

}
