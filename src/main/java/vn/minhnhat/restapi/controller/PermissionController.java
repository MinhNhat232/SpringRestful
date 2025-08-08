package vn.minhnhat.restapi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.minhnhat.restapi.domain.Permission;
import vn.minhnhat.restapi.domain.Skill;
import vn.minhnhat.restapi.domain.response.ResultPaginationDTO;
import vn.minhnhat.restapi.service.PermissionService;
import vn.minhnhat.restapi.util.annotation.ApiMessage;
import vn.minhnhat.restapi.util.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    public ResponseEntity<Permission> createPermission(@RequestBody Permission per) throws IdInvalidException {
        boolean isExists = this.permissionService.isNameExist(per.getName());
        if (isExists) {
            throw new IdInvalidException("Permission already exist");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.handleSavePer(per));
    }

    @PutMapping("/permissions")
    public ResponseEntity<Permission> updatePermission(@RequestBody Permission permission) throws IdInvalidException {
        Permission currentPer = this.permissionService.findPerById(permission.getId());
        if (currentPer == null) {
            throw new IdInvalidException("Permission not exists");
        }

        if (permission.getName() != null && this.permissionService.isNameExist(permission.getName())) {
            throw new IdInvalidException("Permission already exist");
        }

        currentPer.setName(permission.getName());
        currentPer.setApiPath(permission.getApiPath());
        currentPer.setMethod(permission.getMethod());
        currentPer.setModule(permission.getModule());

        return ResponseEntity.status(HttpStatus.OK).body(this.permissionService.update(currentPer));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete successfully")
    public ResponseEntity<Void> deletePermissions(@PathVariable("id") long id) throws IdInvalidException {
        Permission currentPermissions = this.permissionService.findPerById(id);
        if (currentPermissions == null) {
            throw new IdInvalidException("Permissions with id " + id + " does not exist");
        }
        this.permissionService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/permissions/{id}")
    public ResponseEntity<Permission> getPermissionById(@PathVariable("id") long id) throws IdInvalidException {
        Permission per = this.permissionService.findPerById(id);
        if (per == null) {
            throw new IdInvalidException("Permission ID not exist");
        }

        return ResponseEntity.ok(per);
    }

    @GetMapping("/permissions")
    public ResponseEntity<ResultPaginationDTO> getAllPermission(@Filter Specification<Permission> spec,
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.permissionService.fetchAllPermission(spec, pageable));
    }

}
