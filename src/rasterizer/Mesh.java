package rasterizer;

import rasterizer.Vector2;

public class Mesh {

	private Vector3[] m_verts;
	private Vector2[] m_coords;
	private int       m_triCount;
	private int       m_textureID;

	Vector3 m_position;
	Vector3 m_rotation;
	Vector3 m_scale;

	public Mesh(int texID, Vector3[] verts, Vector2[] coords) {
		m_position = new Vector3(0.0f, 0.0f, 0.0f);
		m_rotation = new Vector3(0.0f, 0.0f, 0.0f);
		m_scale = new Vector3(1.0f, 1.0f, 1.0f);
		m_triCount   = verts.length / 3;
		m_textureID  = texID;
		m_verts      = verts;
		m_coords     = coords;
	}

	public Mesh(int texID, MeshResource res) {
		m_position  = new Vector3(0.0f, 0.0f, 0.0f);
		m_rotation  = new Vector3(0.0f, 0.0f, 0.0f);
		m_scale     = new Vector3(1.0f, 1.0f, 1.0f);
		m_textureID = texID;
		m_verts     = res.getVerts();
		m_coords    = res.getCoords();
		m_triCount  = m_verts.length / 3;
	}

	public void setScale(float x, float y, float z) {
		m_scale.x = x;
		m_scale.y = y;
		m_scale.z = z;
	}

	public void setRotation(float pitch, float yaw, float roll) {
		m_rotation.x = pitch;
		m_rotation.y = yaw;
		m_rotation.z = roll;
	}

	public void setPosition(float x, float y, float z) {
		m_position.x = x;
		m_position.y = y;
		m_position.z = z;
	}

	public final Vector3[] getVerts() {
		return m_verts;
	}

	public Matrix4 getTransformMatrix() {
		return Matrix4.transform(m_position, m_rotation, m_scale);
	}

	public final Vector2[] getCoords() {
		return m_coords;
	}

	public final int getTriCount() {
		return m_triCount;
	}

	public final int getTextureID() {
		return m_textureID;
	}

}