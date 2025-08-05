package vn.minhnhat.restapi.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.minhnhat.restapi.domain.Company;
import vn.minhnhat.restapi.domain.User;
import vn.minhnhat.restapi.domain.response.ResCreateUserDTO;
import vn.minhnhat.restapi.domain.response.ResUpdateUserDTO;
import vn.minhnhat.restapi.domain.response.ResUserDTO;
import vn.minhnhat.restapi.domain.response.ResultPaginationDTO;
import vn.minhnhat.restapi.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final CompanyService companyService;

    public UserService(UserRepository userRepository, CompanyService companyService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
    }

    public User handleSaveUser(User user) {

        // check company
        if (user.getCompany() != null) {
            Optional<Company> company = Optional.of((this.companyService.getCompanyById(user.getCompany().getId())));
            user.setCompany(company.isPresent() ? company.get() : null);
        }

        return this.userRepository.save(user);
    }

    public void handleDeleteUser(long id) {
        // Here you can add any business logic before deleting the user
        this.userRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        // Here you can add any business logic before checking if the user exists by
        // email
        return this.userRepository.existsByEmail(email);
    }

    public User getUserById(long id) {
        // Here you can add any business logic before retrieving the user
        return this.userRepository.findById(id).orElse(null);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        // Convert User to ResCreateUserDTO
        ResCreateUserDTO resCreateUserDTO = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser companyUser = new ResCreateUserDTO.CompanyUser();
        resCreateUserDTO.setId(user.getId());
        resCreateUserDTO.setName(user.getName());
        resCreateUserDTO.setEmail(user.getEmail());
        resCreateUserDTO.setGender(user.getGender());
        resCreateUserDTO.setAddress(user.getAddress());
        resCreateUserDTO.setAge(user.getAge());
        resCreateUserDTO.setCreatedAt(user.getCreatedAt());
        if (user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            resCreateUserDTO.setCompany(companyUser);
        }
        return resCreateUserDTO;
    }

    public ResUpdateUserDTO convertToUpdateResUserDTO(User user) {
        // Convert User to ResUserDTO
        ResUpdateUserDTO resUpdateUserDTO = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser companyUser = new ResUpdateUserDTO.CompanyUser();
        if (user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            resUpdateUserDTO.setCompany(companyUser);
        }
        resUpdateUserDTO.setId(user.getId());
        resUpdateUserDTO.setName(user.getName());
        resUpdateUserDTO.setEmail(user.getEmail());
        resUpdateUserDTO.setGender(user.getGender());
        resUpdateUserDTO.setAddress(user.getAddress());
        resUpdateUserDTO.setAge(user.getAge());
        resUpdateUserDTO.setUpdatedAt(user.getUpdatedAt());
        return resUpdateUserDTO;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        // Convert User to ResUpdateUserDTO
        ResUserDTO resUserDTO = new ResUserDTO();
        ResUserDTO.CompanyUser companyUser = new ResUserDTO.CompanyUser();
        if (user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            resUserDTO.setCompany(companyUser);
        }

        resUserDTO.setId(user.getId());
        resUserDTO.setName(user.getName());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setGender(user.getGender());
        resUserDTO.setAddress(user.getAddress());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setUpdatedAt(user.getUpdatedAt());
        return resUserDTO;
    }

    public ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable) {
        // Here you can add any business logic before retrieving all users
        Page<User> pUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pUser.getTotalPages());
        mt.setTotal(pUser.getTotalElements());
        result.setMeta(mt);

        List<ResUserDTO> listUser = pUser.getContent().stream()
                .map(item -> new ResUserDTO(
                        item.getId(),
                        item.getName(),
                        item.getEmail(),
                        item.getGender(),
                        item.getAddress(),
                        item.getAge(),
                        item.getUpdatedAt(),
                        item.getCreatedAt(),
                        new ResUserDTO.CompanyUser(
                                item.getCompany() != null ? item.getCompany().getId() : 0,
                                item.getCompany() != null ? item.getCompany().getName() : null)))
                .collect(Collectors.toList());
        result.setResult(listUser);
        return result;
    }

    public User findByUsername(String username) {
        // Here you can add any business logic before finding the user by email
        return this.userRepository.findByEmail(username);
    }

    public User handleUpdateUser(User user) {
        User currentUser = this.getUserById(user.getId());
        if (currentUser != null) {
            currentUser.setName(user.getName());
            currentUser.setGender(user.getGender());
            currentUser.setAddress(user.getAddress());
            currentUser.setAge(user.getAge());

            if (user.getCompany() != null) {
                Optional<Company> company = Optional
                        .of((this.companyService.getCompanyById(user.getCompany().getId())));
                user.setCompany(company.isPresent() ? company.get() : null);
            }

            currentUser = this.userRepository.save(currentUser);
        }

        return currentUser;

    }

    public void updateUserToken(String email, String token) {
        User currentUser = this.findByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
