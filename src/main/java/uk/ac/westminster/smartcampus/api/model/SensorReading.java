package uk.ac.westminster.smartcampus.api.model;

public class SensorReading {
    private String id;
    private Double value;
    private String unit;
    private String recordedAt;
    private String notes;

    public SensorReading() {
    }

    public SensorReading(String id, Double value, String unit, String recordedAt, String notes) {
        this.id = id;
        this.value = value;
        this.unit = unit;
        this.recordedAt = recordedAt;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(String recordedAt) {
        this.recordedAt = recordedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
