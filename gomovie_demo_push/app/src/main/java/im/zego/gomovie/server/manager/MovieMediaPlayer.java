package im.zego.gomovie.server.manager;

import android.view.TextureView;
import java.nio.ByteBuffer;

import im.zego.gomovie.server.sdk.ZegoSDKManager;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.ZegoMediaPlayer;
import im.zego.zegoexpress.callback.IZegoMediaPlayerEventHandler;
import im.zego.zegoexpress.callback.IZegoMediaPlayerLoadResourceCallback;
import im.zego.zegoexpress.callback.IZegoMediaPlayerVideoHandler;
import im.zego.zegoexpress.constants.ZegoMediaPlayerNetworkEvent;
import im.zego.zegoexpress.constants.ZegoMediaPlayerState;
import im.zego.zegoexpress.constants.ZegoVideoFrameFormat;
import im.zego.zegoexpress.constants.ZegoViewMode;
import im.zego.zegoexpress.entity.ZegoCanvas;
import im.zego.zegoexpress.entity.ZegoVideoFrameParam;

public class MovieMediaPlayer extends IZegoMediaPlayerEventHandler {

    private static String TAG = MovieMediaPlayer.class.getSimpleName();
    private ZegoMediaPlayer mZegoMediaPlayer;
    private String mMoviePath;
    private ByteBuffer tempByteBuffer;

    private static volatile MovieMediaPlayer singleton = null;

    public static MovieMediaPlayer getInstance() {
        if (singleton == null) {
            synchronized (MovieMediaPlayer.class) {
                if (singleton == null) {
                    singleton = new MovieMediaPlayer();
                }
            }
        }
        return singleton;
    }

    public MovieMediaPlayer() {
        mZegoMediaPlayer = ZegoExpressEngine.getEngine().createMediaPlayer();
        mZegoMediaPlayer.setProgressInterval(1000);
        mZegoMediaPlayer.setEventHandler(this);
        mZegoMediaPlayer.enableAux(true);
        mZegoMediaPlayer.setVolume(80);
        mZegoMediaPlayer.enableRepeat(true);
    }

    /**
     * 数据采集
     */
    public void setVideoHandler() {
        mZegoMediaPlayer.setVideoHandler(new IZegoMediaPlayerVideoHandler() {
            @Override
            public void onVideoFrame(ZegoMediaPlayer zegoMediaPlayer, ByteBuffer[] byteBuffers, int[] ints, ZegoVideoFrameParam zegoVideoFrameParam) {
                if (RoomManager.getInstance().isCanSenRawData()) {
                    // 将采集的数据传给ZEGO SDK
                    int totalDataLength = byteBuffers[0].capacity();
                    if (tempByteBuffer == null || tempByteBuffer.capacity() != totalDataLength) {
                        tempByteBuffer = ByteBuffer.allocateDirect(byteBuffers[0].capacity()).put(byteBuffers[0]);
                    } else {
                        tempByteBuffer.clear();
                        tempByteBuffer.put(byteBuffers[0]);
                    }
                    ZegoSDKManager.getInstance().getStreamService().sendCustomVideoCaptureRawData(tempByteBuffer, tempByteBuffer.capacity(), zegoVideoFrameParam);
                }
            }
        }, ZegoVideoFrameFormat.RGBA32);
    }

    /**
     * 预加载电影
     *
     * @param path 电影加载路径
     */
    public void load(String path, IZegoMediaPlayerLoadResourceCallback callback) {
        mMoviePath = path;
        mZegoMediaPlayer.loadResource(path, new IZegoMediaPlayerLoadResourceCallback() {
            @Override
            public void onLoadResourceCallback(int code) {
                if (code == 0) {
                    mZegoMediaPlayer.start();
                    if (callback != null) {
                        callback.onLoadResourceCallback(code);
                    }
                }
            }
        });
    }

    /**
     * 停止歌曲播放
     */
    public void stop() {
        mZegoMediaPlayer.stop();
    }

    /**
     * 开始播放
     */
    public void star() {
        mZegoMediaPlayer.start();
    }

    /**
     * 暂停播放
     */
    public void pause() {
        mZegoMediaPlayer.pause();
    }

    /**
     * 恢复播放
     */
    public void resume() {
        mZegoMediaPlayer.resume();
    }

    /**
     * 设置媒体播放器的播放视图
     *
     * @param view 播放视图
     */
    public void setView(TextureView view) {
        ZegoCanvas zegoCanvas = new ZegoCanvas(view);
        zegoCanvas.viewMode = ZegoViewMode.ASPECT_FILL;
        mZegoMediaPlayer.setPlayerCanvas(zegoCanvas);
    }

    public void destroy() {
        if (mZegoMediaPlayer != null) {
            mZegoMediaPlayer.stop();
            mZegoMediaPlayer.setEventHandler(null);
            ZegoExpressEngine.getEngine().destroyMediaPlayer(mZegoMediaPlayer);
            mZegoMediaPlayer = null;
        }
        singleton = null;
    }


    @Override
    public void onMediaPlayerStateUpdate(ZegoMediaPlayer mediaPlayer, ZegoMediaPlayerState state, int errorCode) {
        super.onMediaPlayerStateUpdate(mediaPlayer, state, errorCode);
    }

    @Override
    public void onMediaPlayerNetworkEvent(ZegoMediaPlayer mediaPlayer, ZegoMediaPlayerNetworkEvent networkEvent) {
        super.onMediaPlayerNetworkEvent(mediaPlayer, networkEvent);
    }

    @Override
    public void onMediaPlayerPlayingProgress(ZegoMediaPlayer mediaPlayer, long millisecond) {
        super.onMediaPlayerPlayingProgress(mediaPlayer, millisecond);
    }

    @Override
    public void onMediaPlayerRecvSEI(ZegoMediaPlayer mediaPlayer, byte[] data) {
        super.onMediaPlayerRecvSEI(mediaPlayer, data);
    }
}
