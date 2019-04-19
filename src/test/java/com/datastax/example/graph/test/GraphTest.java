package com.datastax.example.graph.test;

import com.datastax.driver.dse.DseCluster;
import com.datastax.driver.dse.DseSession;
import com.datastax.driver.dse.graph.GraphResultSet;
import com.datastax.driver.dse.graph.GraphStatement;

import com.datastax.dse.graph.api.DseGraph;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Simple JUnit DSE Graph test example
 *
 */
public class GraphTest {
    private static final Logger logger = LoggerFactory.getLogger(GraphTest.class);
    // run DSE in docker and use IP of docker container
    private static final String GRAPH_HOST = "127.0.0.1";
    private static final String GRAPH_NAME = "remote_test";
    private DseCluster dseCluster = null;
    private DseSession dseSession = null;

    /**
     * Connect to the DSE docker container, establish a session and create a test graph
     *
     * @throws Exception if any errors occur
     */
    @org.junit.Before
    public void setUp() throws Exception {
        // Connect to DSE cluster
        DseCluster dseCluster = DseCluster.builder()
                .addContactPoint(GRAPH_HOST)
                .build();

        dseSession = dseCluster.connect();
        logger.debug("Connected to " + GRAPH_HOST);

        // Create test graph
        dseSession.executeGraph("system.graph('" + GRAPH_NAME + "').ifNotExists().create()");
        logger.debug("Created graph " + GRAPH_NAME);
    }

    /**
     * Destroy the test graph and close the session with the DSE cluster
     *
     * @throws Exception if any errors occur
     */
    @org.junit.After
    public void tearDown() throws Exception {
        if (dseCluster != null) {
            // dseSession.executeGraph("system.graph('" + GRAPH_NAME + "').drop()");
            dseCluster.close();
            logger.debug("Disconnected from " + GRAPH_HOST);
        }
    }

    /**
     * Simple test to read a vertex and check correct property is returned using the fluent graph API.
     *
     * @throws Exception if any errors occour
     */
    @org.junit.Test
    public void testGraphQuery() throws Exception {
        try {
            // Query the graph for a vertex
            GraphTraversalSource g = DseGraph.traversal();
            GraphTraversal traversal = g.V().has("person", "name", "marko"); // Java-based Gremlin Traversal API, more comfortable than a String query

            GraphStatement s1 = DseGraph.statementFromTraversal(traversal).setGraphName(GRAPH_NAME);
            GraphResultSet rs = dseSession.executeGraph(s1);

            String name = rs.one().asVertex().getProperty("name").toString();

            logger.debug("Name property: " + name);

            // Check vertex label matches value created
            assertEquals(name, "marko");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}