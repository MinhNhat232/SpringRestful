package vn.minhnhat.restapi.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.minhnhat.restapi.domain.Job;
import vn.minhnhat.restapi.domain.Skill;
import vn.minhnhat.restapi.domain.job.ResCreateJobDTO;
import vn.minhnhat.restapi.domain.job.ResUpdateJobDTO;
import vn.minhnhat.restapi.domain.response.ResultPaginationDTO;
import vn.minhnhat.restapi.repository.JobRepository;
import vn.minhnhat.restapi.repository.SkillRepository;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
    }

    public Optional<Job> findJobById(long id) {
        return this.jobRepository.findById(id);
    }

    public ResultPaginationDTO fetchAllJob(Specification<Job> spec, Pageable pageable) {
        Page<Job> pJob = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pJob.getTotalPages());
        meta.setTotal(pJob.getTotalElements());
        result.setMeta(meta);
        result.setResult(pJob.getContent());
        return result;
    }

    public ResCreateJobDTO create(Job j) {
        if (j.getSkills() != null) {
            List<Long> reqSkill = j.getSkills().stream().map(x -> x.getId()).collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkill);
            j.setSkills(dbSkills);
        }

        Job currentJob = this.jobRepository.save(j);

        // convert response
        ResCreateJobDTO dto = new ResCreateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setActive(currentJob.isActive());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setCreatedAt(currentJob.getCreatedAt());
        dto.setCreatedBy(currentJob.getCreatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills().stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }

        return dto;

    }

    public ResUpdateJobDTO update(Job j) {
        if (j.getSkills() != null) {
            List<Long> reqSkill = j.getSkills().stream().map(x -> x.getId()).collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkill);
            j.setSkills(dbSkills);
        }

        Job currentJob = this.jobRepository.save(j);

        // convert response
        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setActive(currentJob.isActive());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setUpdatedAt(currentJob.getUpdatedAt());
        dto.setUpdatedBy(currentJob.getUpdatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills().stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }

        return dto;

    }

    public void handleDeleteJob(long id) {
        this.jobRepository.deleteById(id);
    }

}
