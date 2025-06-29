package com.example.assessment_employee.repository;

import com.example.assessment_employee.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    /**
     * Find account by username
     * @param username the username to search for
     * @return Optional<Account>
     */
    Optional<Account> findByUsername(String username);
    
    /**
     * Find accounts by role
     * @param role the role to search for
     * @return List<Account>
     */
    List<Account> findByRole(String role);
    
    /**
     * Find accounts by status
     * @param status the status to search for
     * @return List<Account>
     */
    List<Account> findByStatus(String status);
    
    /**
     * Find accounts by role and status
     * @param role the role to search for
     * @param status the status to search for
     * @return List<Account>
     */
    List<Account> findByRoleAndStatus(String role, String status);
    
    /**
     * Check if username exists
     * @param username the username to check
     * @return boolean
     */
    boolean existsByUsername(String username);
    
    /**
     * Find active accounts (status = 'ACTIVE')
     * @return List<Account>
     */
    @Query("SELECT a FROM Account a WHERE a.status = 'ACTIVE'")
    List<Account> findActiveAccounts();
    
    /**
     * Find accounts with employee information
     * @return List<Account>
     */
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.employee")
    List<Account> findAllWithEmployee();
    
    /**
     * Find account by username with employee information
     * @param username the username to search for
     * @return Optional<Account>
     */
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.employee WHERE a.username = :username")
    Optional<Account> findByUsernameWithEmployee(@Param("username") String username);
}
