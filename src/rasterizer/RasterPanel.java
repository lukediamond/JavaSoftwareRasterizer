package rasterizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JPanel;
import java.awt.Point;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.io.IOException;
import java.io.File;

import rasterizer.Vector2;
import rasterizer.Mesh;

/**
 * Raster panel, responsible for all rasterization/drawing to screen.
 */
public class RasterPanel extends JPanel {
	// Back buffers for rendering to/drawing from.
	private volatile BufferedImage m_backBuffer;
	private volatile float[][] m_depthBuffer;

	// Render thread state.
	private Thread[] m_renderThreads;
	private int m_threadCount;
	private volatile ArrayDeque<DrawAction> m_drawQueue;
	private volatile int m_drawCount = 0;

	// Texture array (for sampler textuers).
	BufferedImage m_textures[];
	// Mesh array.
	Mesh m_meshes[];

	// Current mesh index.
	private int m_meshIndex = 0;

	// Screen dimensions.
	private int m_screenWidth;
	private int m_screenHeight;

	// Current update listener.
	IUpdateListener m_listener;

	// Resolution divisor (for low-res upscaling, improves FPS).
	final int m_RESDIVISOR = 2;

	/**
	 * Linearly interpolates between two floats given an alpha value.
	 * @param a The first value.
	 * @param b The second value.
	 * @param alpha The amount to blend the first and second values.
	 * @return Some value where a <= retval <= b given a < b.
	 */
	private float lerp(float a, float b, float alpha) {
		// Linear interpolation equation. Lower alpha means bigger weight
		// on A (alpha * a), 
		// while higher alpha menas bigger weight on B (alpha * b).
		return (1.0f - alpha) * a + alpha * b;
	}

	/**
	 * Linearly interpolates between two colors given an alpha value.
	 * @param a The first color.
	 * @param b The second color.
	 * @param alpha The amount to blend the first and second colors.
	 * @return A new color representing A blended with B.
	 */
	private Color lerpColor(Color a, Color b, float alpha) {
		// Blend colors using lerp method.
		return new Color(
			Math.round(
				lerp((float) a.getRed(), (float) b.getRed(), alpha)),
			Math.round(
				lerp((float) a.getGreen(), (float) b.getGreen(), alpha)),
			Math.round(
				lerp((float) a.getBlue(), (float) b.getBlue(), alpha)));

	}

	/**
	 * Clamps value between range.
	 * @param x The value to clamp.
	 * @param min The minimum value in the range.
	 * @param max The maximum value in the range.
	 * @return The clamped value.
	 */
	private float clamp(float x, float min, float max) {
		// If X is greater than the maximum, return the maximum, if X is lower
		// than the minimum, return the minimum, else return X.
		if (x > max) return max;
		if (x < min) return min;
		return x;
	}

	/**
	 * Sample a texture given X/Y coordinate in range [0, 1].
	 * @param image The image to sample.
	 * @param x The X texture coordinate.
	 * @param y The Y texture coordinate.
	 * @return The color sampled from the image at the X/Y pair.
	 */
	private Color sampleImage(BufferedImage image, float x, float y) {
		// If the image is undefined return pure black.
		if (image == null) return Color.BLACK;
		// X and  positions are rounded, then clamped within the image borders.
		int xpos =
			(int) clamp(
				(int) Math.ceil(x * image.getWidth()),
				0, image.getWidth() - 1);
		int ypos =
			(int) clamp(
				(int) Math.ceil(y * image.getHeight()),
				0, image.getHeight() - 1);
		// Return the color as sampled from the computed coordinates.
		return new Color(image.getRGB(xpos, image.getHeight() - ypos - 1));
	}

	/**
	 * Read a texture from the disk and assign it to an index.
	 * @param id The ID/index to assign the texture to.
	 * @param path The path of the image to load.
	 */
	public void addTexture(Integer id, String path) {
		// Attempt to read the image from the disk.
		try {
			BufferedImage img = ImageIO.read(new File(path));
			// Assign the texture to the ID.
			m_textures[id] = img;
		} catch (IOException e) {
			// Print stack trace if an exception is thrown.
			e.printStackTrace();
		}
	}

	/**
	 * The meat of the rasterizer, handles filling triangles.
	 * @param id The ID of the texture to sample.
	 * @param model The model matrix (transformation).
	 * @param proj The projection matrix (world->screen)
	 * @param va The first vertex.
	 * @param vb The second vertex.
	 * @param vc The third vertex.
	 * @param ta The first texture coordinate.
	 * @param tb The second texture coordinate.
	 * @param tc The third texture coordinate.
	 */
	private synchronized void fillTriangle(DrawAction action) {
		// Get the texture at the given ID.
		BufferedImage tex = m_textures[action.tex];
		// Compute model-projection transform.
		Matrix4 mp = action.proj.mult(action.model);

		// First blend variable (between vert/texcoord a and b).
		float alphaX;
		// Interpolated vertices.
		Vector3 ia;
		Vector3 ib;
		// Interpolated texture coordinates.
		Vector2 ita;
		Vector2 itb;

		// Define the position of the point light in the scene.
		Vector3 lightPos = new Vector3(0.0f, 1.0f, 2.0f);
		Color lightColor = new Color(128, 128, 255);


		// Compute the number of X and Y interpolation iterations to perform.
		final int ITER_X = m_screenWidth;
		final int ITER_Y = m_screenHeight;
		// Compute the inverses of the X and Y iterations to multiply to map
		// the iteration counter to the range [0, 1].
		final float ITER_X_INV = 1.0f / ITER_X;
		final float ITER_Y_INV = 1.0f / ITER_Y;

		// Perform X iterations (from 0 to ITER_X).
		for (int i = 0; i < ITER_X; ++i) {
			// Set first blend variable to the iteration counter mapped to
			// the range [0, 1] using ITER_X_INV.
			alphaX = (float) i * ITER_X_INV;

			// Interpolate between first and second vertex.
			ia = action.va.lerp(action.vb, alphaX);
			// Interpolate first and third vertex.
			ib = action.va.lerp(action.vc, alphaX);
			// Interpolate between first and second texture coordinate.
			ita = action.ta.lerp(action.tb, alphaX);
			// Interpolate between first and third texture coordinate.
			itb = action.ta.lerp(action.tc, alphaX);

			// Second interpolation alpha.
			float alphaY;
			// Second interpolation vertex.
			Vector3 ic;
			// Screen-space coordinate.
			Vector3 ssc;
			// Interpolated texture coordinate.
			Vector2 it;

			// Perform Y iterations (from 0 to ITER_Y).
			for (int i2 = 0; i2 < ITER_Y; ++i2) {
				// Set second blend variable to the iteration counter mapped to
				// the range [0, 1] using ITER_Y_INV.
				alphaY = (float) i2 * ITER_Y_INV;
				// Interpolate between the first interpolated variable and the
				// second interpolated variable.
				ic = ia.lerp(ib, alphaY);
				// Compute screen-space coordinate by multiplying the
				// world-space coordinate by the model-projection matrix, then
				// divide by w to make it a 3-dimensional vector.
				ssc = mp.mult(new Vector4(ic, 1.0f)).wdivide();

				// Calculate the screen coordinates to draw the pixel to.
				int dcoordX =
					(int) clamp(
						// Map [-1, 1] to [0, 1].
						(ssc.x + 1.0f) * 0.5f * m_screenWidth,
						0.0f,
						(float) m_screenWidth - 1);
				int dcoordY =
					(int) clamp(
						// Map [-1, 1] to [0, 1]
						(ssc.y + 1.0f) * 0.5f * m_screenHeight,
						0.0f,
						(float) m_screenHeight - 1);

				// Perform depth test to prevent drawing occluded fragments.
				if (ssc.z < m_depthBuffer[dcoordX][dcoordY]) {
					// Compute texture coordinate by blending first interpolated
					// coordinate with second interpolated coordinate.
					it = ita.lerp(itb, alphaY);
					// Compute world-space position for lighting calculations.
					Vector3 world = 
						action.model.mult(new Vector4(ic, 1.0f)).wdivide();
					// Sample texture using texture coordinate.
					Color color = sampleImage(tex, it.x, it.y);

					// Compute the inverse square attenuation factor on the light.
					float atten = 
						1.0f 
						/ (1.0f 
							+ (float) Math.pow(world.distance(lightPos), 2.0f));
					// Apply attenuation by blending sampled color with black using
					// the attenuation factor as the alpha.
					color =
						lerpColor(
							lerpColor(Color.BLACK, color, (float) Math.pow(atten, 0.5f)),
							lightColor,
							clamp(atten * 1.0f, 0.0f, 1.0f));

					// Write screen-space depth value to depth buffer.
					m_depthBuffer[dcoordX][dcoordY] = ssc.z;
					// Write color to backbuffer.
					m_backBuffer.setRGB(
						(int) dcoordX,
						(int) m_screenHeight - dcoordY - 1,
						color.getRGB());
				}
			}
		}
	}

	/*
	 * General timing variables.
	 */

	// Last chrono time.
	float m_last = 0.0f;
	// Current elapsed time.
	float m_elapsed = 0.0f;

	/*
	 * FPS profiling variables.
	 */

	// FPS time accumulator.
	float m_fpsAccumulator = 0.0f;
	// Number of frames drawn.
	int   m_frames = 0;

	/**
	 * Overriden JPanel paintComponent, for drawing the rasterized scene.
	 * @param g The graphics object to draw to.
	 */
	@Override
	public void paintComponent(Graphics g) {
		// Get the graphics object from the BufferedImage back buffer.
		Graphics ig = m_backBuffer.getGraphics();

		// Compute projection matrix from screen width/height and fixed FOV
		// and near/far planes.
		Matrix4 proj =
			Matrix4.perspective(
				(float) m_backBuffer.getWidth()
				/ (float) m_backBuffer.getHeight(),
				90.0f,
				0.01f,
				10.0f);

		// Create pool for render threads.
		ArrayList<Thread> threadPool = new ArrayList<Thread>();

		// Define triangle sum to be displayed as debug info.
		int triangleSum = 0;

		// Iterate through meshes in scene.
		for (int i = 0; i < m_meshIndex; ++i) {
			// Get mesh from index.
			Mesh m = m_meshes[i];
			// Add polycount.
			triangleSum += m.getTriCount();

			// Get the verts of the mesh.
			Vector3[] verts = m.getVerts();

			// Loop through every three verts (every triangle).
			for (int v = 0; v < m.getTriCount() * 3; v += 3) {
				// Enqueue draw action.
				synchronized (m_drawQueue) {
					m_drawQueue.add(
						new DrawAction(
							m.getTextureID(), 
							m.getTransformMatrix(), 
							proj,
							m.getVerts()[v + 0], 
							m.getVerts()[v + 1], 
							m.getVerts()[v + 2], 
							m.getCoords()[v + 0], 
							m.getCoords()[v + 1], 
							m.getCoords()[v + 2]));
				}
			}
		}

		while (m_drawCount < triangleSum) {
		}
		m_drawCount = 0;

		// Draw the backbuffer to the screen.
		g.drawImage(
			m_backBuffer,
			0,
			0,
			m_screenWidth * m_RESDIVISOR,
			m_screenHeight * m_RESDIVISOR,
			null);
		// Set the text color to draw the debug info.
		g.setColor(Color.WHITE);
		// Draw polycount to screen.
		g.drawString("POLYCOUNT: " + triangleSum, 32, 32);
		// Clear the backbuffer.
		ig.clearRect(0, 0, m_screenWidth, m_screenHeight);
		// Fill depth buffer with 100% depth.
		for (int x = 0; x < m_screenWidth; ++x) {
			for (int y = 0; y < m_screenHeight; ++y) {
				m_depthBuffer[x][y] = 1.0f;
			}
		}

		// Compute delta time/elapsed time.
		float now = System.nanoTime() * 1E-9f;
		float delta = now - m_last;
		m_elapsed += delta;
		m_last = now;

		// Add delta time to FPS accumulator and increment frame counter.
		m_fpsAccumulator += delta;
		++m_frames;
		// Print frame count to console if FPS accumulator goes over 1 second.
		if (m_fpsAccumulator > 1.0f) {
			System.out.println("FPS: " + m_frames);
			// Reset FPS accumulators.
			m_frames = 0;
			m_fpsAccumulator = 0.0f;
		}

		// Update listener if non-null.
		if (m_listener != null) {
			m_listener.update(delta);
		}
	}

	/**
	 * Set the update listener to notify once per update.
	 * @param listener The listener to assign.
	 */
	public void setUpdateListener(IUpdateListener listener) {
		m_listener = listener;
	}

	/**
	 * Add a mesh to the render array.
	 * @param m The mesh to add.
	 */
	public void addMesh(Mesh m) {
		m_meshes[m_meshIndex++] = m;
	}

	/**
	 * Cnstruct a render panel given a width/height.
	 * @param width The width of the render target in pixels.
	 * @param height The height of the render target in pixels.
	 */
	RasterPanel(int width, int height) {
		// Initialize back buffer.
		m_backBuffer = new BufferedImage(
			width / m_RESDIVISOR,
			height / m_RESDIVISOR,
			BufferedImage.TYPE_INT_RGB);
		// Initialize depth buffer.
		m_depthBuffer = new float[width / m_RESDIVISOR][height / m_RESDIVISOR];
		// Initialize threads.

		// Use ones less thread than the number of available CPU cores.
		m_threadCount = Runtime.getRuntime().availableProcessors() - 1;
		m_renderThreads = new Thread[m_threadCount];
		m_drawQueue = new ArrayDeque<DrawAction>();
		for (int i = 0; i < m_threadCount; ++i) {
			m_renderThreads[i] = new Thread(new Runnable() {
				@Override
				public synchronized void run() {
					for (;;) {
						// Initialize next draw action to null.
						DrawAction action = null;

						// Lock draw queue.
						synchronized (m_drawQueue) {
							// Process the next action if not empty.
							if (!m_drawQueue.isEmpty()) {
								action = m_drawQueue.pop();
							}
						}
						if (action != null) {
							// Draw triangle and increment counter.
							fillTriangle(action);
							++m_drawCount;
							// Allow other threads to do computations.
							Thread.yield();
						}
					}
				}
			});
			m_renderThreads[i].start();
		}
		// Initialize state.
		m_textures = new BufferedImage[32];
		m_meshes = new Mesh[32];
		m_screenWidth = width / m_RESDIVISOR;
		m_screenHeight = height / m_RESDIVISOR;
	}

}
