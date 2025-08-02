package vn.minhnhat.restapi.domain.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.minhnhat.restapi.util.constant.GenderEnum;

@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updatedAt;
}
