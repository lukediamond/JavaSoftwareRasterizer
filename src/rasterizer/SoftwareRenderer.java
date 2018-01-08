package rasterizer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Point;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import rasterizer.RasterPanel;
import rasterizer.Mesh;

public class SoftwareRenderer extends JFrame {
	private RasterPanel m_panel;

	SoftwareRenderer(int width, int height) {
		m_panel = new RasterPanel(width, height);

		this.setSize(width, height);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
		Thread renderThread = new Thread(() -> {
			m_panel.addTexture(0, "img.png");
			Mesh m = new Mesh(
				0,
				new Vector3[] {
					new Vector3(-1.0f, -1.0f, 1.0f), // bottom left
					new Vector3(-1.0f, +1.0f, 1.0f), // top left
					new Vector3(+1.0f, +1.0f, 1.0f), // top right

					new Vector3(+1.0f, +1.0f, 0.0f), // top right
					new Vector3(+1.0f, -1.0f, 0.0f), // bottom right
					new Vector3(-1.0f, -1.0f, 0.0f), // bottom left
				},
				new Vector2[] {
					new Vector2(0.0f, 0.0f), // bottom left
					new Vector2(0.0f, 1.0f), // top left
					new Vector2(1.0f, 1.0f), // top right

					new Vector2(1.0f, 1.0f), // top right
					new Vector2(1.0f, 0.0f), // bottom right
					new Vector2(0.0f, 0.0f), // bottom left
				});
			m.setPosition(0.0f, 0.0f, 15.0f);
			m_panel.addMesh(m);
			float start = System.nanoTime() * 1E-9f;
			for (;;) {
				m_panel.repaint();
				float now = System.nanoTime() * 1E-9f;
				m.setRotation(0.0f, 90.0f * (now - start), 0.0f);
			}
		});
		this.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					System.exit(0);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {

			}
		});
		renderThread.start();
		this.setContentPane(m_panel);
		this.setVisible(true);
	}

	public static void main(String[] args) { new SoftwareRenderer(1280, 720); }

}
