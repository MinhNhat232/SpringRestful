package vn.minhnhat.restapi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import jakarta.validation.Valid;
import vn.minhnhat.restapi.domain.Company;
import vn.minhnhat.restapi.domain.Job;
import vn.minhnhat.restapi.domain.Resume;
import vn.minhnhat.restapi.domain.User;
import vn.minhnhat.restapi.domain.response.ResultPaginationDTO;
import vn.minhnhat.restapi.domain.resume.ResCreateResumeDTO;
import vn.minhnhat.restapi.domain.resume.ResGetResumeDTO;
import vn.minhnhat.restapi.domain.resume.ResUpdateResumeDTO;
import vn.minhnhat.restapi.service.ResumeService;
import vn.minhnhat.restapi.service.UserService;
import vn.minhnhat.restapi.util.SecurityUtil;
import vn.minhnhat.restapi.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    private final ResumeService resumeService;
    private final UserService userService;
    private final FilterBuilder filterBuilder;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeController(ResumeService resumeService, UserService userService,
            FilterBuilder filterBuilder, FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    @PostMapping("/resumes")
    public ResponseEntity<ResCreateResumeDTO> createResume(@Valid @RequestBody Resume resume)
            throws IdInvalidException {

        boolean checkResume = this.resumeService.checkResumeExistByUserAndJob(resume);

        if (!checkResume) {
            throw new IdInvalidException("User/ Job invalid");

        }

        Resume resumeUser = this.resumeService.handleSaveResume(resume);

        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.create(resumeUser));
    }

    @PutMapping("/resumes")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume res) throws IdInvalidException {
        Optional<Resume> currentRes = this.resumeService.fetchById(res.getId());
        if (currentRes.isEmpty()) {
            throw new IdInvalidException("Invalid id");
        }
        Resume reqResume = currentRes.get();
        reqResume.setStatus(res.getStatus());

        return ResponseEntity.ok().body(this.resumeService.update(reqResume));
    }

    @DeleteMapping("/resumes/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> currentRes = this.resumeService.fetchById(id);
        if (currentRes == null) {
            throw new IdInvalidException("Invalid id");
        }

        this.resumeService.deleteResume(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes")
    public ResponseEntity<ResultPaginationDTO> fetchAll(@Filter Specification<Resume> spec, Pageable pageable) {

        List<Long> arrJobId = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User currentUser = this.userService.findByUsername(email);
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> jobs = userCompany.getJobs();
                if (jobs != null && jobs.size() > 0) {
                    arrJobId = jobs.stream().map(x -> x.getId()).collect(Collectors.toList());
                }
            }
        }

        Specification<Resume> jobInSpec = filterSpecificationConverter
                .convert(filterBuilder.field("job").in(filterBuilder.input(arrJobId)).get());

        Specification<Resume> finalSpec = jobInSpec.and(spec);
        return ResponseEntity.ok().body(this.resumeService.getAllResumes(finalSpec, pageable));
    }

    @GetMapping("/resumes/{id}")
    public ResponseEntity<ResGetResumeDTO> getResumeById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> res = this.resumeService.fetchById(id);
        if (res == null) {
            throw new IdInvalidException("Resume not exists");
        }

        return ResponseEntity.ok().body(this.resumeService.getResume(res.get()));

    }

    @PostMapping("/resumes/by-user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable) {
        // TODO: process POST request

        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }

}
