package com.example.erinspetitions;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class PetitionController {

    private final PetitionService petitionService;

    public PetitionController(PetitionService petitionService) {
        this.petitionService = petitionService;
    }

    // Page to view all petitions
    @GetMapping("/")
    public String listPetitions(Model model) {
        model.addAttribute("petitions", petitionService.findAll());
        return "index"; // Maps to src/main/resources/templates/index.html
    }

    // Page to create a new petition
    @GetMapping("/create")
    public String createPetitionForm(Model model) {
        model.addAttribute("petition", new Petition());
        return "create-petition"; // Maps to create-petition.html
    }

    // Endpoint to handle form submission for creating a petition
    @PostMapping("/create")
    public String createPetition(@ModelAttribute Petition petition) {
        petitionService.save(petition);
        return "redirect:/"; // Go back to the home page
    }

    // Page to view a single petition and sign it
    @GetMapping("/petition/{id}")
    public String viewPetition(@PathVariable Long id, Model model) {
        Petition petition = petitionService.findById(id);
        if (petition == null) {
            return "redirect:/"; // Petition not found, go home
        }
        model.addAttribute("petition", petition);
        return "view-petition"; // Maps to view-petition.html
    }

    // Endpoint to handle signing a petition (with name AND email)
    @PostMapping("/petition/{id}/sign")
    public String signPetition(@PathVariable Long id,
                               @RequestParam String signatoryName,
                               @RequestParam String signatoryEmail) {
        Petition petition = petitionService.findById(id);
        if (petition != null && signatoryName != null && !signatoryName.trim().isEmpty()
                && signatoryEmail != null && !signatoryEmail.trim().isEmpty()) {

            Petition.Signatory newSignatory = new Petition.Signatory(
                    signatoryName.trim(),
                    signatoryEmail.trim()
            );
            petition.getSignatories().add(newSignatory);
            petitionService.save(petition);
        }
        return "redirect:/petition/" + id;
    }

    // Search form page
    @GetMapping("/search-page")
    public String searchPage() {
        return "search"; // Maps to search.html
    }

    // Search results page
    @GetMapping("/search")
    public String searchPetitions(@RequestParam String query, Model model) {
        List<Petition> results = petitionService.search(query);
        model.addAttribute("results", results);
        model.addAttribute("query", query);
        return "search-results"; // Maps to search-results.html
    }

}
