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

		this.setContentPane(m_panel);
		this.setVisible(true);

		m_panel.addTexture(0, "difmap.png");

		MeshResource mres = new MeshResource("untitled.obj");
		Mesh m = new Mesh(
			0,
			mres);
		m.setPosition(0.0f, 0.0f, 3.0f);
		m_panel.addMesh(m);
		float start = System.nanoTime() * 1E-9f;

		m_panel.setUpdateListener(new IUpdateListener() {
			float elapsed = 0.0f;
			@Override
			public void update(float delta) {
				elapsed += delta;
				m.setRotation(45.0f, 90.0f * elapsed, 0.0f);
			}
		});

		for (;;) {
			m_panel.repaint();
		}
	}

	public static void main(String[] args) { new SoftwareRenderer(640, 480); }

}
