package rasterizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Mesh resource class, for loading a 3D model from the disk.
 */
public class MeshResource {
    // Vert/coord data.
    private Vector3[] m_verts;
    private Vector2[] m_coords;

    /**
     * Construct a mesh resource given a mesh file path.
     * @param path The file path of the model to load.
     */
	public MeshResource(String path) {
        // Open the file at the given path.
		File file = new File(path);

        // Create the vert/texcoord/index dynamic arrays for mesh data loading.
        ArrayList<Vector3> verts = new ArrayList<Vector3>();
        ArrayList<Vector2> coords = new ArrayList<Vector2>();
        ArrayList<Integer> vindices = new ArrayList<Integer>();
        ArrayList<Integer> tindices = new ArrayList<Integer>();

        // Try opening a buffered reader on the specified mesh file.
        BufferedReader freader = null;
        try {
            freader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            // Print stack trace and crash if an exception occurred.
            e.printStackTrace();
            System.exit(-1);
        }

        // Declare line to be read by the reader.
        String line = null;
        do {
            // Try to read a line from the file.
            try {
                line = freader.readLine();
            } catch (IOException e) {
                // Print stack trace and continue to next line if an exception
                // occurred.
                e.printStackTrace();
                continue;
            }

            // Break if the line is null, as that marks the EoF.
            if (line == null) { break; }

            // Match the beginning of the line with various OBJ-format
            // patterns.
            if (line.startsWith("v ")) {
                // Read vertex from line.

                // Split line into tokens.
                String split[] = line.split(" ");
                // Parse vertex coordinates.
                float x = Float.parseFloat(split[1]);
                float y = Float.parseFloat(split[2]);
                float z = Float.parseFloat(split[3]);
                // Add vertex position to list.
                verts.add(new Vector3(x, y, z));
            } else if (line.startsWith("f ")) {
                // Read face data from line.

                // Split line into tokens.
                String split[] = line.split(" ");
                for (int i = 1; i <= 3; ++i) {
                    // Split vert/texcoord index pairs.
                    String pairsplit[] = split[i].split("/");
                    // Parse indices.
                    int vc = Integer.parseInt(pairsplit[0]);
                    int tc = Integer.parseInt(pairsplit[1]);
                    // Add indices.
                    vindices.add(vc);
                    tindices.add(tc);
                }
            } else if (line.startsWith("vt ")) {
                // Read texture coordinate from line.

                // Split line into tokens.
                String split[] = line.split(" ");
                // Parse texture coordinates.
                float u = Float.parseFloat(split[1]);
                float v = Float.parseFloat(split[2]);
                // Add texture coordinate to list.
                coords.add(new Vector2(u, v));
            }
        }
        // Loop until null line is read.
        while (line != null);

        // Attempt to close the reader.
        try {
            freader.close();
        } catch (IOException e) {
            // Print the stack trace if an exception occurred.
            e.printStackTrace();
        }

        // Allocate space for the fixed-size arrays.
        m_verts = new Vector3[vindices.size()];
        m_coords = new Vector2[vindices.size()];

        // Copy vertex data into fixed-size arrays.
        for (int i = 0; i < vindices.size(); ++i) {
            // If there are more verts than texture coordinates then
            // insert zero vectors, otherwise insert the texture coordinate at
            // the current index.
            if (i >= tindices.size()) {
                m_coords[i] = new Vector2(0.0f, 0.0f);
            } else {
                m_coords[i] = coords.get(tindices.get(i) - 1);
            }
            // Add the vert at the current index.
            m_verts[i] = verts.get(vindices.get(i) - 1);
        }
	}

    /**
     * Get the verts of the resource.
     * @return The resource's verts.
     */
    public Vector3[] getVerts() {
        return m_verts;
    }

    /**
     * Get the texture coordinates of the resource.
     * @return The resource's texture coordinates.
     */
    public Vector2[] getCoords() {
        return m_coords;
    }

}