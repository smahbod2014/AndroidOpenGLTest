package com.youtubeapitest;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

public class MyRenderer implements GLSurfaceView.Renderer {

    private static final int BYTES_PER_FLOAT = 4;
    private static final int VERTEX_SIZE = 7;
    private static final int POSITION_SIZE = 3;
    private static final int COLOR_SIZE = 4;

    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private boolean VPDirty = true;
    private Shader shader;
    private List<Entity> entities = new ArrayList<>();

    public MyRenderer() {}

    private void init() {
        /*float[] vertices = {
                -.5f, -.5f, 0,
                .5f, -.5f, 0,
                .5f, .5f, 0,
                -.5f, .5f, 0
        };

        float[] colors = {
                0, 0, 1, 1,
                1, 0, 0, 1,
                0, 1, 0, 1,
                1, 1, 0, 1
        };

        int[] indices = {
                0, 1, 2,
                0, 2, 3
        };

        Mesh square = new Mesh(vertices, colors, indices);*/

        Mesh cube = Mesh.createCube(2.0f);

        //shader = Shader.createDefaultShader();
        shader = new Shader("Shaders/PlainVertexShader.txt", "Shaders/PlainFragmentShader.txt");
        shader.bind();

        Entity e1 = new Entity(cube);
        entities.add(e1);
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

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(0, POSITION_SIZE, GL_FLOAT, false, BYTES_PER_FLOAT * VERTEX_SIZE, 0);
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, BYTES_PER_FLOAT * VERTEX_SIZE, 12);
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

        update(0.016667f);

        if (VPDirty) {
            float[] VPMatrix = new float[16];
            Matrix.multiplyMM(VPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
            shader.setUniformMatrix("u_VPMatrix", VPMatrix);
            VPDirty = false;
        }

        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            shader.setUniformMatrix("u_ModelMatrix", e.transformation);
            glBindBuffer(GL_ARRAY_BUFFER, e.mesh.vbo);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, e.mesh.ibo);
            glDrawElements(GL_TRIANGLES, e.mesh.numIndices, GL_UNSIGNED_INT, 0);
        }
    }

    private void update(float dt) {
        Entity e = entities.get(0);
        e.rotate(0, 1, 1, 45 * dt);
    }
}
