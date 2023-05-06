package com.dave.astronomer.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.dave.astronomer.common.world.PhysicsSystem;

import java.util.ArrayList;
import java.util.List;

//Note: anything toShape needs to be adjusted with Pixels Per Meter
public class PhysicsUtils {
    private PhysicsUtils() {
    }
    // https://stackoverflow.com/questions/45805732/libgdx-tiled-map-box2d-collision-with-polygon-map-object

    public static PolygonShape toShape(RectangleMapObject rectangleObject) {
        return toShape(rectangleObject.getRectangle());
    }

    public static PolygonShape toShape(Rectangle rectangle) {
        PolygonShape polygon = new PolygonShape();
        Vector2 center = new Vector2((rectangle.x + rectangle.width * 0.5f) / Constants.PIXELS_PER_METER,
            (rectangle.y + rectangle.height * 0.5f) / Constants.PIXELS_PER_METER);

        polygon.setAsBox(rectangle.width * 0.5f / Constants.PIXELS_PER_METER,
            rectangle.height * 0.5f / Constants.PIXELS_PER_METER, center, 0.0f);

        return polygon;
    }

    public static ChainShape toShape(PolylineMapObject polylineObject) {
        float[] vertices = polylineObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] / Constants.PIXELS_PER_METER;
            worldVertices[i].y = vertices[i * 2 + 1] / Constants.PIXELS_PER_METER;
        }

        ChainShape chain = new ChainShape();
        chain.createChain(worldVertices);
        return chain;
    }


    public static CircleShape toShape(CircleMapObject circleObject) {
        Circle circle = circleObject.getCircle();
        return toShape(circle);
    }

    public static CircleShape toShape(Circle circle) {
        CircleShape circleShape = new CircleShape();

        circleShape.setRadius(circle.radius / Constants.PIXELS_PER_METER);
        circleShape.setPosition(new Vector2(circle.x / Constants.PIXELS_PER_METER, circle.y / Constants.PIXELS_PER_METER));

        return circleShape;
    }

    public static PolygonShape toShape(PolygonMapObject polygonObject) {
        return toShape(polygonObject.getPolygon());
    }

    public static PolygonShape toShape(Polygon polygon) {
        PolygonShape shape = new PolygonShape();
        float[] vertices = polygon.getTransformedVertices();

        float[] worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; ++i) {
            worldVertices[i] = vertices[i] / Constants.PIXELS_PER_METER;
        }

        shape.set(worldVertices);
        return shape;
    }



    private static Pixmap getVisiblePixmap(Sprite sprite) {
        Texture texture = sprite.getTexture();
        TextureData data = texture.getTextureData();
        if (!data.isPrepared()) data.prepare();

        //this pixmap may be a texture atlas, so adjust it to only what the sprite sees
        Pixmap fullPixmap = data.consumePixmap();
        Pixmap visible = new Pixmap(sprite.getRegionWidth(), sprite.getRegionHeight(), data.getFormat());

        visible.drawPixmap(fullPixmap, 0, 0, sprite.getRegionX(), sprite.getRegionY(), sprite.getRegionWidth(), sprite.getRegionHeight());

        fullPixmap.dispose();
        return visible;
    }

    public static Rectangle traceRectangle(Sprite sprite) {
        Pixmap pixmap = getVisiblePixmap(sprite);


        int width = pixmap.getWidth();
        int height = pixmap.getHeight();

        // The rectangle is defined by (minX, minY) and (maxX, maxY)
        // The bottom leftmost pixel with a color is at (minX, minY)
        int minX = width;
        int minY = height;
        int maxX = 0;
        int maxY = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int color = pixmap.getPixel(x, y);

                if ((color & 0x000000ff) != 0) { // check if alpha is not 0
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }


        Rectangle rectangle = new Rectangle(sprite.getBoundingRectangle());
        rectangle.setSize((float) maxX - minX, (float) maxY - minY);
        rectangle.setX(minX);
        rectangle.setY(minY);


        pixmap.dispose();

        return rectangle;
    }

    public static Circle traceCircle(Sprite sprite, boolean halfOrigin) {
        Pixmap pixmap = getVisiblePixmap(sprite);


        List<Vector2> coloredPixels = new ArrayList<>();

        // Iterate through the pixels and find colored pixels
        for (int x = 0; x < pixmap.getWidth(); x++) {
            for (int y = 0; y < pixmap.getHeight(); y++) {
                int color = pixmap.getPixel(x, y);
                if ((color & 0x000000ff) != 0) { // check if alpha is not 0
                    coloredPixels.add(new Vector2(x, y));
                }
            }
        }

        // Calculate center of mass
        float xSum = 0;
        float ySum = 0;
        for (Vector2 pixel : coloredPixels) {
            xSum += pixel.x;
            ySum += pixel.y;
        }
        float xCenter = xSum / coloredPixels.size();
        float yCenter = ySum / coloredPixels.size();

        // Calculate radius
        float maxDistance = 0;
        for (Vector2 pixel : coloredPixels) {
            float distance = (float) Math.sqrt(Math.pow(pixel.x - xCenter, 2) + Math.pow(pixel.y - yCenter, 2));
            if (distance > maxDistance) {
                maxDistance = distance;
            }
        }
        float radius = maxDistance;

        if (halfOrigin) {
            yCenter /= 2;
            radius /= 2;
        }

        Circle circle = new Circle();
        circle.setPosition(xCenter, yCenter);
        circle.setRadius(radius);

        pixmap.dispose();

        return circle;
    }

    public static Polygon bevelRectangle(Rectangle rect, float bevelSize) {
        return new Polygon(new float[]{
            rect.x, rect.y + bevelSize,
            rect.x + bevelSize, rect.y,
            rect.x + rect.width - bevelSize, rect.y,
            rect.x + rect.width, rect.y + bevelSize,
            rect.x + rect.width, rect.y + rect.height - bevelSize,
            rect.x + rect.width - bevelSize, rect.y + rect.height,
            rect.x + bevelSize, rect.y + rect.height,
            rect.x, rect.y + rect.height - bevelSize
        });
    }

    public static void centerSprite(Sprite sprite, Body body) {
        Vector2 bodyPos = body.getPosition();
        sprite.setOrigin(bodyPos.x, bodyPos.y);
    }

    /**
     * @param maxSpeed radians per second
     */
    public static float angularVelocityToAngle(Body body, float angleRad, float maxSpeed, float deltaTime) {
        float currentAngleRad = body.getAngle();
        float angleDiffRad = angleRad - currentAngleRad;

        // Calculate the shortest angle difference
        float shortestAngleDiffRad = (angleDiffRad + MathUtils.PI) % MathUtils.PI2 - MathUtils.PI;

        float angularVelocity = shortestAngleDiffRad / deltaTime;

        if (Math.abs(angularVelocity) > maxSpeed) {
            angularVelocity = Math.signum(angularVelocity) * maxSpeed;
        }

        return angularVelocity;
    }

    /**
     * @param maxSpeed meters per second
     */
    public static Vector2 velocityToPosition(Body body, Vector2 targetPosition, float maxSpeed) {
        Vector2 position = body.getPosition();
        Vector2 velocity = body.getLinearVelocity();

        // point in target direction
        Vector2 targetDirection = targetPosition.cpy().sub(position);

        float distance = targetDirection.len();

        // Calculate distance that can be covered in one frame
        float maxDistance = maxSpeed / PhysicsSystem.STEP_FREQUENCY;

        if (distance <= maxDistance) {
            velocity.set(targetDirection);
        }
        else {
            velocity.set(targetDirection.scl(maxSpeed / distance));
        }

        // If passed target position, zero velocity
        if (velocity.dot(targetDirection) < 0) {
            velocity.setZero();
        }
        return velocity;
    }
    public static Vector2 velocityToPosition(Body body, Vector2 targetPosition, float maxSpeed, float arrivalRadius) {
        Vector2 position = body.getPosition();
        if (position.dst(targetPosition) < arrivalRadius) {
            return smoothVelocityToPosition(body, targetPosition, maxSpeed, Gdx.graphics.getDeltaTime());
        } else return velocityToPosition(body, targetPosition, maxSpeed);
    }
    public static Vector2 smoothVelocityToPosition(Body body, Vector2 targetPosition, float maxSpeed, float delta) {
        Vector2 position = body.getPosition();

        //point from current direction to target
        Vector2 targetDirection = targetPosition.cpy().sub(position);


        Vector2 velocity = targetDirection.scl(delta * maxSpeed * PhysicsSystem.STEP_FREQUENCY);
        return velocity;
    }
}
