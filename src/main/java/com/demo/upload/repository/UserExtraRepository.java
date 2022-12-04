package com.demo.upload.repository;

import com.demo.upload.domain.User;
import com.demo.upload.domain.UserExtra;

import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserExtra entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserExtraRepository extends JpaRepository<UserExtra, Long> {
	
    Optional<UserExtra> findOneById(Long id);

	Optional<UserExtra> findByUserId(Long id);

}
