package uk.ac.westminster.smartcampus.api.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class DiscoveryResponse {
    private String name;
    private String version;
    private String description;
    private String administrativeContact;
    private Map<String, String> collections = new LinkedHashMap<>();

    public DiscoveryResponse() {
    }

    public DiscoveryResponse(String name, String version, String description, String administrativeContact,
                             Map<String, String> collections) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.administrativeContact = administrativeContact;
        this.collections = collections;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdministrativeContact() {
        return administrativeContact;
    }

    public void setAdministrativeContact(String administrativeContact) {
        this.administrativeContact = administrativeContact;
    }

    public Map<String, String> getCollections() {
        return collections;
    }

    public void setCollections(Map<String, String> collections) {
        this.collections = collections;
    }
}
