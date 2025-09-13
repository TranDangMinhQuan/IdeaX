package com.novaid.ideax.service.auth;

import com.novaid.ideax.dto.account.AccountCreateDTO;
import com.novaid.ideax.dto.account.AccountUpdateDTO;
import com.novaid.ideax.dto.account.AccountResponse;
import com.novaid.ideax.enums.Role;
import com.novaid.ideax.enums.Status;

import java.util.List;

public interface AccountService {
    void createByAdmin(AccountCreateDTO dto);

    void updateByAdminOrStaff(Long id, AccountUpdateDTO dto);

    List<AccountResponse> getAllByRole(Role role);

    void setSelfStatus(Long selfId, Status status);

    void adminSetStatus(Long adminId, Long targetId, Status status);

    AccountResponse getAccountById(Long id);
}
