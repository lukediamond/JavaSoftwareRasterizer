package rasterizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JPanel;
import java.awt.Point;
import java.util.HashMap;
import java.util.ArrayList;
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

	final int m_RESDIVISOR = 3;

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

		Matrix4 mp = proj.mult(model);

		float alphaX;
		Vector3 ia;
		Vector3 ib;
		Vector2 ita;
		Vector2 itb;

		Vector3 lightPos = new Vector3(0.0f, 1.0f, 2.0f);

		final float ASPECT = (float) m_screenWidth / (float) m_screenHeight;

		final int ITER_X = Math.round(m_screenWidth * ASPECT);
		final int ITER_Y = Math.round(m_screenHeight / ASPECT);
		final float ITER_X_INV = 1.0f / ITER_X;
		final float ITER_Y_INV = 1.0f / ITER_Y;

		for (int i = 0; i < ITER_X; ++i) {
			alphaX = (float) i * ITER_X_INV;
			ia = va.lerp(vb, alphaX);
			ib = va.lerp(vc, alphaX);
			ita = ta.lerp(tb, alphaX);
			itb = ta.lerp(tc, alphaX);

			float alphaY;
			Vector3 ic;
			Vector3 ssc;
			Vector2 it;

			for (int i2 = 0; i2 < ITER_Y; ++i2) {
				alphaY = (float) i2 * ITER_Y_INV;
				ic = ia.lerp(ib, alphaY);
				ssc = mp.mult(new Vector4(ic, 1.0f)).wdivide();
				it = ita.lerp(itb, alphaY);

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

	@Override
	public void paintComponent(Graphics g) {
		Graphics ig = m_backBuffer.getGraphics();

		Matrix4 proj =
			Matrix4.perspective(
				(float) m_backBuffer.getWidth()
				/ (float) m_backBuffer.getHeight(),
				90.0f,
				0.01f,
				100.0f);

		ArrayList<Thread> threadPool = new ArrayList<Thread>();

		for (int i = 0; i < m_meshIndex; ++i) {
			Mesh m = m_meshes[i];
			Vector3[] verts = m.getVerts();

			for (int v = 0; v < m.getTriCount() * 3; v += 3) {
				Vector3 vert0 = m.getVerts()[v + 0];
				Vector3 vert1 = m.getVerts()[v + 1];
				Vector3 vert2 = m.getVerts()[v + 2];
				Vector2 coord0 = m.getCoords()[v + 0];
				Vector2 coord1 = m.getCoords()[v + 1];
				Vector2 coord2 = m.getCoords()[v + 2];
				Thread t = new Thread(() -> {
					fillTriangle(
						m.getTextureID(),
						m.getTransformMatrix(),
						proj,
						vert0,
						vert1,
						vert2,
						coord0,
						coord1,
						coord2);
				});
				t.start();
				threadPool.add(t);
			}
		}

		for (Thread t : threadPool) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
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
		for (int x = 0; x < m_screenWidth; ++x) {
			for (int y = 0; y < m_screenHeight; ++y) {
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
