package com.virtuallibrary.services.impl;

import com.virtuallibrary.entities.Loan;
import com.virtuallibrary.entities.User;
import com.virtuallibrary.repositories.LoanRepository;
import com.virtuallibrary.services.BookService;
import com.virtuallibrary.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private BookService bookService;

    @Transactional(readOnly = true)
    @Override
    public List<Loan> findAll() {
        return (List<Loan>) loanRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Loan> findById(Long id) {
        return loanRepository.findById(id);
    }

    @Transactional
    @Override
    public Loan save(Loan loan) {
        return loanRepository.save(loan);
    }

    @Transactional
    @Override
    public void delete(Loan loan) {
        loanRepository.delete(loan);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Loan> findByUser(User user) {
        return loanRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Loan> findByEnabledTrue() {
        return loanRepository.findByEnabledTrue();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Loan> findByEnabledFalse() {
        return loanRepository.findByEnabledFalse();
    }

    @Transactional
    @Override
    public void activate(Long id) {
        Optional<Loan> loanOptional = findById(id);
        loanOptional.ifPresent(loan -> {
            if (!loan.isEnabled() && loan.getDateDevolution().isAfter(LocalDate.now())) {
                bookService.lendBook(loan.getBook());
                loan.setEnabled(true);
                save(loan);
            }
        });
    }

    @Transactional
    @Override
    public void deactivate(Long id) {
        Optional<Loan> loanOptional = findById(id);
        loanOptional.ifPresent(loan -> {
            if (loan.isEnabled()) {
                //We return the book for disabling the loan
                bookService.devolutionBook(loan.getBook());
                loan.setEnabled(false);
                save(loan);
            }
        });
    }
}