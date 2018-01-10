package rasterizer;

/**
 * 3-dimensional vector class. Handles common 3-dimensional vector operations.
 */
public class Vector3 {
	// Coordinate floats.
	public float x;
	public float y;
	public float z;

	/**
	 * Constructs a 3-dimensional vector from a x/y/z triplet.
	 * @param x_ The initial x coordinate.
	 * @param y_ The initial y coordinate.
	 * @param z_ The initial z coordinate.
	 */
	public Vector3(float x_, float y_, float z_) {
		// Initialize all members.
		x = x_;
		y = y_;
		z = z_;
	}

	/**
	 * Constructs a 3-dimensional vector from a 2-dimensional
	 * vector and a z coordinate-float.
	 * @param a The 2D vector to initialize x/y with.
	 * @param z_ the z coordinate to initialize the z coordinate with. 
	 */
	public Vector3(Vector2 a, float z_) {
		// Initialize all members.
		x = a.x;
		y = a.y;
		z = z_;
	}

	/**
	 * Constructs a 3-dimensional vector from a 4-dimensional vector.
	 * @param a The 4-dimensional vector to copy x/y/z from.
	 */
	public Vector3(Vector4 a) {
		// Initialize all members.
		x = a.x;
		y = a.y;
		z = a.z;
	}

	/**
	 * Compute the length of the vector.
	 * @return The vector's length.
	 */
	public float length() {
		// Return the length from the formula sqrt(x^2 + y^2 + z^2).
		return
			(float) Math.sqrt(
				Math.pow(x, 2.0f)
				+ Math.pow(y, 2.0f)
				+ Math.pow(z, 2.0f));
	}

	/**
	 * Compute a normalized version of the current vector.
	 * @return The vector divided by its length.
	 */
	public Vector3 normalize() {
		// Compute length.
		float len = length();
		// Return a new vector with each element divided by the length.
		return new Vector3(x / len, y / len, z / len);
	}

	/**
	 * Linearly interpolate between the current vector and another vector
	 * given a float alpha.
	 * @param other The other vector to blend to.
	 * @param a The ratio between this and other.
	 * @return A new vector between this and other based on some factor a.
	 */
	public Vector3 lerp(Vector3 other, float a) {
		float ia = 1.0f - a;
		// Interpolate vector channels.
		return new Vector3(
			ia * x + a * other.x,
			ia * y + a * other.y,
			ia * z + a * other.z);
	}

	/**
	 * Compute the Euclidean distance from the current vector to another vector.
	 * @param other The other vector to compute the distance to.
	 * @return The Euclidean distance from this to other.
	 */
	public float distance(Vector3 other) {
		// Compute the Euclidean distance.
		return (float) Math.sqrt(
			(float) Math.pow(x - other.x, 2.0f)
			+ (float) Math.pow(y - other.y, 2.0f)
			+ (float) Math.pow(z - other.z, 2.0f));
	}

	/**
	 * Compute the product of the current vector and another vector
	 * @param other The other vector to compute the product with.
	 * @return The product of this and other.
	 */
	public Vector3 mult(Vector3 other) {
		// Multiply channels of this with corresponding channels of other.
		return new Vector3(x * other.x, y * other.y, z * other.z);
	}

	/**
	 * Compute the vector dot product between the current vector
	 * and another vector.
	 * @param other The other vector to compute the dot product with.
	 * @return The dot product (cos-angle) between this and other.
	 */
	public float dot(Vector3 other) {
		// Compute the dot product (x1*x2 + y1*y2 + z1*z2).
		return x * other.x + y * other.y + z * other.z;
	}
}