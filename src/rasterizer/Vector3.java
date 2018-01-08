package rasterizer;

public class Vector3 {
	public float x;
	public float y;
	public float z;

	public Vector3(float x_, float y_, float z_) {
		x = x_;
		y = y_;
		z = z_;
	}

	public Vector3(Vector2 a, float z_) {
		x = a.x;
		y = a.y;
		z = z_;
	}

	public Vector3 lerp(Vector3 other, float a) {
		return new Vector3(
			(1.0f - a) * x + a * other.x,
			(1.0f - a) * y + a * other.y,
			(1.0f - a) * z + a * other.z);
	}

	public Vector3 mult(Vector3 other) {
		return new Vector3(x * other.x, y * other.y, z * other.z);
	}

	public float dot(Vector3 other) {
		return x * other.x + y * other.y + z * other.z;
	}
}