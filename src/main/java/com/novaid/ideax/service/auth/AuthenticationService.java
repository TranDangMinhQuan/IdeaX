package com.novaid.ideax.service.auth;
import com.novaid.ideax.dto.login.LoginRequestDTO;
import com.novaid.ideax.dto.login.LoginResponseDTO;
import com.novaid.ideax.dto.register.InvestorRegisterDTO;
import com.novaid.ideax.dto.register.StartupRegisterDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AuthenticationService extends UserDetailsService {
    void registerStartup(StartupRegisterDTO dto);
    void registerInvestor(InvestorRegisterDTO dto);
    LoginResponseDTO login(LoginRequestDTO loginRequest);
    UserDetails loadUserByUsername(String username)throws UsernameNotFoundException;
}

