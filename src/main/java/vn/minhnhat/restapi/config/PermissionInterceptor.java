package vn.minhnhat.restapi.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.minhnhat.restapi.domain.Permission;
import vn.minhnhat.restapi.domain.Role;
import vn.minhnhat.restapi.domain.User;
import vn.minhnhat.restapi.service.UserService;
import vn.minhnhat.restapi.util.SecurityUtil;
import vn.minhnhat.restapi.util.error.IdInvalidException;
import vn.minhnhat.restapi.util.error.PermissionException;

public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Override
    @Transactional
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();

        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path = " + path);
        System.out.println(">>> httpMethod = " + httpMethod);
        System.out.println(">>> requestURI = " + requestURI);

        // Check permission
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        if (email != null && !email.isEmpty()) {
            User user = this.userService.findByUsername(email);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    List<Permission> permissions = role.getPermissions();
                    boolean isAllow = permissions.stream()
                            .anyMatch(item -> item.getApiPath().equals(path) && item.getMethod().equals(httpMethod));

                    if (isAllow == false) {
                        throw new PermissionException("Ban khong co quyen truy cap");
                    }
                } else {
                    throw new PermissionException("Role not found for user: " + email);
                }
            }
        }

        return true; // Cho phép request tiếp tục
    }
}
