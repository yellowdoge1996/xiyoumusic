package com.xiyoumusic.app.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.xiyoumusic.app.R;
import com.xiyoumusic.app.service.PlayService;
import com.xiyoumusic.app.utils.retrofit.AlertDialogBuilder;

public class BaseActivity extends AppCompatActivity {

    protected PlayService playService;
    private ServiceConnection serviceConnection;
    private AlertDialogBuilder alertDialogBuilder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        bindService();
    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setClass(this, PlayService.class);
        serviceConnection = new PlayServiceConnection();
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private class PlayServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playService = ((PlayService.PlayBinder) service).getService();
            onServiceBound();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(getClass().getSimpleName(), "service disconnected");
        }
    }

    protected void onServiceBound() {
    }

    public void showProgress(String message) {
        if (alertDialogBuilder == null) {
            alertDialogBuilder = new AlertDialogBuilder(this);
        }
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setDialog_message(message)
                .getWanderingCubes(R.color.colorAccent)
                .setLoadingView()
                .builder();
        if (!alertDialogBuilder.isShow()) {
            alertDialogBuilder.show();
        }
    }

    public void cancelProgress() {
        if (alertDialogBuilder != null && alertDialogBuilder.isShow()) {
            alertDialogBuilder.dismissDialog();
        }
    }

    @Override
    protected void onDestroy() {
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
        super.onDestroy();
    }
}
