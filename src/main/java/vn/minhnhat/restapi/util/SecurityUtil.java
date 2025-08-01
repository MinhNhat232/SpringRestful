package vn.minhnhat.restapi.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.nimbusds.jose.util.Base64;

import vn.minhnhat.restapi.domain.dto.ResLoginDTO;

@Service
public class SecurityUtil {

    private final JwtEncoder jwtEncoder;

    public SecurityUtil(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS256;

    @Value("${mn7.jwt.base64-secret}")
    private String jwtKey;

    @Value("${mn7.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @Value("${mn7.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public String createAccessToken(String email, ResLoginDTO.UserLogin dto) {

        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);

        // hardcode permissions
        List<String> listAuthority = new ArrayList<String>();

        listAuthority.add("ROLE_USER_CREATE");
        listAuthority.add("ROLE_USER_UPDATE");

// @formatter:off
JwtClaimsSet claims = JwtClaimsSet.builder()
    .issuedAt(now)
    .expiresAt(validity)
    .subject(email)
    .claim("user", dto)
    .claim("permissions", listAuthority)
    .claim("exp", validity)
    .build();

JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();

    }

    public String createRefreshToken(String email,ResLoginDTO dto) {

        Instant now = Instant.now();
        Instant validity = now.plus(this.refreshTokenExpiration, ChronoUnit.SECONDS);

// @formatter:off
JwtClaimsSet claims = JwtClaimsSet.builder()
    .issuedAt(now)
    .expiresAt(validity)
    .subject(email)
    .claim("user", dto.getUser())
    .build();

JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();

    }

    public static Optional<String> getCurrentUserLogin() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
}

private static String extractPrincipal(Authentication authentication) {
    if (authentication == null) {
        return null;
    } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
        return springSecurityUser.getUsername();
    } else if (authentication.getPrincipal() instanceof Jwt jwt) {
        return jwt.getSubject();
    } else if (authentication.getPrincipal() instanceof String s) {
        return s;
    }
    return null;
}

public static Optional<String> getCurrentUserJWT() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    return Optional.ofNullable(securityContext.getAuthentication())
        .filter(authentication -> authentication.getCredentials() instanceof String)
        .map(authentication -> (String) authentication.getCredentials());
}

private SecretKey getSecretKey(){
    byte[] keyBytes = Base64.from(jwtKey).decode();
    return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
}

public Jwt checkValidRefreshToken(String token){
    NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
            getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
            try{
        return jwtDecoder.decode(token);
            }catch (Exception e) {
                System.out.println(">>> Refresh token error: " + e.getMessage());
                throw e;
            }
}




}
