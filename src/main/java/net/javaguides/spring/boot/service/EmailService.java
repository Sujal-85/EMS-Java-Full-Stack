package net.javaguides.spring.boot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public void sendWelcomeEmail(String toEmail, String fullName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to Employee Management System");
            message.setText("Dear " + fullName + ",\n\n" +
                "Welcome to the Employee Management System! Your account has been created successfully.\n\n" +
                "Best regards,\nEMS Team");
            
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't throw - email is not critical
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
    }
    
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Password Reset Request");
            message.setText("You have requested to reset your password.\n\n" +
                "Your reset token is: " + resetToken + "\n\n" +
                "This token will expire in 1 hour.\n\n" +
                "If you did not request this, please ignore this email.");
            
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send password reset email: " + e.getMessage());
        }
    }
    
    public void sendLeaveStatusEmail(String toEmail, String employeeName, String status, String leaveType) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Leave Request " + status);
            message.setText("Dear " + employeeName + ",\n\n" +
                "Your " + leaveType + " request has been " + status.toLowerCase() + ".\n\n" +
                "Best regards,\nHR Team");
            
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send leave status email: " + e.getMessage());
        }
    }
    
    public void sendEmployeeCreationEmail(String toEmail, String employeeName, String employeeCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome - Employee Profile Created");
            message.setText("Dear " + employeeName + ",\n\n" +
                "Your employee profile has been created.\n" +
                "Employee Code: " + employeeCode + "\n\n" +
                "Please contact HR for further details.\n\n" +
                "Best regards,\nHR Team");
            
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send employee creation email: " + e.getMessage());
        }
    }
}
