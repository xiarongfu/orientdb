package com.orientechnologies.orient.graph.batch;

import com.orientechnologies.common.io.OFileUtils;
import com.orientechnologies.common.listener.OProgressListener;
import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

/**
 * Single-threads graph importer. Source file downloaded from
 * http://snap.stanford.edu/data/amazon/productGraph/categoryFiles/ratings_Books.csv.
 * 
 * @author Luca Garulli
 */

public class OGraphImporterSTAPITest {
  static int  row             = 0;
  static long lastVertexCount = 0;
  static long lastEdgeCount   = 0;

  public static void main(String[] args) throws IOException, InterruptedException {
    // String dbUrl = "memory:amazonReviews";
    String dbUrl = "plocal:/temp/databases/amazonReviews";

    final File f = new File("/temp/databases/amazonReviews");
    if (f.exists())
      OFileUtils.deleteRecursively(f);

    final OrientGraph roGraph = new OrientGraph(dbUrl, "admin", "admin");

    final OrientGraph graph = new OrientGraph(dbUrl, "admin", "admin");

    OrientVertexType user = graph.createVertexType("User");
    user.createProperty("uid", OType.STRING);

    final OIndex<?> userIndex = user.createIndex("User.uid", OClass.INDEX_TYPE.UNIQUE.toString(), (OProgressListener) null,
        (ODocument) null, "AUTOSHARDING", new String[] { "uid" });

    OrientVertexType product = graph.createVertexType("Product");
    product.createProperty("uid", OType.STRING);

    final OIndex<?> productIndex = product.createIndex("Product.uid", OClass.INDEX_TYPE.UNIQUE.toString(), (OProgressListener) null,
        (ODocument) null, "AUTOSHARDING", new String[] { "uid" });

    graph.createEdgeType("Reviewed");

    final File file = new File("/Users/luca/Downloads/ratings_Books.csv");
    final BufferedReader br = new BufferedReader(new FileReader(file));

    Orient.instance().scheduleTask(new TimerTask() {
      @Override
      public void run() {
        roGraph.makeActive();
        final long vertexCount = roGraph.countVertices();
        final long edgeCount = roGraph.countEdges();

        System.out.println(String.format("%d vertices=%d %d/sec edges=%d %d/sec", row, vertexCount,
            ((vertexCount - lastVertexCount) * 1000 / 2000), edgeCount, ((edgeCount - lastEdgeCount) * 1000 / 2000)));

        lastVertexCount = vertexCount;
        lastEdgeCount = edgeCount;
      }
    }, 2000, 2000);

    try {
      for (String line; (line = br.readLine()) != null;) {
        row++;

        final String[] parts = line.split(",");

        if (parts.length != 4) {
          // SKIP IT
          System.out.print("Skipped invalid line " + row + ": " + line);
          continue;
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("score", new Float(parts[2]).intValue());
        properties.put("date", Long.parseLong(parts[3]));

        final Object k1 = userIndex.get(parts[0]);
        OrientVertex v1;
        if (k1 == null) {
          v1 = graph.addVertex("class:User", "uid", parts[0]);
        } else
          v1 = graph.getVertex(k1);

        final Object k2 = productIndex.get(parts[1]);
        OrientVertex v2;
        if (k2 == null) {
          v2 = graph.addVertex("class:Product", "uid", parts[1]);
        } else
          v2 = graph.getVertex(k2);

        final OrientEdge edge = graph.addEdge(null, v1, v2, "Reviewed");
        edge.setProperties(properties);

        if (row % 2 == 0) {
          graph.commit();
        }
      }
    } finally {
      br.close();
    }

    graph.shutdown();
    roGraph.shutdown();
  }
}
