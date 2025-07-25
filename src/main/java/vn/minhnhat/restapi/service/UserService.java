package vn.minhnhat.restapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.minhnhat.restapi.domain.User;
import vn.minhnhat.restapi.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleSaveUser(User user) {
        // Here you can add any business logic before saving the user
        return this.userRepository.save(user);
    }

    public void handleDeleteUser(long id) {
        // Here you can add any business logic before deleting the user
        this.userRepository.deleteById(id);
    }

    public User getUserById(long id) {
        // Here you can add any business logic before retrieving the user
        return this.userRepository.findById(id).orElse(null);
    }

    public List<User> getAllUsers(User user) {
        // Here you can add any business logic before retrieving all users
        return this.userRepository.findAll();
    }

    public User findByUsername(String username) {
        // Here you can add any business logic before finding the user by email
        return this.userRepository.findByEmail(username);
    }
}
