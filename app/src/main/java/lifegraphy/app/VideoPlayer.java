package lifegraphy.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.potyvideo.library.AndExoPlayerView;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class VideoPlayer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        Intent intent= getIntent();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();


        View mVideoLayout;
        UniversalVideoView mVideoView;
        UniversalMediaController mMediaController;

        MaterialCardView playerBack = (MaterialCardView) findViewById(R.id.playerBack);

        mVideoView = (UniversalVideoView) findViewById(R.id.videoView);
        mMediaController = (UniversalMediaController) findViewById(R.id.media_controller);
        mVideoLayout = (FrameLayout) findViewById(R.id.video_layout);
        mVideoView.setMediaController(mMediaController);

        TextView loading_text;
        loading_text = findViewById(R.id.loading_text);
        loading_text.setText("Loading");

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (null != intent) {

            storageReference.child(intent.getStringExtra("url")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    mVideoView.setVideoURI(uri);
                    mVideoView.seekTo( 1 );


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(VideoPlayer.this, "An error occured. Please try again", Toast.LENGTH_SHORT).show();
                    VideoPlayer.super.onBackPressed();

                }
            });

        }else{
            Toast.makeText(this, "An error occured. Please try again", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }

        playerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoPlayer.super.onBackPressed();
            }
        });

        mVideoView.setVideoViewCallback(new UniversalVideoView.VideoViewCallback() {
            @Override
            public void onScaleChange(boolean isFullscreen) {

                if (isFullscreen) {
                    ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    mVideoLayout.setLayoutParams(layoutParams);


                } else {
                    ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    mVideoLayout.setLayoutParams(layoutParams);

                }
            }

            @Override
            public void onPause(MediaPlayer mediaPlayer) { // Video pause

            }

            @Override
            public void onStart(MediaPlayer mediaPlayer) { // Video start/resume to play

            }

            @Override
            public void onBufferingStart(MediaPlayer mediaPlayer) {// steam start loading

            }

            @Override
            public void onBufferingEnd(MediaPlayer mediaPlayer) {// steam end loading

            }




        });

    }
}