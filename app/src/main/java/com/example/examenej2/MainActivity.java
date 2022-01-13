package com.example.examenej2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private File video;
    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private boolean paused;

    private final ActivityResultLauncher<Intent> activityResultLauncherTakeVideo = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(this, "Video capturado correctamente", Toast.LENGTH_SHORT).show();
                    getWindow().setFormat(PixelFormat.UNKNOWN);
                    surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
                    surfaceHolder = surfaceView.getHolder();
                    surfaceHolder.setFixedSize(800, 480);
                    surfaceHolder.addCallback(this);
                    mediaPlayer = new MediaPlayer();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void pause(View v) {
        if (paused) {
            mediaPlayer.start();
            paused = false;
        } else {
            mediaPlayer.pause();
            paused = true;
        }
    }

    public void grabar(View v) throws IOException {
        File movieDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (!movieDir.exists())
            movieDir.mkdirs();

        video = File.createTempFile(
                "video" + System.currentTimeMillis(),
                ".3gp",
                movieDir
        );

        Uri videoURI = FileProvider.getUriForFile(this,
                "com.example.android.fileprovider",
                video);

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
        activityResultLauncherTakeVideo.launch(takeVideoIntent);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        mediaPlayer.setDisplay(holder);
        try {
            mediaPlayer.setDataSource(video.getAbsolutePath());
            mediaPlayer.prepare();

        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }
}