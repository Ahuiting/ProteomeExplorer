package ProteomeExplorer.statisticalDataShow;

import ProteomeExplorer.WindowController;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.HashMap;

public class StatisticalDataShow {

    public void setup(WindowController controller, HashMap<String, HashMap<String, Integer>> map) {
        ChartsBuilder charts = new ChartsBuilder(map);
        HBox hBox = new HBox(charts.moleculePieChart(), charts.moleculeBarChart());
        controller.getChartsVBox().getChildren().clear();
        controller.getChartsVBox().getChildren().add(hBox);
        ArrayList<PieChart> polymerPieCharts = charts.polymerPieChart();
        ArrayList<BarChart> polymerBarCharts = charts.polymerBarChart();
        for (int i = 0; i < polymerPieCharts.size(); i++) {
            HBox h = new HBox(polymerPieCharts.get(i), polymerBarCharts.get(i));
            controller.getChartsVBox().getChildren().add(h);
        }
    }
}
