package com.novaid.ideax.controller.auth;

import com.novaid.ideax.dto.account.AccountCreateDTO;
import com.novaid.ideax.dto.account.AccountUpdateDTO;
import com.novaid.ideax.dto.account.AccountResponse;
import com.novaid.ideax.enums.Role;
import com.novaid.ideax.enums.Status;
import com.novaid.ideax.service.auth.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@SecurityRequirement(name = "api")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // Admin tạo account / kh làm fe
    @PostMapping("/create")
    public ResponseEntity<String> createByAdmin(@RequestBody AccountCreateDTO dto) {
        accountService.createByAdmin(dto);
        return ResponseEntity.ok("Account created successfully");
    }

    // Admin update account (role, status)  /ko làm fe
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateByAdminOrStaff(@PathVariable Long id,
                                                       @RequestBody AccountUpdateDTO dto) {
        accountService.updateByAdminOrStaff(id, dto);
        return ResponseEntity.ok("Account updated successfully");
    }

    // Lấy danh sách account theo role
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountResponse>> getAllByRole(@PathVariable Role role) {
        return ResponseEntity.ok(accountService.getAllByRole(role));
    }

    // Lấy chi tiết account
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    // User tự đổi status (ví dụ deactivate)
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('START_UP','INVESTOR')")
    public ResponseEntity<String> setSelfStatus(@PathVariable Long id,
                                                @RequestParam Status status) {
        accountService.setSelfStatus(id, status);
        return ResponseEntity.ok("Status updated successfully");
    }

    // Admin đổi status của người khác
    @PutMapping("/admin/{targetId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminSetStatus(@PathVariable Long targetId,
                                                 @RequestParam Status status,
                                                 @RequestParam Long adminId) {
        accountService.adminSetStatus(adminId, targetId, status);
        return ResponseEntity.ok("Target account status updated successfully");
    }

    // Xóa mềm (soft delete): đổi status sang DELETED
    @DeleteMapping("/{id}/soft")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> softDeleteAccount(@PathVariable Long id) {
        accountService.softDelete(id);
        return ResponseEntity.ok("Account soft deleted successfully");
    }

    // Xóa cứng (hard delete): xóa luôn trong DB
    @DeleteMapping("/{id}/hard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> hardDeleteAccount(@PathVariable Long id) {
        accountService.hardDelete(id);
        return ResponseEntity.ok("Account hard deleted successfully");
    }


}

