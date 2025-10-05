package com.novaid.ideax.controller.auth;

import com.novaid.ideax.dto.login.LoginRequestDTO;
import com.novaid.ideax.dto.login.LoginResponseDTO;
import com.novaid.ideax.dto.register.InvestorRegisterDTO;
import com.novaid.ideax.dto.register.StartupRegisterDTO;
import com.novaid.ideax.service.auth.impl.AuthenticationServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationServiceImpl authenticationService;

    @PostMapping(value = "/register/startup", consumes = {"multipart/form-data"})
    public ResponseEntity<String> registerStartup(@ModelAttribute StartupRegisterDTO dto) {
        authenticationService.registerStartup(dto);
        return ResponseEntity.ok("Startup registered successfully!");
    }


    @PostMapping("/register/investor")
    public ResponseEntity<String> registerInvestor(@RequestBody InvestorRegisterDTO dto) {
        authenticationService.registerInvestor(dto);
        return ResponseEntity.ok("Investor registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = authenticationService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}
