package rasterizer;

public class Vector2 {
	public float x;
	public float y;

	public Vector2(float x_, float y_) {
		x = x_;
		y = y_;
	}

	public Vector2(Vector3 other) {
		x = other.x;
		y = other.y;
	}

	public Vector2 lerp(Vector2 other, float alpha) {
		return new Vector2(
			(1.0f - alpha) * x + alpha * other.x,
			(1.0f - alpha) * y + alpha * other.y);
	}

	public float dist(Vector2 other) {
		return
			(float) Math.sqrt(
				Math.pow(x - other.x, 2.0f) +
				Math.pow(y - other.y, 2.0f));
	}

	public Vector2 translate(float ax, float ay) {
		return new Vector2(x + ax, y + ay);
	}

	public Vector2 rotateX(float deg) {
		return
			new Vector2(
				x * (float) Math.cos(Math.toRadians(deg)),
				y);
	}

	public Vector2 rotateY(float deg) {
		return
			new Vector2(
				x,
				y * (float) -Math.sin(Math.toRadians(deg)));
	}

	public Vector2 rotateZ(float deg) {
		float rad = (float) Math.toRadians(deg);
		return
			new Vector2(
				x * (float) Math.cos(rad) + y * (float) -Math.sin(rad),
				x * (float) Math.sin(rad) + y * (float) Math.cos(rad));
	}

	public Vector2 scale(float xs, float ys) {
		return new Vector2(x * xs, y * ys);
	}

	public float dot(Vector2 other) {
		return x * other.x + y * other.y;
	}
}
