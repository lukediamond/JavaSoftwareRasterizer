package rasterizer;

public class Matrix4 {
	public Vector4 a;
	public Vector4 b;
	public Vector4 c;
	public Vector4 d;

	public Matrix4(Vector4 a_, Vector4 b_, Vector4 c_, Vector4 d_) {
		a = a_;
		b = b_;
		c = c_;
		d = d_;
	}

	public Matrix4 transpose() {
		return new Matrix4(
			new Vector4(a.x, b.x, c.x, d.x),
			new Vector4(a.y, b.y, c.y, d.y),
			new Vector4(a.z, b.z, c.z, d.z),
			new Vector4(a.w, b.w, c.w, d.w));
	}

	public Vector4 mult(Vector4 other) {
		return new Vector4(
			a.dot(other),
			b.dot(other),
			c.dot(other),
			d.dot(other));
	}

	public Matrix4 mult(Matrix4 other) {
		Matrix4 tother = other.transpose();
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

	public static Matrix4 perspective(
		float aspect,
		float fov,
		float near,
		float far) {
		float tanHFOV = (float) Math.tan(Math.toRadians(fov) / 2.0f);
		float nf0 = -((near + far) / (near - far));
		float nf1 = (2.0f * far * near) / (near - far);
		return new Matrix4(
			new Vector4(1.0f / (aspect * tanHFOV), 0.0f, 0.0f, 0.0f),
			new Vector4(0.0f, 1.0f / tanHFOV, 0.0f, 0.0f),
			new Vector4(0.0f, 0.0f, nf0, nf1),
			new Vector4(0.0f, 0.0f, 1.0f, 0.0f));
	}

	public static Matrix4 translation(Vector3 pos) {
		return new Matrix4(
			new Vector4(1.0f, 0.0f, 0.0f, pos.x),
			new Vector4(0.0f, 1.0f, 0.0f, pos.y),
			new Vector4(0.0f, 0.0f, 1.0f, pos.z),
			new Vector4(0.0f, 0.0f, 0.0f, 1.0f));
	}

	public static Matrix4 rotationX(float deg) {
		float sdeg = (float) Math.sin(Math.toRadians(deg));
		float cdeg = (float) Math.cos(Math.toRadians(deg));
		return new Matrix4(
			new Vector4(1.0f, 0.0f, 0.0f, 0.0f),
			new Vector4(0.0f, cdeg, -sdeg, 0.0f),
			new Vector4(0.0f, sdeg, cdeg, 0.0f),
			new Vector4(0.0f, 0.0f, 0.0f, 1.0f));
	}

	public static Matrix4 rotationY(float deg) {
		float sdeg = (float) Math.sin(Math.toRadians(deg));
		float cdeg = (float) Math.cos(Math.toRadians(deg));
		return new Matrix4(
			new Vector4(cdeg, 0.0f, sdeg, 0.0f),
			new Vector4(0.0f, 1.0f, 0.0f, 0.0f),
			new Vector4(-sdeg, 0.0f, cdeg, 0.0f),
			new Vector4(0.0f, 0.0f, 0.0f, 1.0f));
	}

	public static Matrix4 rotationZ(float deg) {
		float sdeg = (float) Math.sin(Math.toRadians(deg));
		float cdeg = (float) Math.cos(Math.toRadians(deg));
		return new Matrix4(
			new Vector4(cdeg, -sdeg, 0.0f, 0.0f),
			new Vector4(sdeg, cdeg, 0.0f, 0.0f),
			new Vector4(0.0f, 0.0f, 1.0f, 0.0f),
			new Vector4(0.0f, 0.0f, 0.0f, 1.0f));
	}

	public static Matrix4 scale(Vector3 scale) {
		return new Matrix4(
			new Vector4(scale.x, 0.0f, 0.0f, 0.0f),
			new Vector4(0.0f, scale.y, 0.0f, 0.0f),
			new Vector4(0.0f, 0.0f, scale.z, 0.0f),
			new Vector4(0.0f, 0.0f, 0.0f, 1.0f));
	}

	public static Matrix4 transform(Vector3 p, Vector3 r, Vector3 s) {
		Matrix4 translationMatrix = Matrix4.translation(p);
		Matrix4 rotationMatrix =
			Matrix4.rotationZ(r.z)
			.mult(Matrix4.rotationY(r.y))
			.mult(Matrix4.rotationX(r.x));
		Matrix4 scaleMatrix = Matrix4.scale(s);
		return translationMatrix.mult(rotationMatrix).mult(scaleMatrix);
	}
}