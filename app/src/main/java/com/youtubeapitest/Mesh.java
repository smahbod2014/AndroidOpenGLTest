package com.youtubeapitest;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static android.opengl.GLES20.*;

public class Mesh {

    //public int vao;
    public int vbo;
    public int ibo;
    public int numIndices;

    public Mesh(float[] vertices, float[] colors, int[] indices) {
        float[] interleaved = interleaveArrays(vertices, 3, colors, 4);
        int[] vbotemp = new int[1];
        glGenBuffers(1, vbotemp, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vbotemp[0]);
        glBufferData(GL_ARRAY_BUFFER, 4 * interleaved.length, vertexDataToBuffer(interleaved), GL_STATIC_DRAW);

        int[] ibotemp = new int[1];
        glGenBuffers(1, ibotemp, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibotemp[0]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, 4 * indices.length, indicesToBuffer(indices), GL_STATIC_DRAW);

        vbo = vbotemp[0];
        ibo = ibotemp[0];
        numIndices = indices.length;
    }

    private FloatBuffer vertexDataToBuffer(float[] data) {
        FloatBuffer buffer = ByteBuffer.allocateDirect(data.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        buffer.put(data).position(0);
        return buffer;
    }

    private IntBuffer indicesToBuffer(int[] data) {
        IntBuffer buffer = ByteBuffer.allocateDirect(data.length * 4)
                .order(ByteOrder.nativeOrder()).asIntBuffer();
        buffer.put(data).position(0);
        return buffer;
    }

    private float[] interleaveArrays(float[] a, int numElemsA, float[] b, int numElemsB) {
        float[] result = new float[a.length + b.length];
        int i = 0, j = 0, k = 0;
        while (i < result.length) {
            for (int c = 0; c < numElemsA; c++) {
                result[i++] = a[j + c];
            }

            for (int c = 0; c < numElemsB; c++) {
                result[i++] = b[k + c];
            }

            j += numElemsA;
            k += numElemsB;
        }

        return result;
    }

    public static Mesh createCube(float length) {
        float half = length / 2;
        float[] vertices = {
                //0 bot left
                -half, -half, half,

                //1 bot right
                half, -half, half,

                //2 top left
                -half, half, half,

                //3 top right
                half, half, half,

                //4 back bot left
                -half, -half, -half,

                //5 back bot right
                half, -half, -half,

                //6 back top left
                -half, half, -half,

                //7 back top right
                half, half, -half,
        };

        int[] indices = {
                //front
                0, 1, 3, 0, 1, 2,
                //back
                5, 4, 6, 5, 4, 7,
                //left
                4, 0, 2, 4, 0, 6,
                //right
                1, 5, 6, 1, 5, 3,
                //bottom
                4, 5, 1, 4, 5, 2,
                //top
                2, 3, 7, 2, 3, 6
        };

        float[] colors = {
                1, 0, 0, 1,
                0, 1, 0, 1,
                0, 0, 1, 1,
                1, 1, 0, 1,
                0, 1, 1, 1,
                1, 0, 1, 1,
                1, 1, 1, 1,
                0, 0.5f, 0.5f, 1
        };

        return new Mesh(vertices, colors, indices);
    }
}
