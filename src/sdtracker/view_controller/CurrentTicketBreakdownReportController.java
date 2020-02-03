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
import sdtracker.database.ReportQueryServiceManager.GenerateCurrentTicketBreakdownReportService;
import sdtracker.model.CurrentTicketBreakdownReport;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class CurrentTicketBreakdownReportController implements Initializable {
    private static final double SECONDS_TO_HOURS_THRESHOLD = 3600.0;
    private static final double SECONDS_TO_DAYS_THRESHOLD = 86400.0;
    
    @FXML private ProgressIndicator progressIndicator;
    @FXML private PieChart ticketStatusBreakdownPieChart;
    @FXML private PieChart ticketPriorityBreakdownPieChart;
    @FXML private VBox agingVBox;
    @FXML private Label newTicketAgingLabel;
    @FXML private Label openTicketAgingLabel;
    @FXML private Label resolvedTicketAgingLabel;
    @FXML private Label onHoldTicketAgingLabel;
    @FXML private Label criticalTicketAgingLabel;
    @FXML private Label urgentTicketAgingLabel;
    @FXML private Label normalTicketAgingLabel;
    @FXML private Label lowTicketAgingLabel;
    
    private CurrentTicketBreakdownReport report;
    
    ReportQueryServiceManager queryServiceManager = ReportQueryServiceManager.getInstance();
    GenerateCurrentTicketBreakdownReportService generateReportService 
            = queryServiceManager.new GenerateCurrentTicketBreakdownReportService();
    
    
        
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
        ticketStatusBreakdownPieChart.visibleProperty().bind(progressIndicator.visibleProperty().not());
        ticketPriorityBreakdownPieChart.visibleProperty().bind(progressIndicator.visibleProperty().not());
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
        
        ticketStatusBreakdownPieChart.setTitle("Ticket Status Distribution");
        chartData.add(new PieChart.Data("New", report.getNewTicketCount()));
        chartData.add(new PieChart.Data("Open", report.getOpenTicketCount()));
        chartData.add(new PieChart.Data("Resolved", report.getResolvedTicketCount()));
        chartData.add(new PieChart.Data("On Hold", report.getOnHoldTicketCount()));
        ticketStatusBreakdownPieChart.setData(chartData);
        ticketStatusBreakdownPieChart.setLegendSide(Side.RIGHT);
        ticketStatusBreakdownPieChart.getData().forEach((data) -> {
            data.nameProperty().bind(Bindings.concat(data.getName(), " - ", 
                                                     data.pieValueProperty().intValue(), 
                                                     ", ", 
                                                     String.format("%.1f%%", data.pieValueProperty().doubleValue()/(double) report.getTotalTickets() * 100)));
        });
    }
    
    private void loadPriorityChart() {
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
        
        ticketPriorityBreakdownPieChart.setTitle("Ticket Priority Distribution");
        chartData.add(new PieChart.Data("Critical", report.getCriticalTicketCount()));
        chartData.add(new PieChart.Data("Urgent", report.getUrgentTicketCount()));
        chartData.add(new PieChart.Data("Normal", report.getNormalTicketCount()));
        chartData.add(new PieChart.Data("Low", report.getLowTicketCount()));
        ticketPriorityBreakdownPieChart.setData(chartData);
        ticketPriorityBreakdownPieChart.setLegendSide(Side.RIGHT);
        ticketPriorityBreakdownPieChart.getData().forEach((data) -> {
            data.nameProperty().bind(Bindings.concat(data.getName(), " - ", 
                                                     data.pieValueProperty().intValue(), 
                                                     ", ", 
                                                     String.format("%.1f%%", data.pieValueProperty().doubleValue()/(double) report.getTotalTickets() * 100)));
        });
    }
    
    private void loadStatusAging() { 
        newTicketAgingLabel.setText(createAgingLabelCaption(report.getNewTicketAgeInSeconds()));
        openTicketAgingLabel.setText(createAgingLabelCaption(report.getOpenTicketAgeInSeconds()));
        resolvedTicketAgingLabel.setText(createAgingLabelCaption(report.getResolvedTicketAgeInSeconds()));
        onHoldTicketAgingLabel.setText(createAgingLabelCaption(report.getOnHoldTicketAgeInSeconds()));
    }
    
    private void loadPriorityAging() {
        criticalTicketAgingLabel.setText(createAgingLabelCaption(report.getCriticalTicketAgeInSeconds()));
        urgentTicketAgingLabel.setText(createAgingLabelCaption(report.getUrgentTicketAgeInSeconds()));
        normalTicketAgingLabel.setText(createAgingLabelCaption(report.getNormalTicketAgeInSeconds()));
        lowTicketAgingLabel.setText(createAgingLabelCaption(report.getLowTicketAgeInSeconds()));
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
