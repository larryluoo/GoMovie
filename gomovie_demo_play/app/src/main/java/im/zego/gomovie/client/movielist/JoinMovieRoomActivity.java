package im.zego.gomovie.client.movielist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import im.zego.gomovie.client.R;
import im.zego.gomovie.client.manager.RoomManager;
import im.zego.gomovie.client.manager.UserManager;
import im.zego.gomovie.client.movieroom.MovieRoomActivity;
import im.zego.gomovie.client.sdk.ZegoSDKManager;
import im.zego.gomovie.client.utils.PermissionHelper;
import im.zego.gomovie.client.utils.ToastUtil;
import im.zego.gomovie.client.view.BaseInquiryDialog;

import static im.zego.gomovie.client.ErrorcodeConstants.ERROR_ROOM_FULL;

/**
 * 1.输入房间ID（房间ID在手机电影播放端创建电影房时设置）
 * 2.一起看电影要先创建电影房才能进去
 */
public class JoinMovieRoomActivity extends AppCompatActivity {

    public final static String JOIN_ROOM_ID = "JOIN_ROOM_ID";

    private ConstraintLayout mJoinMovieRoom;
    private EditText mEtRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ZegoSDKManager.getInstance().initSDK(getApplication());
        setContentView(R.layout.movie_join_room);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mJoinMovieRoom = findViewById(R.id.join_movie_room);
        mEtRoomId = findViewById(R.id.et_room_id);
    }

    private void initData() {
        UserManager.getInstance().reset();
    }

    private void initListener() {
        mJoinMovieRoom.setOnClickListener(v -> {
            String roomId = mEtRoomId.getText().toString();
            if (!TextUtils.isEmpty(roomId)) {
                requestPermission(roomId);
            } else {
                ToastUtil.showToast(getString(R.string.movie_input_room_id));
            }
        });
    }

    /**
     * 需要请求权限（摄像头、录音）
     *
     * @param roomId
     */
    private void requestPermission(String roomId) {
        PermissionHelper.onCameraAndAudioPermissionGranted(this, allGranted -> {
            if (allGranted) {
                //已经获取了权限
                //登录房间
                RoomManager.getInstance().loginRoom(roomId, errorCode -> {
                    if (errorCode == 0) {
                        goMovieRoom(roomId);
                    } else if (errorCode == ERROR_ROOM_FULL) {
                        //设置了一起看电影的房间最多3人（播放的电影房算一人，所以只能两个人观看）
                        ToastUtil.showToast(getString(R.string.movie_room_full));
                    } else {
                        ToastUtil.showToast(getString(R.string.movie_join_fail));
                    }
                });
            } else {
                BaseInquiryDialog baseInquiryDialog = new BaseInquiryDialog(this);
                baseInquiryDialog.setSureListener(v -> {
                    baseInquiryDialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(Uri.fromParts("package", getPackageName(), null));
                    startActivityForResult(intent, 666);

                });
                baseInquiryDialog.setCancelListener(v -> baseInquiryDialog.dismiss());
            }
        });
    }

    /**
     * 跳转到播放房间
     */
    private void goMovieRoom(String roomId) {
        Intent intent = new Intent(this, MovieRoomActivity.class);
        intent.putExtra(JOIN_ROOM_ID, roomId);
        startActivity(intent);
    }

}
