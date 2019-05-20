package com.xiyoumusic.app.utils.retrofit;

import android.content.Context;

import com.xiyoumusic.app.utils.ToastUtil;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ProgressObserver<T> implements Observer<T>, ProgressCancelListener {
    private static final String TAG = "ProgressObserver";
    private ObserverOnNextListener listener;
    private ProgressDialogHandler mProgressDialogHandler;
    private Disposable d;
    private boolean showable;

    public ProgressObserver(Context context, ObserverOnNextListener listener, String title,
                            boolean cancelable, boolean showable) {
        this.listener = listener;
        this.showable = showable;
        if (showable) {
            mProgressDialogHandler = new ProgressDialogHandler(context, this, title, cancelable);
        }
    }


    private void showProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG)
                    .sendToTarget();
            mProgressDialogHandler = null;
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
        this.d = d;
        if (showable) {
            showProgressDialog();
        }
    }

    @Override
    public void onNext(T t) {
        listener.onNext(t);
    }

    @Override
    public void onError(Throwable e) {
        if (showable) {
            dismissProgressDialog();
        }
        ToastUtil.error("连接服务器失败");
    }

    @Override
    public void onComplete() {
        if (showable) {
            dismissProgressDialog();
        }
    }

    @Override
    public void onCancelProgress() {
        //如果处于订阅状态，则取消订阅
        if (!d.isDisposed()) {
            d.dispose();
        }
    }
}
