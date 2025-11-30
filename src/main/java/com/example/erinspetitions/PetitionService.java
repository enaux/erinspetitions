package com.example.erinspetitions;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class PetitionService {

    private final List<Petition> petitions = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong();

    // Constructor with 3 environmental petitions as required
    public PetitionService() {
        // Initialize with 3 environmental petitions
        save(new Petition(counter.incrementAndGet(),
                "Ban Single-Use Plastics in Our City",
                "We urge the city council to implement a complete ban on single-use plastics including bags, straws, and food containers to reduce plastic pollution in our waterways and landfills."));

        save(new Petition(counter.incrementAndGet(),
                "Protect Old-Growth Forests from Logging",
                "Stop the clear-cutting of ancient forests that are vital for biodiversity, carbon sequestration, and maintaining ecological balance. These forests are irreplaceable."));

        save(new Petition(counter.incrementAndGet(),
                "Invest in Renewable Energy Infrastructure",
                "Demand increased public investment in solar, wind, and geothermal energy projects to transition away from fossil fuels and combat climate change effectively."));
    }

    public List<Petition> findAll() {
        return new ArrayList<>(petitions);
    }

    public void save(Petition petition) {
        if (petition.getId() == null) {
            petition.setId(counter.incrementAndGet());
        }
        // Simple "update" logic: remove old, add new
        petitions.removeIf(p -> p.getId().equals(petition.getId()));
        petitions.add(petition);
    }

    public Petition findById(Long id) {
        return petitions.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Search method for the search functionality
    public List<Petition> search(String query) {
        return petitions.stream()
                .filter(p -> p.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        p.getDescription().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

}
