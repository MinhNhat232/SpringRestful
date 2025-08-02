package vn.minhnhat.restapi.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.minhnhat.restapi.domain.Company;
import vn.minhnhat.restapi.domain.User;
import vn.minhnhat.restapi.domain.dto.ResultPaginationDTO;
import vn.minhnhat.restapi.service.CompanyService;
import vn.minhnhat.restapi.util.annotation.ApiMessage;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable("id") long id) {
        Company company = this.companyService.getCompanyById(id);
        if (company != null) {
            return ResponseEntity.ok(company);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/companies")
    @ApiMessage("List of companies retrieved successfully")
    public ResponseEntity<ResultPaginationDTO> fetchAllCompanies(@Filter Specification<Company> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.companyService.getAllCompanies(spec, pageable));
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@RequestBody Company company) {
        // TODO: process POST request
        Company createdCompany = this.companyService.handleSaveCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCompany);
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<String> deleteCompany(@PathVariable("id") long id) {
        this.companyService.handleDeleteCompany(id);
        return ResponseEntity.ok("Company deleted successfully");

    }

    @PutMapping("/companies/{id}")
    public ResponseEntity<Company> updateCompany(@PathVariable("id") long id, @RequestBody Company company) {
        Company existingCompany = this.companyService.getCompanyById(id);
        if (existingCompany != null) {
            existingCompany.setName(company.getName());
            existingCompany.setDescription(company.getDescription());
            existingCompany.setAddress(company.getAddress());
            existingCompany.setLogo(company.getLogo());
            Company updatedCompany = this.companyService.handleSaveCompany(existingCompany);
            return ResponseEntity.ok(updatedCompany);
        }
        return ResponseEntity.notFound().build(); // or throw an exception if company not found
    }

}
