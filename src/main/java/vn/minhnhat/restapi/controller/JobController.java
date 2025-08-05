package vn.minhnhat.restapi.controller;

import java.util.Optional;

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
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.minhnhat.restapi.domain.Job;
import vn.minhnhat.restapi.domain.job.ResCreateJobDTO;
import vn.minhnhat.restapi.domain.job.ResUpdateJobDTO;
import vn.minhnhat.restapi.domain.response.ResultPaginationDTO;
import vn.minhnhat.restapi.service.JobService;
import vn.minhnhat.restapi.util.error.IdInvalidException;

@RestController
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> job = this.jobService.findJobById(id);
        if (job == null) {
            throw new IdInvalidException("Job not exists");
        }

        return ResponseEntity.ok(job.get());

    }

    @GetMapping("/jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJob(@Filter Specification<Job> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.fetchAllJob(spec, pageable));
    }

    @PostMapping("/jobs")
    public ResponseEntity<ResCreateJobDTO> createJob(@Valid @RequestBody Job job) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.create(job));
    }

    @PutMapping("/jobs")
    public ResponseEntity<ResUpdateJobDTO> updateJob(@Valid @RequestBody Job job) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.findJobById(job.getId());

        if (!currentJob.isPresent()) {
            throw new IdInvalidException("Job not found");
        }

        return ResponseEntity.ok().body(this.jobService.update(job));
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable("id") long id) throws IdInvalidException {

        Optional<Job> currentJob = this.jobService.findJobById(id);
        if (!currentJob.isPresent()) {
            throw new IdInvalidException("Job not found");
        }
        this.jobService.handleDeleteJob(id);
        return ResponseEntity.ok().body(null);

    }

}
