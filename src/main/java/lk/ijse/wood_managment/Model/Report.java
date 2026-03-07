package lk.ijse.wood_managment.Model;

import java.time.LocalDate;

public class Report {

    private int reportId;
    private String reportType;
    private LocalDate reportDate;
    private String generatedBy;
    private String orderId;
    private String cuttingId;
    private String description;

    public Report(int reportId, String reportType, LocalDate reportDate, String generatedBy, String orderId, String cuttingId, String description) {
        this.reportId = reportId;
        this.reportType = reportType;
        this.reportDate = reportDate;
        this.generatedBy = generatedBy;
        this.orderId = orderId;
        this.cuttingId = cuttingId;
        this.description = description;
    }

    public Report() {}

    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }

    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCuttingId() { return cuttingId; }
    public void setCuttingId(String cuttingId) { this.cuttingId = cuttingId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
