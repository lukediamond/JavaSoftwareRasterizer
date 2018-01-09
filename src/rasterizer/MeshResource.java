package rasterizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class MeshResource {
    private Vector3[] m_verts;
    private Vector2[] m_coords;

	public MeshResource(String path) {
		File file = new File(path);

        ArrayList<Vector3> verts   = new ArrayList<Vector3>();
        ArrayList<Vector2> coords  = new ArrayList<Vector2>();
        ArrayList<Integer> vindices = new ArrayList<Integer>();
        ArrayList<Integer> tindices = new ArrayList<Integer>();

        BufferedReader freader = null;
        try {
            freader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        String line = null;
        do {
            try {
                line = freader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            if (line == null) { break; }

            if (line.startsWith("v ")) {
                String split[] = line.split(" ");
                float x = Float.parseFloat(split[1]);
                float y = Float.parseFloat(split[2]);
                float z = Float.parseFloat(split[3]);
                verts.add(new Vector3(x, y, z));
            } else if (line.startsWith("f ")) {
                String split[] = line.split(" ");
                for (int i = 1; i <= 3; ++i) {
                    String pairsplit[] = split[i].split("/");
                    int vc = Integer.parseInt(pairsplit[0]);
                    int tc = Integer.parseInt(pairsplit[1]);
                    vindices.add(vc);
                    tindices.add(tc);
                }
            } else if (line.startsWith("vt ")) {
                String split[] = line.split(" ");
                float u = Float.parseFloat(split[1]);
                float v = Float.parseFloat(split[2]);
                coords.add(new Vector2(u, v));
            }
        }
        while (line != null);

        try {
            freader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        m_verts = new Vector3[vindices.size()];
        m_coords = new Vector2[vindices.size()];

        for (int i = 0; i < vindices.size(); ++i) {
            if (i >= tindices.size()) {
                m_coords[i] = new Vector2(0.0f, 0.0f);
            } else {
                m_coords[i] = coords.get(tindices.get(i) - 1);
            }
            m_verts[i] = verts.get(vindices.get(i) - 1);
        }
	}

    public Vector3[] getVerts() {
        return m_verts;
    }

    public Vector2[] getCoords() {
        return m_coords;
    }

}