package com.officeflow.backend.controller;

import com.officeflow.backend.common.Result;
import com.officeflow.backend.dto.LoginDTO;
import com.officeflow.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {

        var token = authService.login(loginDTO);

        // 返回给前端
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", loginDTO.getUsername());
        return Result.success(data);
    }
}