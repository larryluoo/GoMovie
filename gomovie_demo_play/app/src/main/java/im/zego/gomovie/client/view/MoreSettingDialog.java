package im.zego.gomovie.client.view;

import android.os.Bundle;
import android.view.View;

import im.zego.gomovie.client.R;

/**
 * 房主的设置弹窗
 */
public class MoreSettingDialog extends BaseBottomSheetDialogFragment implements View.OnClickListener {

    private MoreSettingView mSettingFlip; //翻转
    private MoreSettingView mSettingCamera; //摄像头
    private MoreSettingView mSettingMic; //麦克风

    private ISettingMoreCallBack callBack;
    private boolean isMicOpen = true; //麦克风是否开启
    private boolean isCamOpen = true; //摄像头是否开启

    @Override
    public int getLayoutResId() {
        return R.layout.movie_dialog_more_setting;
    }

    @Override
    public void initView(Bundle savedInstanceState) {

        mSettingFlip = rootView.findViewById(R.id.setting_flip);
        mSettingCamera = rootView.findViewById(R.id.setting_camera);
        mSettingMic = rootView.findViewById(R.id.setting_mic);

        mSettingFlip.setOnClickListener(this);
        mSettingCamera.setOnClickListener(this);
        mSettingMic.setOnClickListener(this);

        setMic(isMicOpen);
        setCamara(isCamOpen);

    }

    /**
     * 设置麦克风状态
     *
     * @param isMicOpen
     */
    public void setMic(boolean isMicOpen) {
        mSettingMic.setMic(isMicOpen);
    }

    /**
     * 设置摄像头状态
     *
     * @param isCamOpen
     */
    public void setCamara(boolean isCamOpen) {
        mSettingCamera.setCamara(isCamOpen);
    }

    /**
     * 设置数据初始化
     *
     * @param isMicOpen
     * @param isCamOpen
     */
    public void setMicAndCamara(boolean isMicOpen, boolean isCamOpen) {
        this.isCamOpen = isCamOpen;
        this.isMicOpen = isMicOpen;
    }

    @Override
    public void onClick(View v) {
        if (callBack == null) {
            return;
        }
        int id = v.getId();
        if (id == R.id.setting_flip) {
            callBack.onClickFlip();
        } else if (id == R.id.setting_camera) {
            callBack.onClickCamera();
        } else if (id == R.id.setting_mic) {
            callBack.onClickMic();
        }
    }

    public void setCallBack(ISettingMoreCallBack callBack) {
        this.callBack = callBack;
    }

    public interface ISettingMoreCallBack {
        void onClickFlip();

        void onClickCamera();

        void onClickMic();
    }

}
