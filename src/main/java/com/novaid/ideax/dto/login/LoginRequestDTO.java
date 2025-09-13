package com.novaid.ideax.dto.login;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}