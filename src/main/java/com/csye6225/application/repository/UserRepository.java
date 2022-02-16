package com.csye6225.application.repository;

import com.csye6225.application.objects.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username);

    User findById(long id);

//    @Modifying
//    @Query("update User u set u.first_name = :first_name , u.last_name = :last_name where u.lastLoginDate < :date")
//    void deactivateUsersNotLoggedInSince(@Param("date") LocalDate date);
}
