package vn.minhnhat.restapi.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.minhnhat.restapi.domain.Job;
import vn.minhnhat.restapi.domain.Skill;
import vn.minhnhat.restapi.domain.Subscriber;
import vn.minhnhat.restapi.domain.email.ResEmailJob;
import vn.minhnhat.restapi.repository.JobRepository;
import vn.minhnhat.restapi.repository.SkillRepository;
import vn.minhnhat.restapi.repository.SubscriberRepository;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository,
            JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    public boolean existsByEmail(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }

    public Subscriber create(Subscriber s) {
        if (s.getSkills() != null) {
            List<Long> reqSkill = s.getSkills().stream().map(x -> x.getId()).collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkill);
            s.setSkills(dbSkills);
        }
        return this.subscriberRepository.save(s);
    }

    public Subscriber update(Subscriber subDB, Subscriber subReq) {
        if (subReq.getSkills() != null) {
            List<Long> reqSkill = subReq.getSkills().stream().map(x -> x.getId()).collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkill);
            subDB.setSkills(dbSkills);
        }
        return this.subscriberRepository.save(subDB);
    }

    public Subscriber findById(long id) {
        Optional<Subscriber> optionalSubscriber = this.subscriberRepository.findById(id);
        if (optionalSubscriber.isPresent())
            return optionalSubscriber.get();
        return null;
    }

    private ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());

        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();

        List<ResEmailJob.SkillEmail> s = skills.stream().map(skill -> new ResEmailJob.SkillEmail(skill.getName()))
                .collect(Collectors.toList());

        res.setSkills(s);

        return res;
    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {

                        List<ResEmailJob> arr = listJobs.stream().map(
                                job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());

                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "test",
                                sub.getName(),
                                arr);
                    }
                }
            }
        }
    }

}
