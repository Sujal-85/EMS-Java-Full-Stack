package net.javaguides.spring.boot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private String type = "Bearer";
    private String email;
    private String fullName;
    private String role;
    private Long employeeId;

    public JwtResponse(String accessToken, String refreshToken, String email, String fullName, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public JwtResponse(String accessToken, String refreshToken, String email, String fullName, String role, Long employeeId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.employeeId = employeeId;
    }
}
