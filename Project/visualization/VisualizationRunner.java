package visualization;

import algorithms.GWO;
import algorithms.YenKShortestPaths;
import algorithms.GWO.RouteResult;
import algorithms.YenKShortestPaths.Path;
import graph.*;

import java.util.List;

public class VisualizationRunner {

    /**
     * Runs all project visualizations:
     * 1) Graph with Yen's K-Shortest Paths
     * 2) GWO convergence plot
     */
    public static void run(
            Graph graph,
            Node source,
            Node destination,
            int kPaths,
            int numWolves,
            int maxIterations
    ) {

        // ---------- Yen’s K-Shortest Paths ----------
        YenKShortestPaths yen = new YenKShortestPaths(graph);
        List<Path> yenPaths = yen.getKShortestPaths(source, destination, kPaths);

        System.out.println("Yen's K-Shortest Paths:");
        for (Path p : yenPaths) {
            System.out.println(p);
        }

        // Visualize Yen paths on graph
        YenGraphViewer.show(graph, yenPaths);

        // ---------- Grey Wolf Optimizer ----------
        GWO gwo = new GWO(graph, numWolves, maxIterations);
        RouteResult gwoResult = gwo.optimize(source, destination);

        System.out.println("\nGWO Result:");
        System.out.println(gwoResult);

        // Visualize GWO convergence
        GWOConvergencePlot.show(gwo.getConvergenceHistory());
    }
}
