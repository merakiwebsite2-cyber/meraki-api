package com.meraki.website.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String from;

    public EmailService(JavaMailSender mailSender, @Value("${app.mail.from}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    public void sendInitialPassword(String toEmail, String password) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(toEmail);
        msg.setSubject("Your Meraki account password");
        msg.setText("Your account has been approved.\n\n"
                + "Initial password: " + password + "\n\n"
                + "Please login and change your password immediately.");
        mailSender.send(msg);
    }

    public void sendInitialPasswordV1(String toEmail, String password) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    
        helper.setFrom(from);
        helper.setTo(toEmail);
        helper.setSubject("Your Meraki account password");
    
        String emailContent = 
            "Dear User,<br><br>" +
            "We are pleased to inform you that your account with <b>Meraki</b> has been successfully approved.<br><br>" +
            "To access your account, please use the temporary login credentials provided below:<br><br>" +
            "<b>Temporary Password:</b> " + password + "<br><br>" +
            "For security reasons, you are required to change your password immediately after your first login.<br><br>" +
            "You can log in using your registered email address at the Miraki platform.<br><br>" +
            "If you did not request this account or need any assistance, please contact our support team at " +
            "<a href=\"mailto:solution@meraki-interiors.ae\">solution@meraki-interiors.ae</a>.<br><br>" +
            "Thank you for choosing Miraki.<br><br>" +
            "Warm regards,<br>" +
            "<b>Meraki Team</b><br>" +
            "Meraki Interiors<br>" +
            "<a href=\"mailto:solution@meraki-interiors.ae\">solution@meraki-interiors.ae</a>";
    
        // true = HTML content
        helper.setText(emailContent, true);
    
        mailSender.send(message);
    }

    public void sendNewUserNotificationToAdmin(String userEmail,String userName) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(from);
        helper.setTo(from);
        helper.setSubject("New User Registration - Approval Required");

        String emailContent =
                "Dear Admin,<br><br>" +

                        "A new user has registered and logged in to the <b>Meraki Fabrics</b> website.<br><br>" +

                        "The user is currently awaiting approval to access and view the product collection.<br><br>" +

                        "<b>User Details:</b><br>" +
                        "Name: " + userName + "<br>" +
                        "Email: " + userEmail + "<br><br>" +

                        "Kindly review and approve the access request to allow the user to explore the fabric collections.<br><br>" +

                        "Best regards,<br>" +
                        "<b>Meraki System</b>";

        helper.setText(emailContent, true);

        mailSender.send(message);
    }

    public void sendCustomerWelcomeEmail(String toEmail, String userName) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(from);
        helper.setTo(toEmail);
        helper.setSubject("Welcome to Meraki Fabric");

        String emailContent =
                "Dear " + userName + ",<br><br>" +

                        "Welcome to <b>Meraki Fabric</b>!<br><br>" +

                        "Your login has been completed successfully. We’re glad to have you with us.<br><br>" +

                        "Your details have been securely received and shared with the Meraki admin team. " +
                        "Our team will review your information and get in touch with you shortly.<br><br>" +

                        "Thank you for choosing Meraki Fabric. We look forward to assisting you.<br><br>" +

                        "Best regards,<br>" +
                        "<b>Meraki Fabric Team</b>";

        helper.setText(emailContent, true);

        mailSender.send(message);
    }
}

