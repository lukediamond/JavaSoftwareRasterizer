package rasterizer;

/**
 * 4-dimensional matrix for linear transformations in 3-dimensional space.
 * Handles matrix-matrix and matrix-vector operations, as well as providing
 * various transformation matrices.
 */
public class Matrix4 {
	// matrix columns (4x4 matrix = 4 4-dimensional vectors)
	public Vector4 a;
	public Vector4 b;
	public Vector4 c;
	public Vector4 d;

	/**
	 * Sole constructor for Matrix4, creates a matrix from 4 column vectors.
	 * @param a_ First column.
	 * @param b_ Second column.
	 * @param c_ Third column.
	 * @param d_ Forth column.
	 */
	public Matrix4(Vector4 a_, Vector4 b_, Vector4 c_, Vector4 d_) {
		a = a_;
		b = b_;
		c = c_;
		d = d_;
	}

	/**
	 * Computes the transpose of the matrix.
	 * @return A new matrix representing the transpose of the current matrix.
	 */
	public Matrix4 transpose() {
		// [x][y] -> [y][x] for every element in the matrix
		return new Matrix4(
			new Vector4(a.x, b.x, c.x, d.x),
			new Vector4(a.y, b.y, c.y, d.y),
			new Vector4(a.z, b.z, c.z, d.z),
			new Vector4(a.w, b.w, c.w, d.w));
	}

	/**
	 * Computes a matrix-vector product.
	 * @param other The vector to multiply by
	 * @return The 4-dimensional product of the matrix and the vector.
	 */
	public Vector4 mult(Vector4 other) {
		// Matrix-vector product is the dot product of each column with
		// the vector.
		return new Vector4(
			a.dot(other),
			b.dot(other),
			c.dot(other),
			d.dot(other));
	}

	/**
	 * Computes a matrix-matrix product.
	 * @param other The matrix to multiply with.
	 * @return The product of the multiplication.
	 */
	public Matrix4 mult(Matrix4 other) {
		// Matrix-matrix multiplication works between the columns of the first
		// matrix and the rows of the second, thus the transpose of the other
		// matrix must be taken.
		Matrix4 tother = other.transpose();
		// Compute the dot products of each combination of columns between the
		// two matrices as per matrix-matrix multiplication.
		return new Matrix4(
			new Vector4(
				a.dot(tother.a),
				a.dot(tother.b),
				a.dot(tother.c),
				a.dot(tother.d)),
			new Vector4(
				b.dot(tother.a),
				b.dot(tother.b),
				b.dot(tother.c),
				b.dot(tother.d)),
			new Vector4(
				c.dot(tother.a),
				c.dot(tother.b),
				c.dot(tother.c),
				c.dot(tother.d)),
			new Vector4(
				d.dot(tother.a),
				d.dot(tother.b),
				d.dot(tother.c),
				d.dot(tother.d)));
	}

	/**
	 * Creates a perspective projection matrix.
	 * @param aspect The aspect ratio of the render target.
	 * @param fov The vertical field-of-view of the camera.
	 * @param near The near viewing plane for the frustum.
	 * @param far The far viewing plane for the frustum.
	 * @return The computed perspective projection matrix.
	 */
	public static Matrix4 perspective(
		float aspect,
		float fov,
		float near,
		float far) {
		// Compute the tangent of half of the vertical FOV.
		float tanHFOV = (float) Math.tan(Math.toRadians(fov) / 2.0f);
		// Compute the near/far terms
		float nf0 = -((near + far) / (near - far));
		float nf1 = (2.0f * far * near) / (near - far);
		// Return the perspective matrix (OpenGL layout).
		return new Matrix4(
			new Vector4(1.0f / (aspect * tanHFOV), 0.0f, 0.0f, 0.0f),
			new Vector4(0.0f, 1.0f / tanHFOV, 0.0f, 0.0f),
			new Vector4(0.0f, 0.0f, nf0, nf1),
			new Vector4(0.0f, 0.0f, 1.0f, 0.0f));
	}

	/**
	 * Create a translation matrix.
	 * @param pos The position to use for the translation.
	 * @return The generated translation matrix.
	 */
	public static Matrix4 translation(Vector3 pos) {
		// Return the translation matrix (OpenGL layout).
		return new Matrix4(
			new Vector4(1.0f, 0.0f, 0.0f, pos.x),
			new Vector4(0.0f, 1.0f, 0.0f, pos.y),
			new Vector4(0.0f, 0.0f, 1.0f, pos.z),
			new Vector4(0.0f, 0.0f, 0.0f, 1.0f));
	}

	/**
	 * Create a X-axis rotation matrix.
	 * @param deg The angle of the rotation (in degrees).
	 * @return The generated rotation matrix.
	 */
	public static Matrix4 rotationX(float deg) {
		// Compute the sine/cosine of the angle
		float sdeg = (float) Math.sin(Math.toRadians(deg));
		float cdeg = (float) Math.cos(Math.toRadians(deg));
		// Return the x-axis rotation matrix (OpenGL layout).
		return new Matrix4(
			new Vector4(1.0f, 0.0f, 0.0f, 0.0f),
			new Vector4(0.0f, cdeg, -sdeg, 0.0f),
			new Vector4(0.0f, sdeg, cdeg, 0.0f),
			new Vector4(0.0f, 0.0f, 0.0f, 1.0f));
	}

	/**
	 * Create a Y-axis rotation matrix.
	 * @param deg The angle of the rotation (in degrees)..
	 * @return The generated rotation matrix.
	 */	
	public static Matrix4 rotationY(float deg) {
		// Compute the sine/cosine of the angle.
		float sdeg = (float) Math.sin(Math.toRadians(deg));
		float cdeg = (float) Math.cos(Math.toRadians(deg));
		// Return the y-axis rotation matrix (OpenGL layout).
		return new Matrix4(
			new Vector4(cdeg, 0.0f, sdeg, 0.0f),
			new Vector4(0.0f, 1.0f, 0.0f, 0.0f),
			new Vector4(-sdeg, 0.0f, cdeg, 0.0f),
			new Vector4(0.0f, 0.0f, 0.0f, 1.0f));
	}

	/**
	 * Create a Z-axis rotation matrix.
	 * @param deg The angle of the rotation (in degrees)..
	 * @return The generated rotation matrix.
	 */
	public static Matrix4 rotationZ(float deg) {
		// Compute the sine/cosine of the angle.
		float sdeg = (float) Math.sin(Math.toRadians(deg));
		float cdeg = (float) Math.cos(Math.toRadians(deg));
		// Return the z-axis rotation matrix (OpenGL layout).
		return new Matrix4(
			new Vector4(cdeg, -sdeg, 0.0f, 0.0f),
			new Vector4(sdeg, cdeg, 0.0f, 0.0f),
			new Vector4(0.0f, 0.0f, 1.0f, 0.0f),
			new Vector4(0.0f, 0.0f, 0.0f, 1.0f));
	}

	/**
	 * Create a scale matrix.
	 * @param scale The 3-dimensional scale to use for the transform.
	 * @return The generated scale matrix.
	 */
	public static Matrix4 scale(Vector3 scale) {
		// Return the scale matrix (OpenGL layout).
		return new Matrix4(
			new Vector4(scale.x, 0.0f, 0.0f, 0.0f),
			new Vector4(0.0f, scale.y, 0.0f, 0.0f),
			new Vector4(0.0f, 0.0f, scale.z, 0.0f),
			new Vector4(0.0f, 0.0f, 0.0f, 1.0f));
	}

	/**
	 * Compute a linear combination of position/rotation/scaling matrices.
	 * @param p The position for the translation matrix.
	 * @param r The rotation for the rotation matrix.
	 * @param s The scale for the scaling matrix.
	 * @return The combined transform matrix.
	 */
	public static Matrix4 transform(Vector3 p, Vector3 r, Vector3 s) {
		// Compute the translation matrix.
		Matrix4 translationMatrix = Matrix4.translation(p);
		// Compute the rotation matrix (from X/Y/Z rotation matrices).
		Matrix4 rotationMatrix =
			Matrix4.rotationZ(r.z)
			.mult(Matrix4.rotationY(r.y))
			.mult(Matrix4.rotationX(r.x));
		// Compute the scale matrix.
		Matrix4 scaleMatrix = Matrix4.scale(s);
		// Return the combination of all 3 matrices.
		return translationMatrix.mult(rotationMatrix).mult(scaleMatrix);
	}
}