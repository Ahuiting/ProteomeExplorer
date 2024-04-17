package ProteomeExplorer.statisticalDataShow;

import javafx.geometry.Side;
import javafx.scene.chart.*;

import java.util.ArrayList;
import java.util.HashMap;

public class ChartsBuilder {
    HashMap<String, HashMap<String, Integer>> countMap;

    public ChartsBuilder(HashMap<String, HashMap<String, Integer>> countMap) {
        this.countMap = countMap;
    }

    public PieChart drawPieChart(HashMap<String, Integer> map) {
        var chart = new PieChart();
        chart.setLabelLineLength(10);
        chart.setLegendSide(Side.RIGHT);
        for (String k : map.keySet()) {
            chart.getData().add(new PieChart.Data(k, map.get(k)));
        }
        return chart;
    }

    public ArrayList<PieChart> polymerPieChart() {
        ArrayList<PieChart> polymerPieCharts = new ArrayList<>();
        for (String k : this.countMap.keySet()) {
            PieChart chart = drawPieChart(countMap.get(k));
            chart.setTitle(k + " Chain Composition");
            polymerPieCharts.add(chart);
        }
        return polymerPieCharts;
    }

    public PieChart moleculePieChart() {
        HashMap<String, Integer> allPolymer =getCountMolecule();
        PieChart chart = drawPieChart(allPolymer);
        chart.setTitle("Molecule Composition");
        return chart;
    }

    public BarChart drawBarChart(HashMap<String, Integer> map) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Amino Acid");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount");

        BarChart chart = new BarChart(xAxis, yAxis);
        XYChart.Series data = new XYChart.Series();
        for (String k : map.keySet()) {
            data.getData().add(new XYChart.Data(k, map.get(k)));
        }
        chart.getData().add(data);
        return chart;
    }
    public ArrayList<BarChart> polymerBarChart() {
        ArrayList<BarChart> polymerBarCharts = new ArrayList<>();
        for (String k : this.countMap.keySet()) {
            var chart = drawBarChart(countMap.get(k));
            chart.setTitle(k + " Chain Amino Acid Amount");
            polymerBarCharts.add(chart);
        }
        return polymerBarCharts;
    }

    public BarChart moleculeBarChart() {
        HashMap<String, Integer> allPolymer =getCountMolecule();
        var chart = drawBarChart(allPolymer);
        chart.setTitle("Molecule Amino Acid Amount");
        return chart;
    }

    public  HashMap<String, Integer> getCountMolecule(){
        HashMap<String, Integer> allPolymer = new HashMap();
        for (HashMap<String, Integer> polymerChart : this.countMap.values()) {
            for (String key : polymerChart.keySet()) {
                if (allPolymer.containsKey(key)) {
                    allPolymer.put(key, allPolymer.get(key) + polymerChart.get(key));
                } else allPolymer.put(key, polymerChart.get(key));
            }
        }
        return allPolymer;
    }

}
