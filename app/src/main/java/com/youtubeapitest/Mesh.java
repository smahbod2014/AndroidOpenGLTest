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
            for (int c = 0; c < numElemsA; c++, j++) {
                result[i++] = a[j + c];
            }

            for (int c = 0; c < numElemsB; c++, k++) {
                result[i++] = b[k + c];
            }
        }

        return result;
    }
}
