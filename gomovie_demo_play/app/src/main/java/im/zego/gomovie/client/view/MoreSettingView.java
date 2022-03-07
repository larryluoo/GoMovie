package im.zego.gomovie.client.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import im.zego.gomovie.client.R;

public class MoreSettingView extends ConstraintLayout {

    private ImageView mIvSettingBg;  //图标
    private TextView mTvSettingName; //设置昵称


    public MoreSettingView(@NonNull Context context) {
        this(context, null);
    }

    public MoreSettingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoreSettingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MoreSettingView);
        View view = inflate(context, R.layout.movie_view_moresetting, this);
        String name = typedArray.getString(R.styleable.MoreSettingView_moreSetting_name);
        Drawable bg = typedArray.getDrawable(R.styleable.MoreSettingView_moreSetting_src);
        typedArray.recycle();
        mIvSettingBg = view.findViewById(R.id.iv_setting_bg);
        mTvSettingName = view.findViewById(R.id.tv_setting_name);

        mIvSettingBg.setImageDrawable(bg);
        mTvSettingName.setText(name);

    }

    /**
     * 设置麦克风状态
     *
     * @param isMicOpen
     */
    public void setMic(boolean isMicOpen) {
        if (isMicOpen) {
            mIvSettingBg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.movie_setting_mic_on));
        } else {
            mIvSettingBg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.movie_setting_mic_off));
        }
    }

    /**
     * 设置摄像头状态
     *
     * @param isCamOpen
     */
    public void setCamara(boolean isCamOpen) {
        if (isCamOpen) {
            mIvSettingBg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.movie_setting_cam_on));
        } else {
            mIvSettingBg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.movie_setting_cam_off));
        }
    }

}
