package vn.minhnhat.restapi.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.minhnhat.restapi.domain.Skill;
import vn.minhnhat.restapi.domain.response.ResultPaginationDTO;
import vn.minhnhat.restapi.repository.SkillRepository;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public boolean isNameExist(String name) {
        return this.skillRepository.existsByName(name);

    }

    public Skill fetchSkillById(long id) {
        Optional<Skill> optionalSkill = this.skillRepository.findById(id);
        if (optionalSkill.isPresent())
            return optionalSkill.get();
        return null;
    }

    public Skill createSkill(Skill s) {
        return this.skillRepository.save(s);
    }

    public Skill updateSkill(Skill s) {
        return this.skillRepository.save(s);
    }

    public void deleteSkill(long id) {
        Optional<Skill> optionalSkill = this.skillRepository.findById(id);
        Skill currentSkill = optionalSkill.get();
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));
        this.skillRepository.delete(currentSkill);
    }

    public ResultPaginationDTO fetchAllSkill(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pSkill = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pSkill.getTotalPages());
        meta.setTotal(pSkill.getTotalElements());
        result.setMeta(meta);
        result.setResult(pSkill.getContent());
        return result;
    }
}
