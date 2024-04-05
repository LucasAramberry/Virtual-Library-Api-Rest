package com.virtuallibrary.services;

import com.virtuallibrary.entities.Loan;
import com.virtuallibrary.entities.User;

import java.util.List;
import java.util.Optional;

public interface LoanService {

    List<Loan> findAll();

    Optional<Loan> findById(Long id);

    Loan save(Loan loan);

    void delete(Loan loan);

    List<Loan> findByUser(User user);

    List<Loan> findByEnabledTrue();

    List<Loan> findByEnabledFalse();

    void activate(Long id);

    void deactivate(Long id);
}
