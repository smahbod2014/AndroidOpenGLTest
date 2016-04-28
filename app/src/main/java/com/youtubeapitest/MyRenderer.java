package com.youtubeapitest;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

public class MyRenderer implements GLSurfaceView.Renderer {

    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private boolean VPDirty = true;

    public MyRenderer() {

    }

    private void init() {
        float[] vertices = {
                -.5f, -.5f, 0,
                .5f, -.5f, 0,
                .5f, .5f, 0,
                -.5f, .5f, 0
        };

        float[] colors = {
                1, 0, 0, 1,
                0, 1, 0, 1,
                0, 0, 1, 1,
                1, 1, 0, 1
        };

        int[] indices = {
                0, 1, 2,
                0, 2, 3
        };

        Mesh square = new Mesh(vertices, colors, indices);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(.5f, .5f, .5f, 1f);

        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 1.5f;

        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = 0.0f;

        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);


        init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (VPDirty) {
            Matrix.multiplyMM();
            VPDirty = false;
        }
    }
}
