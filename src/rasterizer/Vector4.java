package rasterizer;

/**
 * 4-dimensional vector class. Handles common 4-dimensional vector operations.
 */
class Vector4 {
	// Vector component floats.
	public float x;
	public float y;
	public float z;
	public float w;

	/**
	 * Constructs a vector given an x/y/z/w quartet.
	 * @param x_ The initial x coordinate.
	 * @param y_ The initial y coordinate.
	 * @param z_ The initial z coordinate.
	 * @param w_ The initial w coordinate.
	 */
	public Vector4(float x_, float y_, float z_, float w_) {
		// Initialize all members.
		x = x_;
		y = y_;
		z = z_;
		w = w_;
	}

	/**
	 * Constructs a vector given a 3-dimensional vector and a w component-float.
	 * @param a The 3-dimensional vector to copy x/y/z from.
	 * @param w_ The w component.
	 */
	public Vector4(Vector3 a, float w_) {
		// Initialize all members.
		x = a.x;
		y = a.y;
		z = a.z;
		w = w_;
	}

	/**
	 * Computes the vector-vector product between the current vector
	 * and another vector.
	 * @param other The other vector to multiply with.
	 * @return The product of this and other.
	 */
	public Vector4 mult(Vector4 other) {
		// Multiply components of this with corresponding components of other.
		return new Vector4(x * other.x, y * other.y, z * other.z, w * other.w);
	}

	/**
	 * Computes the dot product between the current vector and another vector.
	 * @param other The other vector to compute the dot product with.
	 * @return The dot product between this and other.
	 */
	public float dot(Vector4 other) {
		// Compute the dot product (x1*x2 + y1*y2 + z1*z2 + w1*w2).
		return x * other.x + y * other.y + z * other.z + w * other.w;
	}

	/**
	 * Divides the  x/y/z components by 
	 * the w component (homogeneous coordinates).
	 * @return A (n-1)-dimensional vector containing the other coordinates of
	 * the vector divided by the y component
	 */
	public Vector3 wdivide() {
		// Divide x/y/z by w.
		return new Vector3(x / w, y / w, z / w);
	}

}