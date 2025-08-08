package vn.minhnhat.restapi.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.minhnhat.restapi.domain.User;
import vn.minhnhat.restapi.domain.response.ResCreateUserDTO;
import vn.minhnhat.restapi.domain.response.ResUpdateUserDTO;
import vn.minhnhat.restapi.domain.response.ResUserDTO;
import vn.minhnhat.restapi.domain.response.ResultPaginationDTO;
import vn.minhnhat.restapi.service.UserService;
import vn.minhnhat.restapi.util.annotation.ApiMessage;
import vn.minhnhat.restapi.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users/{id}")
    @ApiMessage("User retrieved successfully")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
        User user = this.userService.getUserById(id);
        if (user == null) {
            throw new IdInvalidException("User with id " + id + " does not exist");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(user));
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all users successfully")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<User> spec, Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getAllUsers(spec, pageable));
    }

    @PostMapping("/users")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User user) throws IdInvalidException {

        boolean exists = this.userService.existsByEmail(user.getEmail());
        if (exists) {
            throw new IdInvalidException("Email " + user.getEmail() + " already exists");
        }
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);

        User mnUser = this.userService.handleSaveUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(mnUser));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("User deleted successfully")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {

        User currentUser = this.userService.getUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("User with id " + id + " does not exist");
        }

        this.userService.handleDeleteUser(id);

        return ResponseEntity.ok(null);
    }

    @PutMapping("/users")
    @ApiMessage("User updated successfully")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user)
            throws IdInvalidException {
        User existingUser = this.userService.handleUpdateUser(user);
        if (existingUser == null) {
            throw new IdInvalidException("User with id " + existingUser.getId() + " does not exist");
        }

        return ResponseEntity.ok(this.userService.convertToUpdateResUserDTO(existingUser));
    }
}
