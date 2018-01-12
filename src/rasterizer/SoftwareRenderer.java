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

	float dirX = 0.0f;
	float dirY = 0.0f;
	float dirZ = 0.0f;

	float lookDirX = 0.0f;
	float lookDirY = 0.0f;

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
				// Handle movement key press.
				if (e.getKeyCode() == KeyEvent.VK_W) {
					dirZ = 1.0f;
				}
				else if (e.getKeyCode() == KeyEvent.VK_A) {
					dirX = -1.0f;
				}
				else if (e.getKeyCode() == KeyEvent.VK_S) {
					dirZ = -1.0f;
				}
				else if (e.getKeyCode() == KeyEvent.VK_D) {
					dirX = 1.0f;
				}
				else if (e.getKeyCode() == KeyEvent.VK_Q) {
					dirY = 1.0f;
				}
				else if (e.getKeyCode() == KeyEvent.VK_E) {
					dirY = -1.0f;
				}

				// Handle turning key press.
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					lookDirX = -1.0f; 
				}
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					lookDirY = -1.0f;
				}
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					lookDirX = 1.0f;
				}
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					lookDirY = 1.0f;
				}

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
				// Handle movement key release.
				if (
					e.getKeyCode() == KeyEvent.VK_W 
					|| e.getKeyCode() == KeyEvent.VK_S) {
					dirZ = 0.0f;
				}
				if (
					e.getKeyCode() == KeyEvent.VK_A
					|| e.getKeyCode() == KeyEvent.VK_D) {
					dirX = 0.0f;
				}
				if (
					e.getKeyCode() == KeyEvent.VK_Q
					|| e.getKeyCode() == KeyEvent.VK_E) {
					dirY = 0.0f;
				}

				// Handle turning key release
				if (
					e.getKeyCode() == KeyEvent.VK_UP 
					|| e.getKeyCode() == KeyEvent.VK_DOWN) {
					lookDirY = 0.0f;
				}
				if (
					e.getKeyCode() == KeyEvent.VK_LEFT
					|| e.getKeyCode() == KeyEvent.VK_RIGHT) {
					lookDirX = 0.0f;
				}
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

		// Set up scene.
		m_panel.addTexture(0, "difmap.png");
		m_panel.addTexture(1, "floor.png");

		// Load mesh from file.
		MeshResource cuberes = new MeshResource("cube.obj");
		// Create mesh(es).
		Mesh cube0 = new Mesh(0, cuberes);
		cube0.setPosition(2.0f, 0.0f, 6.0f);

		// Load floor from file
		MeshResource floorres = new MeshResource("plane.obj");
		Mesh floor = new Mesh(1, floorres);

		floor.setPosition(0.0f, -1.5f, 6.0f);
		floor.setRotation(90.0f, 0.0f, 0.0f);
		floor.setScale(2.0f, 2.0f, 0.0f);

		// Add meshes to panel.
		m_panel.addMesh(cube0);
		m_panel.addMesh(floor);

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
				// Get current camera transform.
				Vector3 camPos = m_panel.getCameraPosition();
				Vector3 camRot = m_panel.getCameraRotation();
				// Transform camera based on user input.
				m_panel.setCameraPosition(
					new Vector3(
						camPos.x + dirX * delta * 2.0f, 
						camPos.y + dirY * delta * 2.0f, 
						camPos.z + dirZ * delta * 2.0f));
				m_panel.setCameraRotation(
					new Vector3(
						camRot.x + lookDirY * delta * 45.0f,
						camRot.y + lookDirX * delta * 45.0f,
						camRot.z));
				cube0.setRotation(45.0f, 90.0f * elapsed, 45.0f * elapsed);
			}
		});


		// Open window.
		this.setVisible(true);
		// Start indefinite repaint thread.
		// Will be killed at window exit.
		new Thread(() -> { for (;;) { m_panel.repaint(); } }).start();
	}

	/**
	 * Main method. Creates an instance of the renderer with the window size.
	 * @param args The command-line arguments passed in by the OS.
	 */
	public static void main(String[] args) { new SoftwareRenderer(640, 480); }

}
