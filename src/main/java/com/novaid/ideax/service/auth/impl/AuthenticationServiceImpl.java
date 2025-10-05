package com.novaid.ideax.service.auth.impl;

import com.novaid.ideax.dto.account.AccountResponse;
import com.novaid.ideax.dto.account.InvestorProfileResponse;
import com.novaid.ideax.dto.account.StartupProfileResponse;
import com.novaid.ideax.dto.login.LoginRequestDTO;
import com.novaid.ideax.dto.login.LoginResponseDTO;
import com.novaid.ideax.dto.register.InvestorRegisterDTO;
import com.novaid.ideax.dto.register.StartupRegisterDTO;
import com.novaid.ideax.entity.auth.Account;
import com.novaid.ideax.entity.auth.InvestorProfile;
import com.novaid.ideax.entity.auth.StartupProfile;
import com.novaid.ideax.enums.Role;
import com.novaid.ideax.enums.Status;
import com.novaid.ideax.exception.AuthenticationException;
import com.novaid.ideax.repository.auth.AccountRepository;
import com.novaid.ideax.repository.auth.InvestorRepository;
import com.novaid.ideax.repository.auth.StartupRepository;
import com.novaid.ideax.service.auth.AuthenticationService;
import com.novaid.ideax.service.auth.EmailService;
import com.novaid.ideax.service.auth.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    private final StartupRepository startupRepository;
    private final InvestorRepository investorRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailService emailService;

    // === REGISTER STARTUP ===
    @Override
    public void registerStartup(StartupRegisterDTO dto) {
        validatePassword(dto.getPassword(), dto.getConfirmPassword());
        checkEmailExists(dto.getEmail());

        Account account = Account.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.START_UP)
                .status(Status.ACTIVE)
                .build();
        accountRepository.save(account);

        String base64Logo = null;
        if (dto.getCompanyLogo() != null && !dto.getCompanyLogo().isEmpty()) {
            try {
                byte[] imageBytes = dto.getCompanyLogo().getBytes();
                base64Logo = Base64.getEncoder().encodeToString(imageBytes);
            } catch (IOException e) {
                throw new RuntimeException("Không thể xử lý ảnh logo", e);
            }
        }

        StartupProfile profile = StartupProfile.builder()
                .fullName(dto.getFullName())
                .startupName(dto.getStartupName())
                .companyWebsite(dto.getCompanyWebsite())
                .companyLogo(base64Logo)
                .aboutUs(dto.getAboutUs())
                .account(account)
                .build();
        startupRepository.save(profile);

        // emailService.sendWelcomeEmail(dto.getEmail(), dto.getFullName());
    }

    // === REGISTER INVESTOR ===
    @Override
    public void registerInvestor(InvestorRegisterDTO dto) {
        validatePassword(dto.getPassword(), dto.getConfirmPassword());
        checkEmailExists(dto.getEmail());

        Account account = Account.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.INVESTOR)
                .status(Status.ACTIVE)
                .build();
        accountRepository.save(account);

        InvestorProfile profile = InvestorProfile.builder()
                .fullName(dto.getFullName())
                .organization(dto.getOrganization())
                .investmentFocus(dto.getInvestmentFocus())
                .investmentRange(dto.getInvestmentRange())
                .investmentExperience(dto.getInvestmentExperience())
                .account(account)
                .build();
        investorRepository.save(profile);

        // emailService.sendWelcomeEmail(dto.getEmail(), dto.getFullName());
    }

    // === LOGIN ===
    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        Account account = accountRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AuthenticationException("User not found"));
        // Prevent login if account is banned
        if (account.getStatus() == com.novaid.ideax.enums.Status.BANNED) {
            throw new AuthenticationException("Account is banned");
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), account.getPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }

        AccountResponse accountResponse = AccountResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .role(account.getRole())
                .status(account.getStatus())
        .token(tokenService.generateToken(account))
        .createdAt(account.getCreatedAt())
                .build();

        LoginResponseDTO response = new LoginResponseDTO();
        response.setAccount(accountResponse);

        if (account.getRole() == Role.START_UP) {
            StartupProfile profile = startupRepository.findByAccount(account)
                    .orElseThrow(() -> new AuthenticationException("Startup profile not found"));
            // dùng DTO bạn đã định nghĩa
            StartupProfileResponse dto = StartupProfileResponse.builder()
                    .fullName(profile.getFullName())
                    .startupName(profile.getStartupName())
                    .companyWebsite(profile.getCompanyWebsite())
                    .companyLogo(profile.getCompanyLogo())
                    .aboutUs(profile.getAboutUs())
                    .build();
            response.setStartupProfile(dto);
        } else if (account.getRole() == Role.INVESTOR) {
            InvestorProfile profile = investorRepository.findByAccount(account)
                    .orElseThrow(() -> new AuthenticationException("Investor profile not found"));
        InvestorProfileResponse dto = InvestorProfileResponse.builder()
            .fullName(profile.getFullName())
            .organization(profile.getOrganization())
            .investmentFocus(profile.getInvestmentFocus())
            .investmentRange(profile.getInvestmentRange())
            .investmentExperience(profile.getInvestmentExperience())
            .country(profile.getCountry())
            .phoneNumber(profile.getPhoneNumber())
            .linkedInUrl(profile.getLinkedInUrl())
            .twoFactorEnabled(profile.getTwoFactorEnabled())
            .createdAt(profile.getCreatedAt())
            .build();
            response.setInvestorProfile(dto);
        }

        return response;
    }


    // === SPRING SECURITY ===
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(account.getEmail())
                .password(account.getPassword())
                .authorities(account.getRole().name())
                .build();
    }

    // === HELPER METHODS ===
    private void validatePassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new AuthenticationException("Password and confirm password do not match");
        }
    }

    private void checkEmailExists(String email) {
        if (accountRepository.existsByEmail(email)) {
            throw new AuthenticationException("Email already in use!");
        }
    }
}
