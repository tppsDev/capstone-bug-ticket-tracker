/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.view_controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import sdtracker.database.ReportQueryServiceManager;
import sdtracker.database.ReportQueryServiceManager.GenerateCurrentBugBreakdownReportService;
import sdtracker.model.CurrentBugBreakdownReport;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class CurrentBugBreakdownReportController implements Initializable {
    private static final double SECONDS_TO_HOURS_THRESHOLD = 3600.0;
    private static final double SECONDS_TO_DAYS_THRESHOLD = 86400.0;
    
    @FXML private ProgressIndicator progressIndicator;
    @FXML private PieChart bugStatusBreakdownPieChart;
    @FXML private PieChart bugPriorityBreakdownPieChart;
    @FXML private VBox agingVBox;
    @FXML private Label newBugAgingLabel;
    @FXML private Label openBugAgingLabel;
    @FXML private Label fixInDevelopmentBugAgingLabel;
    @FXML private Label fixPendingTestingBugAgingLabel;
    @FXML private Label resolvedBugAgingLabel;
    @FXML private Label onHoldBugAgingLabel;
    @FXML private Label criticalBugAgingLabel;
    @FXML private Label urgentBugAgingLabel;
    @FXML private Label normalBugAgingLabel;
    @FXML private Label lowBugAgingLabel;
    
    private CurrentBugBreakdownReport report;
    
    ReportQueryServiceManager queryServiceManager = ReportQueryServiceManager.getInstance();
    GenerateCurrentBugBreakdownReportService generateReportService 
            = queryServiceManager.new GenerateCurrentBugBreakdownReportService();
    
    
        
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initService();
        establishBindings();
        runGenerateReportService();
    }
    
    private void initService() {
        generateReportService.setOnSucceeded((event) -> {
            report = generateReportService.getValue();
            loadData();
        });
        
        generateReportService.setOnFailed((event) -> {
            
        });
    }
    
    private void establishBindings() {
        progressIndicator.visibleProperty().bind(generateReportService.runningProperty());
        bugStatusBreakdownPieChart.visibleProperty().bind(progressIndicator.visibleProperty().not());
        bugPriorityBreakdownPieChart.visibleProperty().bind(progressIndicator.visibleProperty().not());
        agingVBox.visibleProperty().bind(progressIndicator.visibleProperty().not());
    }
    
    private void runGenerateReportService() {
        if (!generateReportService.isRunning()) {
            generateReportService.reset();
            generateReportService.start();
        }
    }
    
    private void loadData() {
        loadStatusChart();
        loadPriorityChart();
        loadStatusAging();
        loadPriorityAging();
    }
    
    private void loadStatusChart() {
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
        
        bugStatusBreakdownPieChart.setTitle("Bug Status Distribution");
        chartData.add(new PieChart.Data("New", report.getNewBugCount()));
        chartData.add(new PieChart.Data("Open", report.getOpenBugCount()));
        chartData.add(new PieChart.Data("Fix in development", report.getFixInDevelopmentBugCount()));
        chartData.add(new PieChart.Data("Fix in testing", report.getFixPendingTestingBugCount()));
        chartData.add(new PieChart.Data("Resolved", report.getResolvedBugCount()));
        chartData.add(new PieChart.Data("On Hold", report.getOnHoldBugCount()));
        bugStatusBreakdownPieChart.setData(chartData);
        bugStatusBreakdownPieChart.setLegendSide(Side.RIGHT);
        bugStatusBreakdownPieChart.getData().forEach((data) -> {
            data.nameProperty().bind(Bindings.concat(data.getName(), " - ", 
                                                     data.pieValueProperty().intValue(), 
                                                     ", ", 
                                                     String.format("%.1f%%", data.pieValueProperty().doubleValue()/(double) report.getTotalBugs() * 100)));
        });
    }
    
    private void loadPriorityChart() {
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
        
        bugPriorityBreakdownPieChart.setTitle("Bug Priority Distribution");
        chartData.add(new PieChart.Data("Critical", report.getCriticalBugCount()));
        chartData.add(new PieChart.Data("Urgent", report.getUrgentBugCount()));
        chartData.add(new PieChart.Data("Normal", report.getNormalBugCount()));
        chartData.add(new PieChart.Data("Low", report.getLowBugCount()));
        bugPriorityBreakdownPieChart.setData(chartData);
        bugPriorityBreakdownPieChart.setLegendSide(Side.RIGHT);
        bugPriorityBreakdownPieChart.getData().forEach((data) -> {
            data.nameProperty().bind(Bindings.concat(data.getName(), " - ", 
                                                     data.pieValueProperty().intValue(), 
                                                     ", ", 
                                                     String.format("%.1f%%", data.pieValueProperty().doubleValue()/(double) report.getTotalBugs() * 100)));
        });
    }
    
    private void loadStatusAging() { 
        newBugAgingLabel.setText(createAgingLabelCaption(report.getNewBugAgeInSeconds()));
        openBugAgingLabel.setText(createAgingLabelCaption(report.getOpenBugAgeInSeconds()));
        fixInDevelopmentBugAgingLabel.setText(createAgingLabelCaption(report.getFixInDevelopmentBugAgeInSeconds()));
        fixPendingTestingBugAgingLabel.setText(createAgingLabelCaption(report.getFixPendingTestingBugAgeInSeconds()));
        resolvedBugAgingLabel.setText(createAgingLabelCaption(report.getResolvedBugAgeInSeconds()));
        onHoldBugAgingLabel.setText(createAgingLabelCaption(report.getOnHoldBugAgeInSeconds()));
    }
    
    private void loadPriorityAging() {
        criticalBugAgingLabel.setText(createAgingLabelCaption(report.getCriticalBugAgeInSeconds()));
        urgentBugAgingLabel.setText(createAgingLabelCaption(report.getUrgentBugAgeInSeconds()));
        normalBugAgingLabel.setText(createAgingLabelCaption(report.getNormalBugAgeInSeconds()));
        lowBugAgingLabel.setText(createAgingLabelCaption(report.getLowBugAgeInSeconds()));
    }
    
    private String createAgingLabelCaption(double seconds) {
        
        if (seconds > SECONDS_TO_DAYS_THRESHOLD) {
            return String.format("%.2f Days", seconds / SECONDS_TO_DAYS_THRESHOLD);
        }
        
        if (seconds > SECONDS_TO_HOURS_THRESHOLD) {
            return String.format("%.2f Hours", seconds / SECONDS_TO_HOURS_THRESHOLD);
        }
        
        if (seconds <= 0.0) {
            return "No Data";
        }
        
        return String.format("%.2f Minutes", seconds / 60.0);
    } 
    
}
