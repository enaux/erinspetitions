package com.example.erinspetitions;

import java.util.ArrayList;
import java.util.List;

public class Petition {

    private Long id;
    private String title;
    private String description;
    private List<Signatory> signatories;

    // Inner class for signatory with name and email
    public static class Signatory {
        private String name;
        private String email;

        public Signatory() {}

        public Signatory(String name, String email) {
            this.name = name;
            this.email = email;
        }

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        @Override
        public String toString() {
            return name; // This is what will be displayed in the view
        }
    }

    // Constructors
    public Petition() {
        this.signatories = new ArrayList<>();
    }

    public Petition(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.signatories = new ArrayList<>();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Signatory> getSignatories() { return signatories; }
    public void setSignatories(List<Signatory> signatories) { this.signatories = signatories; }

}
