package com.youtubeapitest;

import android.util.Log;

import java.util.HashMap;

import static android.opengl.GLES20.*;

public class Shader {

    private static final String vertexShader =
            "uniform mat4 u_VPMatrix;      \n"     // A constant representing the combined model/view/projection matrix.
        +   "uniform mat4 u_ModelMatrix;      \n"     // A constant representing the combined model/view/projection matrix.

                    + "attribute vec4 a_Position;     \n"     // Per-vertex position information we will pass in.
                    + "attribute vec4 a_Color;        \n"     // Per-vertex color information we will pass in.

                    + "varying vec4 v_Color;          \n"     // This will be passed into the fragment shader.

                    + "void main()                    \n"     // The entry point for our vertex shader.
                    + "{                              \n"
                    + "   v_Color = a_Color;          \n"     // Pass the color through to the fragment shader.
                    // It will be interpolated across the triangle.
                    + "   gl_Position = u_VPMatrix    \n"     // gl_Position is a special variable used to store the final position.
                    + "               * u_ModelMatrix;\n"     // Multiply the vertex by the matrix to get the final point in
                    + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
                    + "}                              \n";    // normalized screen coordinates.

    private static final String fragmentShader =
            "precision mediump float;       \n"     // Set the default precision to medium. We don't need as high of a
                    // precision in the fragment shader.
                    + "varying vec4 v_Color;          \n"     // This is the color from the vertex shader interpolated across the
                    // triangle per fragment.
                    + "void main()                    \n"     // The entry point for our fragment shader.
                    + "{                              \n"
                    + "   gl_FragColor = v_Color;     \n"     // Pass the color directly through the pipeline.
                    + "}                              \n";

    private static int currentProgram;
    private int program;
    private HashMap<String, Integer> uniforms = new HashMap<>()

    public Shader(String vertexSource, String fragmentSource) {
        int vertex = setupShader(GL_VERTEX_SHADER, vertexSource);
        int fragment = setupShader(GL_FRAGMENT_SHADER, fragmentSource);
        program = createProgram(vertex, fragment);
    }

    public static Shader createDefaultShader() {
        return new Shader(vertexShader, fragmentShader);
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

    private int createProgram(int vertexShaderHandle, int fragmentShaderHandle) {
        int programHandle = glCreateProgram();
        if (programHandle == 0) {
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

        return programHandle;
    }

    public void bind() {
        if (currentProgram != program) {
            currentProgram = program;
            glUseProgram(program);
        }
    }
}
