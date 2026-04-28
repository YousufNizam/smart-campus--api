package uk.ac.westminster.smartcampus.api.service;

import uk.ac.westminster.smartcampus.api.exception.InvalidSensorDependencyException;
import uk.ac.westminster.smartcampus.api.exception.RoomConflictException;
import uk.ac.westminster.smartcampus.api.exception.SensorMaintenanceException;
import uk.ac.westminster.smartcampus.api.model.Room;
import uk.ac.westminster.smartcampus.api.model.Sensor;
import uk.ac.westminster.smartcampus.api.model.SensorReading;
import uk.ac.westminster.smartcampus.api.model.SensorStatus;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public final class SmartCampusStore {

    private static final SmartCampusStore INSTANCE = new SmartCampusStore();

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Map<String, CopyOnWriteArrayList<SensorReading>> readings = new ConcurrentHashMap<>();
    private final AtomicLong roomSequence = new AtomicLong(1000);
    private final AtomicLong sensorSequence = new AtomicLong(2000);
    private final AtomicLong readingSequence = new AtomicLong(3000);
    private final Object mutationLock = new Object();

    private SmartCampusStore() {
        seedData();
    }

    public static SmartCampusStore getInstance() {
        return INSTANCE;
    }

    public List<Room> getAllRooms() {
        return rooms.values().stream()
                .sorted(Comparator.comparing(Room::getId))
                .collect(Collectors.toList());
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public Room createRoom(Room incoming) {
        synchronized (mutationLock) {
            String roomId = normalizeId(incoming.getId(), "room-" + roomSequence.incrementAndGet());
            if (rooms.containsKey(roomId)) {
                throw new WebApplicationException("Room '" + roomId + "' already exists.", Response.Status.CONFLICT);
            }

            Room room = new Room(roomId, incoming.getName(), incoming.getBuilding(), incoming.getFloor(), incoming.getCapacity());
            if (incoming.getMetadata() != null) {
                room.setMetadata(new ConcurrentHashMap<>(incoming.getMetadata()));
            }
            rooms.put(room.getId(), room);
            return room;
        }
    }

    public void deleteRoom(String roomId) {
        synchronized (mutationLock) {
            Room existing = rooms.get(roomId);
            if (existing == null) {
                throw new NotFoundException("Room '" + roomId + "' was not found.");
            }

            boolean hasActiveSensors = sensors.values().stream()
                    .anyMatch(sensor -> roomId.equals(sensor.getRoomId()));
            if (hasActiveSensors) {
                throw new RoomConflictException(
                        "Room '" + roomId + "' cannot be deleted because active sensors are still assigned to it.");
            }

            rooms.remove(roomId);
        }
    }

    public List<Sensor> getSensors(String status) {
        return sensors.values().stream()
                .filter(sensor -> status == null || sensor.getStatus().equalsIgnoreCase(status))
                .sorted(Comparator.comparing(Sensor::getId))
                .collect(Collectors.toList());
    }

    public Sensor getSensor(String sensorId) {
        return sensors.get(sensorId);
    }

    public Sensor createSensor(Sensor incoming) {
        synchronized (mutationLock) {
            if (!rooms.containsKey(incoming.getRoomId())) {
                throw new InvalidSensorDependencyException(
                        "Cannot create sensor because room '" + incoming.getRoomId() + "' does not exist.");
            }

            String sensorId = normalizeId(incoming.getId(), "sensor-" + sensorSequence.incrementAndGet());
            if (sensors.containsKey(sensorId)) {
                throw new WebApplicationException("Sensor '" + sensorId + "' already exists.", Response.Status.CONFLICT);
            }

            String status = incoming.getStatus() == null ? SensorStatus.ACTIVE : incoming.getStatus().toUpperCase(Locale.ROOT);
            Sensor sensor = new Sensor(sensorId, incoming.getName(), incoming.getType(), status, incoming.getRoomId());
            sensors.put(sensor.getId(), sensor);
            readings.put(sensor.getId(), new CopyOnWriteArrayList<>());
            return sensor;
        }
    }

    public void deleteSensor(String sensorId) {
        synchronized (mutationLock) {
            Sensor existing = sensors.remove(sensorId);
            if (existing == null) {
                throw new NotFoundException("Sensor '" + sensorId + "' was not found.");
            }
            readings.remove(sensorId);
        }
    }

    public List<SensorReading> getReadingsForSensor(String sensorId) {
        ensureSensorExists(sensorId);
        return new ArrayList<>(readings.getOrDefault(sensorId, new CopyOnWriteArrayList<>()));
    }

    public SensorReading addReading(String sensorId, SensorReading incoming) {
        synchronized (mutationLock) {
            Sensor sensor = ensureSensorExists(sensorId);
            if (SensorStatus.MAINTENANCE.equalsIgnoreCase(sensor.getStatus())) {
                throw new SensorMaintenanceException(
                        "Sensor '" + sensorId + "' is in MAINTENANCE and cannot accept new readings.");
            }

            SensorReading reading = new SensorReading(
                    normalizeId(incoming.getId(), "reading-" + readingSequence.incrementAndGet()),
                    incoming.getValue(),
                    incoming.getUnit(),
                    normalizeRecordedAt(incoming.getRecordedAt()),
                    incoming.getNotes()
            );

            readings.computeIfAbsent(sensorId, ignored -> new CopyOnWriteArrayList<>()).add(reading);
            sensor.setLatestReading(reading);
            return reading;
        }
    }

    private Sensor ensureSensorExists(String sensorId) {
        Sensor sensor = sensors.get(sensorId);
        if (sensor == null) {
            throw new NotFoundException("Sensor '" + sensorId + "' was not found.");
        }
        return sensor;
    }

    private String normalizeId(String providedId, String generatedId) {
        return providedId == null || providedId.trim().isEmpty() ? generatedId : providedId.trim();
    }

    private String normalizeRecordedAt(String recordedAt) {
        return recordedAt == null || recordedAt.trim().isEmpty() ? Instant.now().toString() : recordedAt.trim();
    }

    private void seedData() {
        Room engineering = new Room("room-a101", "Embedded Systems Lab", "Cavendish", "1", 40);
        engineering.getMetadata().put("department", "Engineering");
        rooms.put(engineering.getId(), engineering);

        Room library = new Room("room-b205", "Quiet Study Room", "New Cavendish", "2", 24);
        library.getMetadata().put("department", "Library");
        rooms.put(library.getId(), library);

        Sensor co2 = new Sensor("sensor-co2-01", "CO2 Monitor 01", "CO2", SensorStatus.ACTIVE, engineering.getId());
        Sensor occupancy = new Sensor("sensor-occ-01", "Occupancy Tracker 01", "OCCUPANCY", SensorStatus.MAINTENANCE, library.getId());
        sensors.put(co2.getId(), co2);
        sensors.put(occupancy.getId(), occupancy);

        readings.put(co2.getId(), new CopyOnWriteArrayList<>());
        readings.put(occupancy.getId(), new CopyOnWriteArrayList<>());

        SensorReading initialReading = new SensorReading("reading-3001", 415.2, "ppm", Instant.now().minusSeconds(3600).toString(),
                "Initial seed reading");
        readings.get(co2.getId()).add(initialReading);
        co2.setLatestReading(initialReading);

        roomSequence.set(Math.max(roomSequence.get(), 1002));
        sensorSequence.set(Math.max(sensorSequence.get(), 2002));
        readingSequence.set(Math.max(readingSequence.get(), 3001));
    }
}
