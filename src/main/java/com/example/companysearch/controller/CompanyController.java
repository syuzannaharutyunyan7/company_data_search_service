package com.example.companysearch.controller;

import com.example.companysearch.model.Company;
import com.example.companysearch.service.CompanyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/search")
    public List<Company> searchCompanies(@RequestParam String query) {
        return companyService.search(query);
    }
}