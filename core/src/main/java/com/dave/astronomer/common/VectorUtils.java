package com.dave.astronomer.common;

import com.badlogic.gdx.math.Vector2;

public class VectorUtils {
    private VectorUtils(){}

    /**
     * @param elasticity value between 0-1 of how much to preserve the original vector's magnitude/energy
     */
    public static Vector2 reflectVector(Vector2 vector, Vector2 normal, float elasticity) {
        float dot = vector.dot(normal);
        Vector2 reflection = new Vector2(normal).scl(2 * dot);
        return new Vector2(vector).sub(reflection).scl(elasticity);
    }
}
