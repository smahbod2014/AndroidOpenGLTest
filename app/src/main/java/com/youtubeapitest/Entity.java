package com.youtubeapitest;

import android.opengl.Matrix;

public class Entity {

    public Mesh mesh;
    public float[] transformation;

    public Entity(Mesh mesh) {
        this.mesh = mesh;
        Matrix.setIdentityM(transformation, 0);
    }

    public void rotate(float x, float y, float z, float degrees) {
        Matrix.rotateM(transformation, 0, degrees, x, y, z);
    }

    public void translate(float x, float y, float z) {
        Matrix.translateM(transformation, 0, x, y, z);
    }

    public void scale(float x, float y, float z) {
        Matrix.scaleM(transformation, 0, x, y, z);
    }
}
