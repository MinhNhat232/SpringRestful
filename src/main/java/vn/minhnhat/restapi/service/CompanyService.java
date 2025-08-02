package vn.minhnhat.restapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.minhnhat.restapi.domain.Company;
import vn.minhnhat.restapi.domain.response.ResultPaginationDTO;
import vn.minhnhat.restapi.repository.CompanyRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleSaveCompany(Company company) {
        // Here you can add any business logic before saving the company
        return this.companyRepository.save(company);
    }

    public Company getCompanyById(long id) {
        // Here you can add any business logic before retrieving the company
        return this.companyRepository.findById(id).orElse(null);
    }

    public ResultPaginationDTO getAllCompanies(Specification<Company> spec, Pageable pageable) {
        Page<Company> pCompany = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pCompany.getTotalPages());
        meta.setTotal(pCompany.getTotalElements());
        result.setMeta(meta);
        result.setResult(pCompany.getContent());
        return result;
    }

    public void handleDeleteCompany(long id) {
        // Here you can add any business logic before deleting the company
        this.companyRepository.deleteById(id);
    }
}
