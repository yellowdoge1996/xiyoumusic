package com.xiyoumusic.app.utils.retrofit;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

import com.xiyoumusic.app.R;

public class ProgressDialogHandler extends Handler {

    public static final int SHOW_PROGRESS_DIALOG = 1;
    public static final int DISMISS_PROGRESS_DIALOG = 2;

    private AlertDialogBuilder alertDialogBuilder;

    private Context context;
    private boolean cancelable;
    private ProgressCancelListener mProgressCancelListener;
    private String title;

    public ProgressDialogHandler(Context context, ProgressCancelListener
            mProgressCancelListener, String title, boolean cancelable) {
        super();
        this.context = context;
        this.mProgressCancelListener = mProgressCancelListener;
        this.title = title;
        this.cancelable = cancelable;
    }

    private void initProgressDialog() {
        if (alertDialogBuilder == null) {
            alertDialogBuilder = new AlertDialogBuilder(context);

            alertDialogBuilder.setCancelable(cancelable);
            alertDialogBuilder.setDialog_message(title)
                    .getWanderingCubes(R.color.colorAccent)
                    .setLoadingView()
                    .builder();
            if (cancelable) {
                alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        mProgressCancelListener.onCancelProgress();
                    }
                });
            }

            if (!alertDialogBuilder.isShow()) {
                alertDialogBuilder.show();
            }
        }
    }

    private void dismissProgressDialog() {
        if (alertDialogBuilder != null) {
            alertDialogBuilder.dismissDialog();
            alertDialogBuilder = null;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS_DIALOG:
                initProgressDialog();
                break;
            case DISMISS_PROGRESS_DIALOG:
                dismissProgressDialog();
                break;
        }
    }
}
