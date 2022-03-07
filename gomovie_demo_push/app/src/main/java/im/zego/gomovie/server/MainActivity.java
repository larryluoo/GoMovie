package im.zego.gomovie.server;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import im.zego.gomovie.server.adapter.MovieListAdapter;
import im.zego.gomovie.server.manager.RoomManager;
import im.zego.gomovie.server.model.MovieInfo;
import im.zego.gomovie.server.sdk.ZegoSDKManager;
import im.zego.gomovie.server.utils.PermissionHelper;
import im.zego.gomovie.server.view.BaseInquiryDialog;
import im.zego.gomovie.server.movieroom.MovieRoomActivity;

public class MainActivity extends AppCompatActivity {

    public final static String CREATE_ROOM_ID = "CREATE_ROOM_ID";
    public final static String SELECTE_MOVIE_ID = "SELECTE_MOVIE_ID";

    private ConstraintLayout mCreateMovieRoom;
    private EditText mEtRoomId;

    private RecyclerView mMovieList; // moview list
    private int movieId = 0; //moview ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ZegoSDKManager.getInstance().initSDK(getApplication());
        setContentView(R.layout.activity_main);

        initView();
        initRecycleView();
        initListener();

    }

    private void initView() {
        mCreateMovieRoom = findViewById(R.id.create_movie_room);
        mEtRoomId = findViewById(R.id.et_room_id);
        mMovieList = findViewById(R.id.movie_list);

        ZegoSDKManager.getInstance().getDeviceService().enableCustomVideoCapture();
        RoomManager.getInstance().setCustomVideoCaptureHandler();
    }

    private void initListener() {
        mCreateMovieRoom.setOnClickListener(v -> {
            String roomId = mEtRoomId.getText().toString();
            if (!TextUtils.isEmpty(roomId)) {
                requestPermission(roomId);
            } else {
                Toast.makeText(this, "Please enter room ID", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Determine whether you have permission
     */
    private void requestPermission(String roomId) {
        PermissionHelper.onReadWriteSDCardPermissionGranted(this, allGranted -> {
            if (allGranted) {
                //Permission has been obtained
                // create room
                RoomManager.getInstance().createRoom(roomId, errorCode -> {
                    if (errorCode == 0) {
                        goMovieRoom(roomId);
                    } else if(errorCode == ErrorcodeConstants.ERROR_ROOM_FULL){
                        Toast.makeText(this, "The room already exists, please set another room ID", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Creation failed, please try again", Toast.LENGTH_LONG).show();
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


    private void initRecycleView() {
        MovieListAdapter mMovieListAdapter = new MovieListAdapter(this, createMovieList()).setListener(position -> {
            movieId = position;
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mMovieList.setLayoutManager(linearLayoutManager);
        mMovieList.setAdapter(mMovieListAdapter);
    }

    /**
     * movie list
     *
     * @return
     */
    private List<MovieInfo> createMovieList() {
        List<MovieInfo> list = new ArrayList<>();
        String[] movieList = getResources().getStringArray(R.array.multi_movie_list);
        for (int i = 0; i < movieList.length; ++i) {
            String movieName = movieList[i];
            String mFileAssetsPath = movieName + ".mp4";
            list.add(new MovieInfo().setMovieId(i).setMovieName(movieName).setmFileAssetsPath(mFileAssetsPath).setCheck(i == 0));
        }
        return list;
    }

    /**
     * Jump to play room
     */
    private void goMovieRoom(String roomId) {
        Intent intent = new Intent(this, MovieRoomActivity.class);
        intent.putExtra(CREATE_ROOM_ID, roomId);
        intent.putExtra(SELECTE_MOVIE_ID, movieId);
        startActivity(intent);
    }

}