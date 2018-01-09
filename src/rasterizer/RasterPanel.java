package rasterizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JPanel;
import java.awt.Point;
import java.util.HashMap;
import java.io.IOException;
import java.io.File;

import rasterizer.Vector2;
import rasterizer.Mesh;

public class RasterPanel extends JPanel {
	private BufferedImage m_backBuffer;
	private float[][] m_depthBuffer;
	BufferedImage m_textures[];
	Mesh m_meshes[];
	private int m_meshIndex = 0;
	private int m_screenWidth;
	private int m_screenHeight;
	IUpdateListener m_listener;

	final int m_RESDIVISOR = 8;

	float lerp(float a, float b, float alpha) {
		return (1.0f - alpha) * a + alpha * b;
	}

	Color lerpColor(Color a, Color b, float alpha) {
		return new Color(
		Math.round(lerp((float) a.getRed(), (float) b.getRed(), alpha)),
		Math.round(
			lerp((float) a.getGreen(), (float) b.getGreen(), alpha)),
		Math.round(
			lerp((float) a.getBlue(), (float) b.getBlue(), alpha)));

	}

	float pointDist(Point a, Point b) {
		return (float) Math.sqrt(
			Math.pow(b.x - a.x, 2) +
			Math.pow(b.y - a.y, 2));
	}

	float clamp(float x, float min, float max) {
		if (x > max) return max;
		if (x < min) return min;
		return x;
	}

	Color sampleImage(BufferedImage image, float x, float y) {
		if (image == null) return Color.BLACK;
		int xpos =
			(int) clamp(
				(int) Math.ceil(x * image.getWidth()),
				0, image.getWidth() - 1);
		int ypos =
			(int) clamp(
				(int) Math.ceil(y * image.getHeight()),
				0, image.getHeight() - 1);
		return new Color(image.getRGB(xpos, image.getHeight() - ypos - 1));
	}

	public void addTexture(Integer id, String path) {
		try {
			BufferedImage img = ImageIO.read(new File(path));
			m_textures[id] = img;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void fillTriangle(
		int     id,
		Matrix4 model,
		Matrix4 proj,
		Vector3 va,
		Vector3 vb,
		Vector3 vc,
		Vector2 ta,
		Vector2 tb,
		Vector2 tc) {

		BufferedImage tex = m_textures[id];
		final int ITER = m_screenHeight;
		final float INVERSE_ITER = 1.0f / ITER;

		Matrix4 mp = proj.mult(model);

		for (int i = 0; i < ITER; ++i) {
			float alphaX = (float) i * INVERSE_ITER;
			Vector3 ia = va.lerp(vb, alphaX);
			Vector3 ib = va.lerp(vc, alphaX);
			Vector2 ita = ta.lerp(tb, alphaX);
			Vector2 itb = ta.lerp(tc, alphaX);
			for (int i2 = 0; i2 < ITER; ++i2) {
				float alphaY = (float) i2 * INVERSE_ITER;
				Vector3 ic = ia.lerp(ib, alphaY);
				Vector3 ssc = projectVert(mp, ic);
				Vector2 it = ita.lerp(itb, alphaY);

				Vector3 lightPos = new Vector3(1.0f, 0.0f, 4.0f);
				Vector3 world = model.mult(new Vector4(ic, 1.0f)).wdivide();
				Color color = sampleImage(tex, it.x, it.y);
				float dist = (float) Math.pow(world.distance(lightPos), 2.0f);
				color =
					lerpColor(
						Color.BLACK,
						color,
						clamp((1.0f / (1.0f + dist)), 0.0f, 1.0f));
				int dcoordX =
					(int) clamp(
						(ssc.x + 1.0f) * 0.5f * m_screenWidth,
						0.0f,
						(float) m_screenWidth - 1);
				int dcoordY =
					(int) clamp(
						(ssc.y + 1.0f) * 0.5f * m_screenHeight,
						0.0f,
						(float) m_screenHeight - 1);
				if (ssc.z < m_depthBuffer[dcoordX][dcoordY]) {
					m_depthBuffer[dcoordX][dcoordY] = ssc.z;
					m_backBuffer.setRGB(
						(int) dcoordX,
						(int) m_screenHeight - dcoordY - 1,
						color.getRGB());
				}
			}
		}
	}

	float m_last           = 0.0f;
	float m_elapsed        = 0.0f;
	float m_fpsAccumulator = 0.0f;
	int   m_frames         = 0;

	private Vector3 projectVert(Matrix4 mp, Vector3 vec) {
		Vector4 pos = new Vector4(vec, 1.0f);
		Vector3 mvec = mp.mult(pos).wdivide();
		return new Vector3(mvec.x, mvec.y, mvec.z);
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics ig = m_backBuffer.getGraphics();

		Matrix4 proj =
			Matrix4.perspective(
				(float) m_backBuffer.getWidth()
				/ (float) m_backBuffer.getHeight(),
				45.0f,
				0.01f,
				100.0f);

		for (int i = 0; i < m_meshIndex; ++i) {
			Mesh m = m_meshes[i];
			Vector3[] verts = m.getVerts();
			for (int v = 0; v < m.getTriCount() * 3; v += 3) {
				fillTriangle(
					m.getTextureID(),
					m.getTransformMatrix(),
					proj,
					verts[v + 0],
					verts[v + 1],
					verts[v + 2],
					m.getCoords()[v + 0],
					m.getCoords()[v + 1],
					m.getCoords()[v + 2]);
			}
		}

		g.drawImage(
			m_backBuffer,
			0,
			0,
			m_screenWidth * m_RESDIVISOR,
			m_screenHeight * m_RESDIVISOR,
			null);
		ig.clearRect(0, 0, m_screenWidth, m_screenHeight);
		for (int y = 0; y < m_screenHeight; ++y) {
			for (int x = 0; x < m_screenWidth; ++x) {
				m_depthBuffer[x][y] = 1.0f;
			}
		}

		float now = System.nanoTime() * 1E-9f;
		float delta = now - m_last;
		m_elapsed += delta;
		m_last = now;

		m_fpsAccumulator += delta;
		++m_frames;
		if (m_fpsAccumulator > 1.0f) {
			System.out.println(m_frames);
			m_frames = 0;
			m_fpsAccumulator = 0.0f;
		}

		if (m_listener != null) {
			m_listener.update(delta);
		}
	}

	public void setUpdateListener(IUpdateListener listener) {
		m_listener = listener;

	}

	public void addMesh(Mesh m) {
		m_meshes[m_meshIndex++] = m;
	}

	RasterPanel(int width, int height) {
		m_backBuffer = new BufferedImage(
			width / m_RESDIVISOR,
			height / m_RESDIVISOR,
			BufferedImage.TYPE_INT_RGB);
		m_depthBuffer = new float[width / m_RESDIVISOR][height / m_RESDIVISOR];
		m_textures = new BufferedImage[32];
		m_meshes = new Mesh[32];
		m_screenWidth = width / m_RESDIVISOR;
		m_screenHeight = height / m_RESDIVISOR;
	}

}
