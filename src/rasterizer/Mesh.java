package rasterizer;

import rasterizer.Vector2;

/**
 * Handles the state of a mesh in 3-dimensional space.
 */
public class Mesh {
	// Mesh buffers.
	private Vector3[] m_verts;
	private Vector2[] m_coords;
	private int m_triCount;
	private int m_textureID;

	// Transform state.
	Vector3 m_position;
	Vector3 m_rotation;
	Vector3 m_scale;

	/**
	 * Constructs a mesh from vertex/texture coordinate arrays and a texture ID.
	 * @param texID The default texture to sample when rasterizing the mesh.
	 * @param verts The vertex array to rasterize.
	 * @param coords The texture coordinate array for texture mapping. 
	 */
	public Mesh(int texID, Vector3[] verts, Vector2[] coords) {
		// Initialize all members.
		m_position = new Vector3(0.0f, 0.0f, 0.0f);
		m_rotation = new Vector3(0.0f, 0.0f, 0.0f);
		m_scale = new Vector3(1.0f, 1.0f, 1.0f);
		m_triCount = verts.length / 3;
		m_textureID = texID;
		m_verts = verts;
		m_coords = coords;
	}

	/**
	 * Constructs a mesh from a texture ID and mesh resource.
	 * @param texID The default texture to sample when rasterizing the mesh.
	 * @param res The MeshResource to derive the mesh data from.
	 */
	public Mesh(int texID, MeshResource res) {
		// Initialize all members.
		m_position = new Vector3(0.0f, 0.0f, 0.0f);
		m_rotation = new Vector3(0.0f, 0.0f, 0.0f);
		m_scale = new Vector3(1.0f, 1.0f, 1.0f);
		m_textureID = texID;
		m_verts = res.getVerts();
		m_coords = res.getCoords();
		m_triCount = m_verts.length / 3;
	}

	/**
	 * Sets the scale of the mesh.
	 * @param x The x-scale (width) to assign.
	 * @param y The y-scale (height) to assign.
	 * @param z The z-scale (depth) to assign.
	 */
	public void setScale(float x, float y, float z) {
		// Assign new scale.
		m_scale.x = x;
		m_scale.y = y;
		m_scale.z = z;
	}

	/**
	 * Sets the rotation of the mesh.
	 * @param pitch The pitch (x-axis rotation) to assign.
	 * @param yaw The yaw (y-axis rotation) to assign.
	 * @param roll The roll (z-axis rotation) to assign.
	 */
	public void setRotation(float pitch, float yaw, float roll) {
		// Assign new rotation
		m_rotation.x = pitch;
		m_rotation.y = yaw;
		m_rotation.z = roll;
	}

	/**
	 * Sets the position of the mesh.
	 * @param x The x coordinate of the center of the mesh.
	 * @param y The y coordinate of the center of the mesh.
	 * @param z The z coordinate of the center of the mesh.
	 */
	public void setPosition(float x, float y, float z) {
		// Assign new position.
		m_position.x = x;
		m_position.y = y;
		m_position.z = z;
	}

	/**
	 * Get the vertices of the mesh.
	 * @return The mesh verts.
	 */
	public final Vector3[] getVerts() {
		return m_verts;
	}

	/**
	 * Get the transformation matrix of the mesh.
	 * @return The mesh transformation matrix (combined pos/rot/scale).
	 */
	public Matrix4 getTransformMatrix() {
		return Matrix4.transform(m_position, m_rotation, m_scale);
	}

	/**
	 * Get the texture coordinates of the mesh.
	 * @return The mesh texture coordinates.
	 */
	public final Vector2[] getCoords() {
		return m_coords;
	}

	/**
	 * Get the number of triangles (polycount) of the mesh.
	 * @return The polycount of the mesh.
	 */
	public final int getTriCount() {
		return m_triCount;
	}

	/**
	 * Get the current texture ID of the mesh.
	 * @return The texture ID currently assigned to the mesh.
	 */
	public final int getTextureID() {
		return m_textureID;
	}

}