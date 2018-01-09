package rasterizer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Point;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import rasterizer.RasterPanel;
import rasterizer.Mesh;

/**
 * Main class. Handles opening/managing the entire application, and creates a
 * RasterPanel.
 */
public class SoftwareRenderer extends JFrame {
	// RasterPanel to handle rendering.
	private RasterPanel m_panel;

	/**
	 * Constructor of the renderer. Creates an instance of the renderer.
	 * @param width The width of the window to create.
	 * @param height The height of the window to create.
	 */
	SoftwareRenderer(int width, int height) {
		// Call superclass constructor.
		super();

		// Initialize render panel.
		m_panel = new RasterPanel(width, height);

		// Set up JFrame super class.
		this.setSize(width, height);
		this.setTitle("Software Renderer");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);

		// Add key listener for input handling.
		this.addKeyListener(new KeyListener() {
			/**
			 * Handles key press inside the window.
			 * @param e The key event emitted.
			 */
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					System.exit(0);
				}
			}

			/**
			 * Handles key release inside the window.
			 * @param e The key event emitted.
			 */
			@Override
			public void keyReleased(KeyEvent e) {
			}

			/**
			 * Handles key types inside the window.
			 * @param e The key event emitted.
			 */
			@Override
			public void keyTyped(KeyEvent e) {

			}
		});

		// Set window content pane to render panel.
		this.setContentPane(m_panel);
		this.setVisible(true);

		// Set up scene.
		m_panel.addTexture(0, "difmap.png");

		// Load mesh from file.
		MeshResource mres = new MeshResource("untitled.obj");
		Mesh m = new Mesh(
			0,
			mres);
		m.setPosition(0.0f, 0.0f, 3.0f);

		// Add mesh to panel.
		m_panel.addMesh(m);

		// Set update listener for moving the mesh.
		m_panel.setUpdateListener(new IUpdateListener() {
			float elapsed = 0.0f;

			/**
			 * Update the input scene.
			 * @param delta The measured time duration of the last update.
			 */
			@Override
			public void update(float delta) {
				elapsed += delta;
				m.setRotation(45.0f, 90.0f * elapsed, 0.0f);
			}
		});

		// Repaint/update/render indefinitely.
		for (;;) {
			m_panel.repaint();
		}
	}

	/**
	 * Main method. Creates an instance of the renderer with the window size.
	 * @param args The command-line arguments passed in by the OS.
	 */
	public static void main(String[] args) { new SoftwareRenderer(640, 480); }

}
