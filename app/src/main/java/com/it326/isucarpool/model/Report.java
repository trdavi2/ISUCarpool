package com.it326.isucarpool.model;

/**
 * Created by Ross on 12/1/2016.
 */
public class Report {
    private String reporterId;
    private String reportedId;
    private String message;

    public Report() {
        setReporterId("");
        setReportedId("");
        setMessage("");
    }

    public Report(String reporterId, String reportedId, String message) {
        setReporterId(reporterId);
        setReportedId(reportedId);
        setMessage(message);
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public String getReportedId() {
        return reportedId;
    }

    public void setReportedId(String reportedId) {
        this.reportedId = reportedId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

