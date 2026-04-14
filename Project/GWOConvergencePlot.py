package visualization;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.*;

import javax.swing.*;
import java.util.List;

public class GWOConvergencePlot {

    public static void show(List<Double> fitnessHistory) {
        XYSeries series = new XYSeries("Best Fitness");

        for (int i = 0; i < fitnessHistory.size(); i++) {
            series.add(i, fitnessHistory.get(i));
        }

        XYDataset dataset = new XYSeriesCollection(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Grey Wolf Optimizer Convergence",
                "Iteration",
                "Best Path Cost",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        ChartPanel panel = new ChartPanel(chart);
        JFrame frame = new JFrame("GWO Convergence");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
