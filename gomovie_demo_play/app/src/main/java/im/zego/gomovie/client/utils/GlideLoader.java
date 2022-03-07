package im.zego.gomovie.client.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import im.zego.gomovie.client.manager.UserManager;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class GlideLoader {

    /**
     * 加载圆图
     *
     * @param resourceId
     * @param imageView
     */
    public static void displayCircleImage(Context context, int resourceId, ImageView imageView) {
        Glide.with(context)
                .load(resourceId)
                .dontAnimate()
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL) // 设置缓存的策略
                .into(imageView);
    }

    /**
     * 加载高斯模糊的背景图
     *
     * @param resourceId
     * @param imageView
     */
    public static void displayRoundImage(Context context, int resourceId, ImageView imageView) {
        Glide.with(context)
                .load(resourceId)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(15, 2)))
                .into(imageView);
    }


    /**
     * 获取头像显示-根据下标获取对应的图片资源
     *
     * @param avatar
     * @param imageView
     */
    public static void displayCircleImageByAvatar(Context context, String avatar, ImageView imageView) {
        try {
            int a = Integer.parseInt(avatar);
            if (a > 0 && a <= UserManager.getInstance().headPortraits.length) {
                displayCircleImage(context, UserManager.getInstance().headPortraits[a - 1], imageView);
            } else {
                displayCircleImage(context, UserManager.getInstance().headPortraits[0], imageView);
            }
        } catch (Exception e) {
            displayCircleImage(context, UserManager.getInstance().headPortraits[0], imageView);
        }
    }

    public static void displayRoundImageByAvatar(Context context, String avatar, ImageView imageView) {
        try {
            int a = Integer.parseInt(avatar);
            if (a > 0 && a <= UserManager.getInstance().backgroundList.length) {
                displayRoundImage(context, UserManager.getInstance().backgroundList[a - 1], imageView);
            } else {
                displayRoundImage(context, UserManager.getInstance().backgroundList[0], imageView);
            }
        } catch (Exception e) {
            displayRoundImage(context, UserManager.getInstance().backgroundList[0], imageView);
        }
    }

}
