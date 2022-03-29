package com.medicensoya.coronavirustracker.controllers;

import com.medicensoya.coronavirustracker.services.CovidDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CovidDataController {

    private final CovidDataService covidDataService;


    @Autowired
    public CovidDataController(CovidDataService covidDataService) {
        this.covidDataService = covidDataService;
    }


    @GetMapping("/")
    public String homeFrontPage(Model model) {
        model.addAttribute("locationsData", covidDataService.getLocationsData());
        model.addAttribute("totalReportedCases", covidDataService.getTheSumOfTotalCases(covidDataService.getLocationsData()));
        model.addAttribute("totalNewCases", covidDataService.getTotalNewCases(covidDataService.getLocationsData()));
        return "home";
    }
}
