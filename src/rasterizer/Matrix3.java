package rasterizer;

class Matrix3 {
	public Vector3 a;
	public Vector3 b;
	public Vector3 c;

	public Matrix3(Vector3 a_, Vector3 b_, Vector3 c_) {
		a = a_;
		b = b_;
		c = c_;
	}

	Matrix3 transpose() {
		return new Matrix3(
			new Vector3(a.x, b.x, c.x),
			new Vector3(a.y, b.y, c.y),
			new Vector3(a.z, b.z, c.z));
	}

	Vector3 mult(Vector3 other) {
		return new Vector3(
			a.dot(other),
			b.dot(other),
			c.dot(other));
	}
}