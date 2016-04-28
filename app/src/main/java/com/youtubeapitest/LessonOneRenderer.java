package com.youtubeapitest;

import static android.opengl.GLES20.*;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class LessonOneRenderer implements GLSurfaceView.Renderer {

    private final FloatBuffer triangle1Vertices;
    //private final FloatBuffer triangle2Vertices;
    //private final FloatBuffer triangle3Vertices;
    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] modelMatrix = new float[16];

    private final int bytesPerFloat = 4;

    private int MVPMatrixHandle;
    private int positionHandle;
    private int colorHandle;

    final String vertexShader =
            "uniform mat4 u_MVPMatrix;      \n"     // A constant representing the combined model/view/projection matrix.

                    + "attribute vec4 a_Position;     \n"     // Per-vertex position information we will pass in.
                    + "attribute vec4 a_Color;        \n"     // Per-vertex color information we will pass in.

                    + "varying vec4 v_Color;          \n"     // This will be passed into the fragment shader.

                    + "void main()                    \n"     // The entry point for our vertex shader.
                    + "{                              \n"
                    + "   v_Color = a_Color;          \n"     // Pass the color through to the fragment shader.
                    // It will be interpolated across the triangle.
                    + "   gl_Position = u_MVPMatrix   \n"     // gl_Position is a special variable used to store the final position.
                    + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
                    + "}                              \n";    // normalized screen coordinates.

    final String fragmentShader =
            "precision mediump float;       \n"     // Set the default precision to medium. We don't need as high of a
                    // precision in the fragment shader.
                    + "varying vec4 v_Color;          \n"     // This is the color from the vertex shader interpolated across the
                    // triangle per fragment.
                    + "void main()                    \n"     // The entry point for our fragment shader.
                    + "{                              \n"
                    + "   gl_FragColor = v_Color;     \n"     // Pass the color directly through the pipeline.
                    + "}                              \n";

    public LessonOneRenderer() {
        //X, Y, Z, R, G, B, A
        final float[] triangle1Data = {
                -.5f, -.5f, 0f, 1f, 0f, 0, 1f,

                .5f, -.5f, 0f, 0f, 1f, 0, 1f,

                0f, .5f, 0f, 0f, 0f, 1f, 1f
        };

        triangle1Vertices = ByteBuffer.allocateDirect(triangle1Data.length * bytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        triangle1Vertices.put(triangle1Data).position(0);
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

        int vertexShaderHandle = setupShader(GL_VERTEX_SHADER, vertexShader);
        int fragmentShaderHandle = setupShader(GL_FRAGMENT_SHADER, fragmentShader);

        int programHandle = glCreateProgram();
        if (programHandle != 0) {
            glAttachShader(programHandle, vertexShaderHandle);
            glAttachShader(programHandle, fragmentShaderHandle);
            glBindAttribLocation(programHandle, 0, "a_Position");
            glBindAttribLocation(programHandle, 1, "a_Color");
            glLinkProgram(programHandle);

            final int[] error = new int[1];
            glGetProgramiv(programHandle, GL_LINK_STATUS, error, 0);

            if (error[0] == 0) {
                glDeleteProgram(programHandle);
                programHandle = 0;
                Log.e("OpenGL Test", "Program failed to link");
            }

            glValidateProgram(programHandle);
            glGetProgramiv(programHandle, GL_VALIDATE_STATUS, error, 0);

            if (error[0] == 0) {
                glDeleteProgram(programHandle);
                programHandle = 0;
                Log.e("OpenGL Test", "Program failed to validate");
            }

            glDeleteShader(vertexShaderHandle);
            glDeleteShader(fragmentShaderHandle);
        }

        if (programHandle == 0) {
            throw new RuntimeException("Error creating program.");
        }

        MVPMatrixHandle = glGetUniformLocation(programHandle, "u_MVPMatrix");
        positionHandle = glGetAttribLocation(programHandle, "a_Position");
        colorHandle = glGetAttribLocation(programHandle, "a_Color");

        glUseProgram(programHandle);
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

        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
        drawTriangle(triangle1Vertices);
    }

    private int setupShader(int shaderEnum, String source) {
        int shaderHandle = glCreateShader(shaderEnum);
        if (shaderHandle != 0) {
            glShaderSource(shaderHandle, source);
            glCompileShader(shaderHandle);
            final int[] compileStatus = new int[1];
            glGetShaderiv(shaderHandle, GL_COMPILE_STATUS, compileStatus, 0);

            if (compileStatus[0] == 0) {
                glDeleteShader(shaderHandle);
                shaderHandle = 0;
                if (shaderEnum == GL_VERTEX_SHADER)
                    Log.e("OpenGL Test", "Failed to compile the vertex shader");
                else
                    Log.e("OpenGL Test", "Failed to compile the fragment shader");
            }
        }

        if (shaderHandle == 0)
        {
            if (shaderEnum == GL_VERTEX_SHADER)
                throw new RuntimeException("Error creating vertex shader.");
            else
                throw new RuntimeException("Error creating fragment shader.");
        }

        return shaderHandle;
    }

    private float[] MVPMatrix = new float[16];
    private final int strideBytes = 7 * bytesPerFloat;
    private final int positionOffset = 0;
    private final int positionDataSize = 3;
    private final int colorOffset = 3;
    private final int colorDataSize = 4;

    private void drawTriangle(final FloatBuffer triangleBuffer) {
        glEnableVertexAttribArray(positionHandle);
        glEnableVertexAttribArray(colorHandle);

        triangleBuffer.position(positionOffset);
        glVertexAttribPointer(positionHandle, positionDataSize, GL_FLOAT, false, strideBytes, triangleBuffer);

        triangleBuffer.position(colorOffset);
        glVertexAttribPointer(colorHandle, colorDataSize, GL_FLOAT, false, strideBytes, triangleBuffer);

        Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);

        glUniformMatrix4fv(MVPMatrixHandle, 1, false, MVPMatrix, 0);
        glDrawArrays(GL_TRIANGLES, 0, 3);
    }
}
