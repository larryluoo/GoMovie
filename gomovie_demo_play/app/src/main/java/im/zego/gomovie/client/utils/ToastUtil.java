package im.zego.gomovie.client.utils;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import im.zego.gomovie.client.MovieApplication;
import im.zego.gomovie.client.R;

public class ToastUtil {

    private static final Handler toastHandler = new Handler(Looper.getMainLooper());

    public enum ToastMessageType {
        NORMAL
    }

    public static void showToast(String message) {
        showNormalToast(message);
    }

    public static void showToast(int res) {
        showNormalToast(res);
    }

    public static void showNormalToast(String message) {
        showToast(ToastMessageType.NORMAL, message);
    }

    public static void showNormalToast(int res) {
        showToast(ToastMessageType.NORMAL, MovieApplication.getAppContext().getString(res));
    }

    private static void showToast(ToastMessageType type, String message) {
        if (MovieApplication.isOnBackground() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            toastHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MovieApplication.getAppContext(), message, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            toastHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (type == ToastMessageType.NORMAL) {
                        showColorToast(ToastMessageType.NORMAL, message);
                    }

                }
            });
        }
    }

    private static void showColorToast(ToastMessageType type, String message) {
        View view = LayoutInflater.from(MovieApplication.getAppContext()).inflate(R.layout.movie_view_center_toast, null);
        TextView mTvToastMassage = (TextView) view.findViewById(R.id.tv_toast_message);
        mTvToastMassage.setText(message);

        Toast toast = new Toast(MovieApplication.getAppContext());
        toast.setGravity(Gravity.CENTER | Gravity.FILL_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.setMargin(0, 0);
        toast.show();
    }

}
