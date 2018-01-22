/*
 * Luke Diamond
 * 01/22/2018
 * Grade 11 Final Project
 * Mr. Patterson
 */

package rasterizer;

/**
 * 2-dimensional vector class. Handles 2D linear algebra vector operations.
 */
public class Vector2 {
	// Coordinate floats.
	public float x;
	public float y;

	/**
	 * Constructs a 2-dimensional vector given an x/y pair.
	 * @param x_ The initial x coordinate.
	 * @param y_ The initial y coordinate.
	 */
	public Vector2(float x_, float y_) {
		// Initialize all members.
		x = x_;
		y = y_;
	}

	/**
	 * Constructs a 2-dimensional vector by copying the x/y pair of a 3D vector.
	 * @param other The 3-dimensional vector to copy.
	 */
	public Vector2(Vector3 other) {
		// Initialize all members.
		x = other.x;
		y = other.y;
	}

	/**
	 * Linearly interpolates between the current vector and another vector.
	 * @param other The other vector to interpolate to.
	 * @param alpha The factor with which to blend the vectors.
	 * @return The interpolated vector between this and other.
	 */
	public Vector2 lerp(Vector2 other, float alpha) {
		// Interpolate the x and y components of the two vectors.
		return new Vector2(
			(1.0f - alpha) * x + alpha * other.x,
			(1.0f - alpha) * y + alpha * other.y);
	}

	/**
	 * Compute the Euclidean distance between the current vector and
	 * another vector.
	 * @param other The other vector to compute the distance to.
	 * @return The distance between the current vector and other.
	 */
	public float dist(Vector2 other) {
		// Compute the Euclidean distance.
		return
			(float) Math.sqrt(
				Math.pow(x - other.x, 2.0f) +
				Math.pow(y - other.y, 2.0f));
	}

	/**
	 * Compute the vector dot product between the current vector and
	 * another vector.
	 * @param other The other vector to compute the dot product with.
	 * @return The dot product of this and other.
	 */
	public float dot(Vector2 other) {
		// Return the dot product (x1*x2 + y1*y2).
		return x * other.x + y * other.y;
	}
}
