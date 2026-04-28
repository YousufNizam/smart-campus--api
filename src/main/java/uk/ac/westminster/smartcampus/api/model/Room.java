package uk.ac.westminster.smartcampus.api.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Room {
    private String id;
    private String name;
    private String building;
    private String floor;
    private Integer capacity;
    private Map<String, String> metadata = new LinkedHashMap<>();

    public Room() {
    }

    public Room(String id, String name, String building, String floor, Integer capacity) {
        this.id = id;
        this.name = name;
        this.building = building;
        this.floor = floor;
        this.capacity = capacity;
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

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
