package vn.minhnhat.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.minhnhat.restapi.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
