package uk.ac.westminster.smartcampus.api.model;

public class Sensor {
    private String id;
    private String name;
    private String type;
    private String status;
    private String roomId;
    private SensorReading latestReading;

    public Sensor() {
    }

    public Sensor(String id, String name, String type, String status, String roomId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.roomId = roomId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public SensorReading getLatestReading() {
        return latestReading;
    }

    public void setLatestReading(SensorReading latestReading) {
        this.latestReading = latestReading;
    }
}
