package im.zego.gomovie.server.movieroom;

import android.os.Bundle;
import android.view.TextureView;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import im.zego.gomovie.server.Constants;
import im.zego.gomovie.server.MainActivity;
import im.zego.gomovie.server.manager.MovieMediaPlayer;
import im.zego.gomovie.server.manager.RoomManager;
import im.zego.gomovie.server.manager.callbacks.INotifyCallback;
import im.zego.gomovie.server.model.CommandInfo;
import im.zego.gomovie.server.model.MovieInfo;
import im.zego.gomovie.server.sdk.ZegoSDKManager;
import im.zego.gomovie.server.utils.FileUtils;
import im.zego.gomovie.server.R;
import im.zego.gomovie.server.sdk.model.ZegoStream;
import im.zego.zegoexpress.callback.IZegoIMSendCustomCommandCallback;
import im.zego.zegoexpress.entity.ZegoRoomExtraInfo;
import im.zego.zegoexpress.entity.ZegoUser;

public class MovieRoomActivity extends AppCompatActivity implements INotifyCallback {

    private ImageView mIvFinish;
    private TextView mTvTitle;
    private TextureView mMovieVideo; //电影播放view
    private ConstraintLayout mCreateMovieRoom;

    private ArrayList<String> mMovieFilePaths = new ArrayList<>();
    private List<MovieInfo> mMovieInfo = new ArrayList<>();

    private MovieMediaPlayer mMovieMediaPlayer;
    private int movieId;  //电影id
    private String roomId; //房间id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.movie_room_activity);
        initLocalMovielist();
        iniView();
        initMediaPlayer();
        initListener();
    }

    private void iniView() {

        if (!ZegoSDKManager.getInstance().isInit()) {
            finish();
            return;
        }

        mIvFinish = findViewById(R.id.iv_finish);
        mTvTitle = findViewById(R.id.tv_title);
        mMovieVideo = findViewById(R.id.movie_video);
        mCreateMovieRoom = findViewById(R.id.create_movie_room);

        roomId = getIntent().getStringExtra(MainActivity.CREATE_ROOM_ID);
        movieId = getIntent().getIntExtra(MainActivity.SELECTE_MOVIE_ID, 0);
        mTvTitle.setText(String.format(getString(R.string.movie_room_id), roomId));

        mIvFinish.setOnClickListener(v -> onBackPressed());
    }

    private void initMediaPlayer() {
        ZegoSDKManager.getInstance().getDeviceService().setVideoConfig(1280, 720, 1500 * 1000, 15);
        ZegoSDKManager.getInstance().getDeviceService().startPreview(mMovieVideo);
        mMovieMediaPlayer = MovieMediaPlayer.getInstance();
        mMovieMediaPlayer.setView(mMovieVideo);
        mMovieMediaPlayer.setVideoHandler();
        mCreateMovieRoom.setOnClickListener(v -> {
            if (mMovieFilePaths != null && mMovieFilePaths.size() > 0) {
                RoomManager.getInstance().startPublishStream(mMovieVideo, null);
                String moviePath = mMovieFilePaths.get(movieId);
                mMovieMediaPlayer.load(moviePath, null);
                RoomManager.getInstance().setRoomExtraInfo(roomId, Constants.MOVIE_STAR_STATUS);
            }
        });
    }

    private void initListener() {
        RoomManager.getInstance().setINotifyCallbackListener(this);
    }

    @Override
    public void onUserAdd(ZegoUser zegoUser) {
        ArrayList<ZegoUser> toUserList = new ArrayList<>();
        toUserList.clear();
        toUserList.add(zegoUser);
        CommandInfo commandInfo = new CommandInfo();
        commandInfo.setCmd(501);
        commandInfo.setContent(mMovieInfo.get(movieId).getMovieName());
        Gson gson = new Gson();
        String jsonString = gson.toJson(commandInfo);
        //发送电影标题（有观众进入房间后组装数据后发送电影标题给该观众）
        ZegoSDKManager.getInstance().getRoomService().sendCustomCommand(roomId, jsonString, toUserList, new IZegoIMSendCustomCommandCallback() {
            @Override
            public void onIMSendCustomCommandResult(int code) {

            }
        });
    }

    @Override
    public void onUserRemove(ZegoUser zegoUser) {
        List<ZegoStream> mStreamList = ZegoSDKManager.getInstance().getStreamService().getStreamList();
        if (mStreamList != null && mStreamList.size() == 1) {
            if (mStreamList.get(0).mUserID.equals(String.valueOf(RoomManager.getInstance().getUsserId()))) {
                onBackPressed();
            }
        }
    }

    @Override
    public void onRoomExtraInfoUpdate(String roomID, ZegoRoomExtraInfo roomExtraInfo) {
        if (roomExtraInfo.key.equals(Constants.MOVIE_EXTRA_INFO)) {
            if (roomExtraInfo.value.equals(Constants.MOVIE_STAR_STATUS)) {
                if (mMovieFilePaths != null && mMovieFilePaths.size() > 0) {
                    RoomManager.getInstance().startPublishStream(mMovieVideo, null);
                    String moviePath = mMovieFilePaths.get(movieId);
                    mMovieMediaPlayer.load(moviePath, null);
                }
            } else if (roomExtraInfo.value.equals(Constants.MOVIE_PLAY_STATUS)) {
                MovieMediaPlayer.getInstance().resume();
            } else if (roomExtraInfo.value.equals(Constants.MOVIE_PAUSE_STATUS)) {
                MovieMediaPlayer.getInstance().pause();
            }
        }
    }

    private void initLocalMovielist() {
        createMovieList();
        new Thread(new Runnable() {
            @Override
            public void run() {
                copyAssetsToLocalStorage();
            }
        }).start();
    }

    /**
     * 电影列表
     *
     * @return
     */
    private List<MovieInfo> createMovieList() {
        mMovieInfo.clear();
        String[] movieList = getResources().getStringArray(R.array.multi_movie_list);
        for (int i = 0; i < movieList.length; ++i) {
            String movieName = movieList[i];
            String mFileAssetsPath = movieName + ".mp4";
            mMovieInfo.add(new MovieInfo().setMovieId(i).setMovieName(movieName).setmFileAssetsPath(mFileAssetsPath));
        }
        return mMovieInfo;
    }

    /**
     * 拷贝电影到本地
     */
    private void copyAssetsToLocalStorage() {
        mMovieFilePaths.clear();
        for (MovieInfo movieInfo : mMovieInfo) {
            String path = FileUtils.copyAssetsFile2Phone(this, movieInfo.getmFileAssetsPath());
            mMovieFilePaths.add(path);
        }
    }

    @Override
    public void onBackPressed() {
        RoomManager.getInstance().setRoomExtraInfo(roomId, Constants.MOVIE_ROOM_CLOSE);
    }

    @Override
    public void onRoomCloseNotify() {
        Toast.makeText(MovieRoomActivity.this, "Room has been closed", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RoomManager.getInstance().logoutRoom();
        MovieMediaPlayer.getInstance().destroy();
    }

}
