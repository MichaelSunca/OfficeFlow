package com.officeflow.backend.service;

import com.officeflow.backend.dto.LoginDTO;

public interface AuthService {
    String login(LoginDTO loginDTO);
}