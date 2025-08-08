package vn.minhnhat.restapi.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.minhnhat.restapi.domain.Permission;
import vn.minhnhat.restapi.domain.Role;
import vn.minhnhat.restapi.domain.User;
import vn.minhnhat.restapi.repository.PermissionRepository;
import vn.minhnhat.restapi.repository.RoleRepository;
import vn.minhnhat.restapi.repository.UserRepository;
import vn.minhnhat.restapi.util.constant.GenderEnum;

@Service
public class DbInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DbInitializer(PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> Initializing database...");

        long countPermissions = this.permissionRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();

        if (countPermissions == 0) {
            ArrayList<Permission> permissions = new ArrayList<>();
            permissions.add(new Permission("Create a companies", "/api/v1/companies",
                    "POST", "COMPANY"));
            permissions.add(new Permission("Update a companies",
                    "/api/v1/companies/{id}", "PUT", "COMPANY"));
            permissions.add(new Permission("Delete a companies",
                    "/api/v1/companies/{id}", "DELETE", "COMPANY"));
            permissions.add(new Permission("Get a companies by id",
                    "/api/v1/companies/{id}", "GET", "COMPANY"));
            permissions.add(new Permission("Get all companies", "/api/v1/companies",
                    "GET", "COMPANY"));

            permissions.add(new Permission("Create a job", "/api/v1/jobs", "POST",
                    "JOB"));
            permissions.add(new Permission("Update a job", "/api/v1/jobs/{id}", "PUT",
                    "JOB"));
            permissions.add(new Permission("Delete a job", "/api/v1/jobs/{id}", "DELETE",
                    "JOB"));
            permissions.add(new Permission("Get a job by id", "/api/v1/jobs/{id}", "GET",
                    "JOB"));
            permissions.add(new Permission("Get all jobs", "/api/v1/jobs", "GET",
                    "JOB"));

            permissions.add(new Permission("Create a user", "/api/v1/users", "POST",
                    "USER"));
            permissions.add(new Permission("Update a user", "/api/v1/users/{id}", "PUT",
                    "USER"));
            permissions.add(new Permission("Delete a user", "/api/v1/users/{id}",
                    "DELETE", "USER"));
            permissions.add(new Permission("Get a user by id", "/api/v1/users/{id}",
                    "GET", "USER"));
            permissions.add(new Permission("Get all users", "/api/v1/users", "GET",
                    "USER"));

            permissions.add(new Permission("Create a permission", "/api/v1/permissions",
                    "POST", "PERMISSION"));
            permissions.add(new Permission("Update a permission", "/api/v1/permissions",
                    "PUT", "PERMISSION"));
            permissions.add(new Permission("Delete a permission",
                    "/api/v1/permissions/{id}", "DELETE", "PERMISSION"));
            permissions.add(new Permission("Get a permission by id",
                    "/api/v1/permissions/{id}", "GET", "PERMISSION"));
            permissions.add(new Permission("Get all permissions", "/api/v1/permissions",
                    "GET", "PERMISSION"));

            permissions.add(new Permission("Create a role", "/api/v1/roles", "POST",
                    "ROLE"));
            permissions.add(new Permission("Update a role", "/api/v1/roles/{id}", "PUT",
                    "ROLE"));
            permissions.add(new Permission("Delete a role", "/api/v1/roles/{id}",
                    "DELETE", "ROLE"));
            permissions.add(new Permission("Get a role by id", "/api/v1/roles/{id}",
                    "GET", "ROLE"));
            permissions.add(new Permission("Get all roles", "/api/v1/roles", "GET",
                    "ROLE"));

            permissions.add(new Permission("Create a skill", "/api/v1/skills", "POST",
                    "SKILL"));
            permissions.add(new Permission("Update a skill", "/api/v1/skills/{id}",
                    "PUT", "SKILL"));
            permissions.add(new Permission("Delete a skill", "/api/v1/skills/{id}",
                    "DELETE", "SKILL"));
            permissions.add(new Permission("Get a skill by id", "/api/v1/skills/{id}",
                    "GET", "SKILL"));
            permissions.add(new Permission("Get all skills", "/api/v1/skills", "GET",
                    "SKILL"));

            permissions.add(new Permission("Create a resume", "/api/v1/resumes", "POST",
                    "RESUME"));
            permissions.add(new Permission("Update a resume", "/api/v1/resumes/{id}",
                    "PUT", "RESUME"));
            permissions.add(new Permission("Delete a resume", "/api/v1/resumes/{id}",
                    "DELETE", "RESUME"));
            permissions.add(new Permission("Get a resume by id", "/api/v1/resumes/{id}",
                    "GET", "RESUME"));
            permissions.add(new Permission("Get all resumes", "/api/v1/resumes", "GET",
                    "RESUME"));

            permissions.add(new Permission("Create a subscriber", "/api/v1/subscribers",
                    "POST", "SUBSCRIBER"));
            permissions.add(new Permission("Update a subscriber",
                    "/api/v1/subscribers/{id}", "PUT", "SUBSCRIBER"));
            permissions.add(new Permission("Delete a subscriber",
                    "/api/v1/subscribers/{id}", "DELETE", "SUBSCRIBER"));
            permissions.add(new Permission("Get a subscriber by id",
                    "/api/v1/subscribers/{id}", "GET", "SUBSCRIBER"));
            permissions.add(new Permission("Get all subscribers", "/api/v1/subscribers",
                    "GET", "SUBSCRIBER"));

            permissions.add(new Permission("Download a file", "/api/v1/files", "POST",
                    "FILE"));
            permissions.add(new Permission("Upload a file", "/api/v1/files", "GET",
                    "FILE"));
            // Add default permissions here
            this.permissionRepository.saveAll(permissions);
        }

        if (countRoles == 0) {
            List<Permission> allPermissions = this.permissionRepository.findAll();

            Role adminRole = new Role();
            adminRole.setName("SUPER_ADMIN");
            adminRole.setDescription("Super Admin Role full permissions");
            adminRole.setActive(true);
            adminRole.setPermissions(allPermissions);

            this.roleRepository.save(adminRole);

        }

        if (countUsers == 0) {
            User adminUser = new User();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setAddress("Hanoi");
            adminUser.setAge(21);
            adminUser.setGender(GenderEnum.MALE);
            adminUser.setName("super admin");
            adminUser.setPassword(passwordEncoder.encode("123456"));

            Role adminRole = this.roleRepository.findByName("SUPER_ADMIN");
            if (adminRole != null) {
                adminUser.setRole(adminRole);
            }

            this.userRepository.save(adminUser);

        }

        if (countUsers > 0 && countRoles > 0 && countPermissions > 0) {
            System.out.println(">>> Database already initialized.");
        } else {
            System.out.println(">>> Database initialization completed.");
        }
    }

}
