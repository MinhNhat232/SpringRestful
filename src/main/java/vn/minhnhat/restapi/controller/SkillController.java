package vn.minhnhat.restapi.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.minhnhat.restapi.domain.Skill;
import vn.minhnhat.restapi.domain.response.ResultPaginationDTO;
import vn.minhnhat.restapi.service.SkillService;
import vn.minhnhat.restapi.util.annotation.ApiMessage;
import vn.minhnhat.restapi.util.error.IdInvalidException;

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
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill s) throws IdInvalidException {
        if (s.getName() != null && this.skillService.isNameExist(s.getName())) {
            throw new IdInvalidException("Skill already existed");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.createSkill(s));
    }

    @PutMapping("/skills")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill s) throws IdInvalidException {
        Skill currentSkill = this.skillService.fetchSkillById(s.getId());
        if (currentSkill == null) {
            throw new IdInvalidException("Skill not exists");
        }

        if (s.getName() != null && this.skillService.isNameExist(s.getName())) {
            throw new IdInvalidException("Skill already existed");
        }

        currentSkill.setName(s.getName());
        return ResponseEntity.ok().body(this.skillService.updateSkill(currentSkill));

    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete successfully")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id) throws IdInvalidException {
        Skill currentSkill = this.skillService.fetchSkillById(id);
        if (currentSkill == null) {
            throw new IdInvalidException("Skill with id " + id + " does not exist");
        }
        this.skillService.deleteSkill(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/skills/{id}")
    @ApiMessage("Skill retrieved successfully")
    public ResponseEntity<Skill> getSkillById(@PathVariable("id") long id) throws IdInvalidException {
        Skill skill = this.skillService.fetchSkillById(id);
        if (skill == null) {
            throw new IdInvalidException("Skill with id " + id + " does not exist");
        }
        return ResponseEntity.ok(skill);
    }

    @GetMapping("/skills")
    @ApiMessage("Fetch all skills successfully")
    public ResponseEntity<ResultPaginationDTO> getAllSkill(
            @Filter Specification<Skill> spec, Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(this.skillService.fetchAllSkill(spec, pageable));
    }

}
