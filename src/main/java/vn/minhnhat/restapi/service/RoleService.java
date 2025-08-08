package vn.minhnhat.restapi.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.minhnhat.restapi.domain.Permission;
import vn.minhnhat.restapi.domain.Role;
import vn.minhnhat.restapi.domain.response.ResultPaginationDTO;
import vn.minhnhat.restapi.repository.PermissionRepository;
import vn.minhnhat.restapi.repository.RoleRepository;

@Service
public class RoleService {

    private RoleRepository roleRepository;
    private PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean isNameExist(String name) {
        return this.roleRepository.existsByName(name);

    }

    public Role findRoleById(long id) {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if (roleOptional.isPresent()) {
            return roleOptional.get();
        }
        return null;
    }

    public ResultPaginationDTO fetchAllRole(Specification<Role> spec, Pageable pageable) {
        Page<Role> pRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pRole.getTotalPages());
        meta.setTotal(pRole.getTotalElements());
        result.setMeta(meta);
        result.setResult(pRole.getContent());
        return result;
    }

    public void handleDeleteRole(long id) {
        this.roleRepository.deleteById(id);
    }

    public Role create(Role r) {
        if (r.getPermissions() != null) {
            List<Long> reqPermissions = r.getPermissions()
                    .stream().map(x -> x.getId()).collect(Collectors.toList());

            List<Permission> dbPer = this.permissionRepository.findByIdIn(reqPermissions);
            r.setPermissions(dbPer);
        }
        return this.roleRepository.save(r);
    }

    public Role update(Role r) {
        Role roleDB = this.findRoleById(r.getId());

        if (r.getPermissions() != null) {
            List<Long> reqPermissions = r.getPermissions()
                    .stream().map(x -> x.getId()).collect(Collectors.toList());

            List<Permission> dbPer = this.permissionRepository.findByIdIn(reqPermissions);
            r.setPermissions(dbPer);
        }

        roleDB.setName(r.getName());
        roleDB.setDescription(r.getDescription());
        roleDB.setActive(r.isActive());
        roleDB.setPermissions(r.getPermissions());
        roleDB = this.roleRepository.save(roleDB);
        return roleDB;
    }

}
