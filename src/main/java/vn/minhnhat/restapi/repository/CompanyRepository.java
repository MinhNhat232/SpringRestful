package vn.minhnhat.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.minhnhat.restapi.domain.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {
    // JpaRepository provides basic CRUD operations
    // JpaSpecificationExecutor allows for complex queries using Specifications
    // No additional methods are needed unless specific queries are required

}
