package vn.minhnhat.restapi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.minhnhat.restapi.domain.User;
import vn.minhnhat.restapi.domain.request.ReqLoginDTO;
import vn.minhnhat.restapi.domain.response.ResLoginDTO;
import vn.minhnhat.restapi.service.UserService;
import vn.minhnhat.restapi.util.SecurityUtil;
import vn.minhnhat.restapi.util.annotation.ApiMessage;
import vn.minhnhat.restapi.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    @Value("${mn7.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResLoginDTO resLoginDTO = new ResLoginDTO();

        User currentUserDB = this.userService.findByUsername(loginDTO.getUsername());
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());
            resLoginDTO.setUser(userLogin);
        } else {
            return ResponseEntity.status(404).body(null); // User not found
        }

        // Create access token
        String accessToken = this.securityUtil.createAccessToken(authentication.getName(), resLoginDTO.getUser());

        resLoginDTO.setAccessToken(accessToken);

        // Create refresh token
        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), resLoginDTO);

        // update token
        this.userService.updateUserToken(loginDTO.getUsername(), refreshToken);

        // Set cookies
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", refreshToken)

                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(resLoginDTO);
    }

    @GetMapping("/auth/account")
    public ResponseEntity<ResLoginDTO.UserLogin> getCurrentUser() {
        String currentUserEmail = SecurityUtil.getCurrentUserLogin().orElse(null);
        if (currentUserEmail == null) {
            return ResponseEntity.status(401).body(null); // Unauthorized
        }

        User currentUser = this.userService.findByUsername(currentUserEmail);
        if (currentUser == null) {
            return ResponseEntity.status(404).body(null); // User not found
        }

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        userLogin.setId(currentUser.getId());
        userLogin.setEmail(currentUser.getEmail());
        userLogin.setName(currentUser.getName());

        return ResponseEntity.ok().body(userLogin);
    }

    @GetMapping("/auth/refresh")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refreshToken", defaultValue = "abc") String refreshToken) throws IdInvalidException {

        if (refreshToken.equals("abc")) {
            throw new IdInvalidException("You dont have refresh token in cookies");
        }
        // Check valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();

        // Check user by token + email
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refreshToken, email);
        if (currentUser == null) {
            throw new IdInvalidException("Refresh token invalid");
        }

        ResLoginDTO resLoginDTO = new ResLoginDTO();

        User currentUserDB = this.userService.findByUsername(email);
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());
            resLoginDTO.setUser(userLogin);
        } else {
            return ResponseEntity.status(404).body(null); // User not found
        }

        // Create access token
        String accessToken = this.securityUtil.createAccessToken(email, resLoginDTO.getUser());

        resLoginDTO.setAccessToken(accessToken);

        // Create refresh token
        String new_refreshToken = this.securityUtil.createRefreshToken(email, resLoginDTO);

        // update token
        this.userService.updateUserToken(email, new_refreshToken);

        // Set cookies
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", new_refreshToken)

                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(resLoginDTO);
    }

    @GetMapping("/auth/logout")
    @ApiMessage("Logout and clear refresh token")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String currentEmail = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        if (currentEmail.equals("")) {
            throw new IdInvalidException("User not found");
        }
        this.userService.updateUserToken(currentEmail, null);

        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", null)

                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .build();
    }
}
