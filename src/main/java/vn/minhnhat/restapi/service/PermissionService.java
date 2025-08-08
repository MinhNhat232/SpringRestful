package vn.minhnhat.restapi.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.minhnhat.restapi.domain.Permission;
import vn.minhnhat.restapi.domain.Skill;
import vn.minhnhat.restapi.domain.response.ResultPaginationDTO;
import vn.minhnhat.restapi.repository.PermissionRepository;

@Service
public class PermissionService {

    private final PermissionRepository perRepo;

    public PermissionService(PermissionRepository perRepo) {
        this.perRepo = perRepo;
    }

    public boolean isNameExist(String name) {
        return this.perRepo.existsByName(name);

    }

    public Permission handleSavePer(Permission p) {
        return this.perRepo.save(p);
    }

    public Permission update(Permission p) {
        return this.perRepo.save(p);
    }

    public Permission findPerById(long id) {
        Optional<Permission> perOptional = this.perRepo.findById(id);
        if (perOptional.isPresent()) {
            return perOptional.get();
        }
        return null;
    }

    public void delete(long id) {
        Optional<Permission> optionalPer = this.perRepo.findById(id);
        Permission currentPer = optionalPer.get();
        currentPer.getRoles().forEach(role -> role.getPermissions().remove(currentPer));
        this.perRepo.delete(currentPer);
    }

    public ResultPaginationDTO fetchAllPermission(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pPermission = this.perRepo.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pPermission.getTotalPages());
        meta.setTotal(pPermission.getTotalElements());
        result.setMeta(meta);
        result.setResult(pPermission.getContent());
        return result;
    }

}
