package com.roomhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.roomhub.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
     User findByPhoneNumber(String phone_number);

     User findByNickname(String nickname);

     User findByEmail(String email);
}
