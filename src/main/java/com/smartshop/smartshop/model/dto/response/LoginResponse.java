package com.smartshop.smartshop.model.dto.response;

import com.smartshop.smartshop.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String id;
    private String username;
    private UserRole role;
    private String message;
}
