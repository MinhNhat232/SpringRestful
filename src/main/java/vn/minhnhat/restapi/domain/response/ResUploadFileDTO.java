package vn.minhnhat.restapi.domain.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResUploadFileDTO {
    private String name;
    private Instant uploadedAt;
}
