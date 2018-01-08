package rasterizer;

class Vector4 {
	public float x;
	public float y;
	public float z;
	public float w;

	public Vector4(float x_, float y_, float z_, float w_) {
		x = x_;
		y = y_;
		z = z_;
		w = w_;
	}

	public Vector4(Vector3 a, float w_) {
		x = a.x;
		y = a.y;
		z = a.z;
		w = w_;
	}

	public Vector4 mult(Vector4 other) {
		return new Vector4(x * other.x, y * other.y, z * other.z, w * other.w);
	} 

	public float dot(Vector4 other) {
		return x * other.x + y * other.y + z * other.z + w * other.w;
	}

	public Vector3 wdivide() {
		return new Vector3(x / w, y / w, z / w);
	}

}