package com.novaid.ideax.service.auth.impl;

import com.novaid.ideax.dto.account.AccountCreateDTO;
import com.novaid.ideax.dto.account.AccountUpdateDTO;
import com.novaid.ideax.dto.account.AccountResponse;
import com.novaid.ideax.entity.auth.Account;
import com.novaid.ideax.enums.Role;
import com.novaid.ideax.enums.Status;
import com.novaid.ideax.repository.auth.AccountRepository;
import com.novaid.ideax.service.auth.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void createByAdmin(AccountCreateDTO dto) {
        if (accountRepo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        Account account = new Account();
        account.setEmail(dto.getEmail());
        account.setPassword(passwordEncoder.encode(dto.getPassword()));
        account.setRole(dto.getRole()); // INVESTOR hoặc STARTUP
        account.setStatus(Status.ACTIVE);
        accountRepo.save(account);
    }

    @Override
    public void updateByAdminOrStaff(Long id, AccountUpdateDTO dto) {
        Account acc = accountRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        // Admin/staff chỉ được đổi role & status
        if (dto.getRole() != null) {
            acc.setRole(dto.getRole());
        }
        if (dto.getStatus() != null) {
            acc.setStatus(dto.getStatus());
        }

        accountRepo.save(acc);
    }

    @Override
    public List<AccountResponse> getAllByRole(Role role) {
        List<Account> accounts = accountRepo.findAllByRole(role);
        return accounts.stream()
                .map(acc -> modelMapper.map(acc, AccountResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public void setSelfStatus(Long selfId, Status status) {
        Account self = accountRepo.findById(selfId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        self.setStatus(status);
        accountRepo.save(self);
    }

    @Override
    public void adminSetStatus(Long adminId, Long targetId, Status status) {
        Account target = accountRepo.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("Target account not found"));
        target.setStatus(status);
        accountRepo.save(target);
    }

    @Override
    public AccountResponse getAccountById(Long id) {
        Account acc = accountRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return modelMapper.map(acc, AccountResponse.class);
    }
}
