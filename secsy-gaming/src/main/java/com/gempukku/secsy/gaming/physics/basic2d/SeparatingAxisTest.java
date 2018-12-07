package com.gempukku.secsy.gaming.physics.basic2d;

import com.badlogic.gdx.math.Vector2;

public class SeparatingAxisTest {
    public static Vector2 findOverlap(float xMin, float xMax, float yMin, float yMax,
                                      float[] vertices, Vector2 vectorToUse) {
        Axis[] axes = new Axis[2 + vertices.length / 2];
        axes[0] = createAxis(false, xMax - xMin, 0);
        axes[1] = createAxis(false, 0, yMax - yMin);

        for (int i = 0; i < vertices.length - 2; i += 2) {
            axes[2 + i / 2] = createAxis(true,
                    vertices[i + 2] - vertices[i],
                    vertices[i + 3] - vertices[i + 1]);
        }
        axes[axes.length - 1] = createAxis(true,
                vertices[0] - vertices[vertices.length - 2],
                vertices[1] - vertices[vertices.length - 1]);

        float smallestOverlap = Float.MAX_VALUE;

        for (Axis axis : axes) {
            Vector2 projectionAABB = project(axis, xMin, xMax, yMin, yMax);
            Vector2 projectionObstacle = project(axis, vertices);

            // Check for overlap
            float overlap;
            boolean aabbOnTop;
            if (projectionAABB.y > projectionObstacle.y) {
                aabbOnTop = true;
                overlap = projectionObstacle.y - projectionAABB.x;
                if (overlap < 0)
                    return null;
            } else {
                aabbOnTop = false;
                overlap = projectionAABB.y - projectionObstacle.x;
                if (overlap < 0)
                    return null;
            }

            // We only want to know how to move the object based on its own axis,
            // not obstacle's
            if (overlap < smallestOverlap) {
                smallestOverlap = overlap;
                vectorToUse.set(axis.axis).scl(vectorToUse.len() * overlap * (aabbOnTop ? 1 : -1));
                if (Math.abs(vectorToUse.x) > Math.abs(vectorToUse.y))
                    vectorToUse.y = 0;
                else
                    vectorToUse.x = 0;
            }
        }

        return vectorToUse;
    }


    private static Vector2 project(Axis axis, float[] vertices) {
        float dot = axis.axis.dot(vertices[0], vertices[1]);
        Vector2 projection = new Vector2(dot, dot);
        for (int i = 2; i < vertices.length; i += 2)
            applyForVertex(axis, vertices[i], vertices[i + 1], projection);
        return projection;
    }

    private static Vector2 project(Axis axis, float xMin, float xMax, float yMin, float yMax) {
        float dot = axis.axis.dot(xMin, yMin);
        Vector2 projection = new Vector2(dot, dot);

        applyForVertex(axis, xMin, yMax, projection);
        applyForVertex(axis, xMax, yMin, projection);
        applyForVertex(axis, xMax, yMax, projection);

        return new Vector2(projection.x, projection.y);
    }

    private static void applyForVertex(Axis axis, float x, float y, Vector2 projection) {
        float dot;
        dot = axis.axis.dot(x, y);
        projection.x = Math.min(projection.x, dot);
        projection.y = Math.max(projection.y, dot);
    }

    private static Axis createAxis(boolean obstacle, float x, float y) {
        Axis axis = new Axis();
        axis.obstacle = obstacle;
        // Orthogonal to edge
        axis.axis = new Vector2(y, -x).nor();
        return axis;
    }

    private static class Axis {
        private boolean obstacle;
        private Vector2 axis;
    }
}
