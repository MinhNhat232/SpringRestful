package vn.minhnhat.restapi.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.minhnhat.restapi.domain.Job;
import vn.minhnhat.restapi.domain.Resume;
import vn.minhnhat.restapi.domain.User;
import vn.minhnhat.restapi.domain.response.ResultPaginationDTO;
import vn.minhnhat.restapi.domain.resume.ResCreateResumeDTO;
import vn.minhnhat.restapi.domain.resume.ResGetResumeDTO;
import vn.minhnhat.restapi.domain.resume.ResUpdateResumeDTO;
import vn.minhnhat.restapi.repository.JobRepository;
import vn.minhnhat.restapi.repository.ResumeRepository;
import vn.minhnhat.restapi.repository.UserRepository;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public ResumeService(ResumeRepository resumeRepository, JobRepository jobRepository,
            UserRepository userRepository) {
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    public boolean checkResumeExistByUserAndJob(Resume res) {
        if (res.getJobs() == null) {
            return false;
        }

        Optional<Job> jobOptional = this.jobRepository.findById(res.getJobs().getId());
        if (jobOptional.isEmpty()) {
            return false;
        }

        if (res.getUsers() == null) {
            return false;
        }

        Optional<User> userOptional = this.userRepository.findById(res.getUsers().getId());
        if (userOptional.isEmpty()) {
            return false;
        }
        return true;
    }

    public Resume handleSaveResume(Resume resume) {

        return this.resumeRepository.save(resume);
    }

    public ResCreateResumeDTO create(Resume resume) {
        ResCreateResumeDTO res = new ResCreateResumeDTO();

        res.setId(resume.getId());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());

        return res;

    }

    public ResUpdateResumeDTO update(Resume resume) {
        resume = this.resumeRepository.save(resume);
        ResUpdateResumeDTO dto = new ResUpdateResumeDTO();
        dto.setUpdatedAt(resume.getUpdatedAt());
        dto.setUpdatedBy(resume.getUpdatedBy());

        return dto;
    }

    public ResGetResumeDTO getResume(Resume resume) {
        ResGetResumeDTO res = new ResGetResumeDTO();

        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setUrl(resume.getUrl());
        res.setStatus(resume.getStatus());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());

        res.setUser(new ResGetResumeDTO.ResumeUser(
                resume.getUsers().getId(),
                resume.getUsers().getName()));
        res.setJob(new ResGetResumeDTO.ResumeJob(
                resume.getJobs().getId(),
                resume.getJobs().getName()));

        return res;
    }

    public ResultPaginationDTO getAllResumes(Specification<Resume> spec, Pageable pageable) {
        // Here you can add any business logic before retrieving all users
        Page<Resume> pResume = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pResume.getTotalPages());
        mt.setTotal(pResume.getTotalElements());

        result.setMeta(mt);

        // remove sensitive data
        List<ResGetResumeDTO> listResume = pResume.getContent()
                .stream()
                .map(item -> this.getResume(item))
                .collect(Collectors.toList());

        result.setResult(listResume);

        return result;

    }

    public Optional<Resume> fetchById(long id) {
        return this.resumeRepository.findById(id);
    }

    public void deleteResume(long id) {
        this.resumeRepository.deleteById(id);
    }

}
