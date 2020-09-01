package com.nirzhor.coronatrack.Coronavirustracker.controllers;


import com.nirzhor.coronatrack.Coronavirustracker.models.LocationStats;
import com.nirzhor.coronatrack.Coronavirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.DecimalFormat;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model) {

        List<LocationStats> allStatesConfirmed = coronaVirusDataService.getAllStatsConfirmed();
        int totalReportedCases =  allStatesConfirmed.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCases =  allStatesConfirmed.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();

        DecimalFormat displayFormatter = new DecimalFormat("###,###");
        String totalReportedCasesDisplay = displayFormatter.format(totalReportedCases);
        String totalNewCasesDisplay = displayFormatter.format(totalNewCases);

        model.addAttribute("locationStats",allStatesConfirmed);
        model.addAttribute("totalReportedCases",totalReportedCasesDisplay);
        model.addAttribute("totalNewCases",totalNewCasesDisplay);



        return "home";
    }
}
