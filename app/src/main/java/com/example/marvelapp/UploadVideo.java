package com.example.marvelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class UploadVideo extends AppCompatActivity {


    private VideoView videoView1;
    private MediaController mediaController;
    private final int CODE_ONE_VIDEO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);
        init();
        setMediaCont();
    }

    public void uploadVideo(View view)
    {

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),CODE_ONE_VIDEO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== CODE_ONE_VIDEO && resultCode == RESULT_OK){
            Uri VideoUri = data.getData();
            if(VideoUri!=null)
            {
                videoView1.setVideoURI(VideoUri);
                videoView1.start();
                videoView1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                    }
                });
            }

        }
    }

    private  void init(){
        this.videoView1 = findViewById(R.id.videoView1);
        this.mediaController = new MediaController(this);
    }

    private void setMediaCont(){
        mediaController.setMediaPlayer(videoView1);
        mediaController.setAnchorView(videoView1);
        videoView1.setMediaController(mediaController);
    }
}