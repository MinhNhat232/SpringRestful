package vn.minhnhat.restapi.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResLoginDTO {
    private String accessToken;
    private UserLogin user;

    @Getter
    @Setter
    public static class UserLogin {
        private long id;
        private String email;
        private String name;
    }

}
