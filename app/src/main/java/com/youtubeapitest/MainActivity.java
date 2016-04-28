package com.youtubeapitest;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import com.google.android.youtube.player.YouTubePlayerView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        glSurfaceView = new GLSurfaceView(this);

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsES2 = configInfo.reqGlEsVersion >= 0x20000
                || Build.FINGERPRINT.startsWith("generic");

        if (supportsES2) {
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            //glSurfaceView.setRenderer(new LessonOneRenderer());
            glSurfaceView.setRenderer(new MyRenderer());
        }
        else {
            Log.e("OpenGL Test", "We don't support OpenGL ES2");
        }

        setContentView(glSurfaceView);
    }

    @Override
    protected void onResume()
    {
        // The activity must call the GL surface view's onResume() on activity onResume().
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause()
    {
        // The activity must call the GL surface view's onPause() on activity onPause().
        super.onPause();
        glSurfaceView.onPause();
    }
}
