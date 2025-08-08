package vn.minhnhat.restapi.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.minhnhat.restapi.domain.Permission;
import vn.minhnhat.restapi.domain.Role;
import vn.minhnhat.restapi.domain.response.ResultPaginationDTO;
import vn.minhnhat.restapi.service.RoleService;
import vn.minhnhat.restapi.util.annotation.ApiMessage;
import vn.minhnhat.restapi.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete successfully")
    public ResponseEntity<Void> deleteRoles(@PathVariable("id") long id) throws IdInvalidException {
        Role currentRole = this.roleService.findRoleById(id);
        if (currentRole == null) {
            throw new IdInvalidException("Permissions with id " + id + " does not exist");
        }
        this.roleService.handleDeleteRole(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable("id") long id) throws IdInvalidException {
        Role role = this.roleService.findRoleById(id);
        if (role == null) {
            throw new IdInvalidException("Permission ID not exist");
        }

        return ResponseEntity.ok(role);
    }

    @GetMapping("/roles")
    public ResponseEntity<ResultPaginationDTO> getAllRoles(@Filter Specification<Role> spec,
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.fetchAllRole(spec, pageable));
    }

    @PostMapping("/roles")
    public ResponseEntity<Role> createPermission(@RequestBody Role role) throws IdInvalidException {
        boolean isExists = this.roleService.isNameExist(role.getName());
        if (isExists) {
            throw new IdInvalidException("Role already exist");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(role));
    }

    @PutMapping("/roles")
    public ResponseEntity<Role> updatePermission(@RequestBody Role role) throws IdInvalidException {

        if (this.roleService.findRoleById(role.getId()) == null) {
            throw new IdInvalidException("Role not exists");
        }

        // if (this.roleService.isNameExist(role.getName())) {
        // throw new IdInvalidException("Role already exist");
        // }

        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.update(role));
    }

}
