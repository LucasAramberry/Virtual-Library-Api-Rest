package com.virtuallibrary.repositories;

import com.virtuallibrary.entities.Loan;
import com.virtuallibrary.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUser(User user);

    List<Loan> findByEnabledTrue();

    List<Loan> findByEnabledFalse();

}
