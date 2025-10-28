package net.javaguides.spring.boot.service;

import net.javaguides.spring.boot.dto.*;
import net.javaguides.spring.boot.entity.User;
import net.javaguides.spring.boot.model.Employee;
import net.javaguides.spring.boot.repository.EmployeeRepository;
import net.javaguides.spring.boot.repository.UserRepository;
import net.javaguides.spring.boot.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private AuditLogService auditLogService;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    public JwtResponse login(LoginRequest request, String ipAddress) {
        // Allow login using either email or employee code in the "email" field
        String emailOrCode = request.getEmail();

        String usernameForAuth = emailOrCode;
        // If it doesn't look like an email, treat it as employee code and resolve to user's email
        if (emailOrCode != null && !emailOrCode.contains("@")) {
            // Attempt to resolve employee by code, then fetch linked user's email
            // We avoid a hard dependency here and authenticate directly by email after resolution
            try {
                // Lookup by employee code via repository
                var employeeOpt = employeeRepository.findByEmployeeCode(emailOrCode);
                if (employeeOpt.isPresent()) {
                    Employee emp = employeeOpt.get();
                    if (emp.getUser() == null) {
                        // Auto-provision a user for this employee with default password
                        User user = new User();
                        user.setEmail(emp.getEmail());
                        user.setFullName(emp.getFirstName() + " " + emp.getLastName());
                        user.setRole(User.Role.EMPLOYEE);
                        user.setActive(true);
                        String defaultPassword = "employee123";
                        user.setPassword(passwordEncoder.encode(defaultPassword));
                        User saved = userRepository.save(user);
                        emp.setUser(saved);
                        employeeRepository.save(emp);
                    }
                    if (emp.getUser() != null) {
                        usernameForAuth = emp.getUser().getEmail();
                    }
                }
            } catch (Exception ignored) {
                // Fallback to using provided value; authentication will fail if invalid
            }
        }

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(usernameForAuth, request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        User user = userRepository.findByEmail(usernameForAuth)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Ensure user is linked to an Employee (for EMPLOYEE role) to provide employeeId to frontend
        if (user.getRole() == User.Role.EMPLOYEE && user.getEmployee() == null) {
            try {
                var empByEmail = employeeRepository.findByEmail(user.getEmail());
                if (empByEmail.isPresent()) {
                    var emp = empByEmail.get();
                    if (emp.getUser() == null) {
                        emp.setUser(user);
                        employeeRepository.save(emp);
                    }
                }
            } catch (Exception ignored) { }
        }

        auditLogService.log("LOGIN", "User", user.getId(),
            "User logged in", usernameForAuth, ipAddress);

        Long employeeId = (user.getEmployee() != null) ? user.getEmployee().getId() : null;
        return new JwtResponse(accessToken, refreshToken, user.getEmail(),
            user.getFullName(), user.getRole().name(), employeeId);
    }
    
    public String signup(SignupRequest request, String ipAddress) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.valueOf(request.getRole().toUpperCase()));
        user.setActive(true);
        
        User savedUser = userRepository.save(user);
        
        auditLogService.log("SIGNUP", "User", savedUser.getId(), 
            "New user registered", "SYSTEM", ipAddress);
        
        emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());
        
        return "User registered successfully";
    }
    
    public String forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
        
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
        
        return "Password reset link sent to your email";
    }
    
    public String resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getResetToken())
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));
        
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        
        return "Password reset successfully";
    }
}
