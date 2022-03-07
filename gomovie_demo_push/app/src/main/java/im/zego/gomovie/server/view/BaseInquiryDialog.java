package im.zego.gomovie.server.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import im.zego.gomovie.server.R;


public class BaseInquiryDialog extends Dialog {

    private TextView mTitle;
    private TextView mContent;
    private TextView mCancelButton;
    private TextView mConfirmButton;

    private boolean mCancelable = false;

    public BaseInquiryDialog(@NonNull Context context) {
        this(context, R.layout.movie_inquiry_dialog, false);
    }

    public BaseInquiryDialog(@NonNull Context context, boolean cancelable) {
        this(context, R.layout.movie_inquiry_dialog, cancelable);
    }

    public BaseInquiryDialog(@NonNull Context context, int layoutId) {
        this(context, R.style.BaseDialogStyle, layoutId, false);
    }

    public BaseInquiryDialog(@NonNull Context context, int layoutId, boolean cancelable) {
        this(context, R.style.BaseDialogStyle, layoutId, cancelable);
    }

    private BaseInquiryDialog(@NonNull Context context, int themeResId, int layoutId) {
        this(context, themeResId, layoutId, false);
    }

    private BaseInquiryDialog(@NonNull Context context, int themeResId, int layoutId, boolean cancelable) {
        super(context, themeResId);
        mCancelable = cancelable;
        init(context, layoutId);
    }

    private void init(Context context, int layoutId) {
        //设置dialog位置
        Window window = getWindow();
        if (window != null) {
            window.setContentView(layoutId);
            mTitle = window.findViewById(R.id.movie_title);
            mContent = window.findViewById(R.id.movie_content);
            mCancelButton = window.findViewById(R.id.movie_cancel);
            mConfirmButton = window.findViewById(R.id.movie_confirm);
        }

        setCancelable(mCancelable);
        setCanceledOnTouchOutside(mCancelable);

        show();
    }

    public void setMsgTitle(String title) {
        mTitle.setText(title);
    }

    public void setMsgContent(String content) {
        mContent.setText(content);
    }

    public void setLeftButtonContent(String content) {
        mCancelButton.setText(content);
    }

    public void setRightButtonContent(String content) {
        mConfirmButton.setText(content);
    }

    public void setSureListener(View.OnClickListener listener) {
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view);
            }
        });
    }

    public void setCancelListener(View.OnClickListener listener) {
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view);
            }
        });
    }


}
