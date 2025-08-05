package vn.minhnhat.restapi.domain.resume;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.minhnhat.restapi.util.constant.StatusEnum;

@Getter
@Setter
public class ResGetResumeDTO {
    private long id;
    private String email;
    private String url;
    private StatusEnum status;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private ResumeJob job;
    private ResumeUser user;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ResumeJob {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ResumeUser {
        private long id;
        private String name;
    }
}
