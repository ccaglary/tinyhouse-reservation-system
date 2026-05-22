package com.tinyhouse.service;

import com.tinyhouse.entity.Reservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@tinyhouse.com}")
    private String fromAddress;

    @Async
    public void sendVerificationEmail(String to, String firstName, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject("TinyHouse - E-posta Doğrulama");
            message.setText("Merhaba " + firstName + ",\n\n"
                    + "Hesabınızı doğrulamak için aşağıdaki linke tıklayın:\n"
                    + "http://localhost:8080/verify-email?token=" + token + "\n\n"
                    + "İyi günler,\nTinyHouse Ekibi");
            mailSender.send(message);
            log.info("Verification email sent to: {}", to);
        } catch (Exception e) {
            log.warn("Failed to send verification email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendPasswordResetEmail(String to, String firstName, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject("TinyHouse - Şifre Sıfırlama");
            message.setText("Merhaba " + firstName + ",\n\n"
                    + "Şifrenizi sıfırlamak için aşağıdaki linke tıklayın:\n"
                    + "http://localhost:8080/reset-password?token=" + token + "\n\n"
                    + "Bu işlemi siz yapmadıysanız, lütfen bu e-postayı dikkate almayın.\n\n"
                    + "İyi günler,\nTinyHouse Ekibi");
            mailSender.send(message);
            log.info("Password reset email sent to: {}", to);
        } catch (Exception e) {
            log.warn("Failed to send password reset email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendReservationConfirmation(String to, String firstName, Reservation reservation) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject("TinyHouse - Rezervasyon Onaylandı");
            message.setText("Merhaba " + firstName + ",\n\n"
                    + reservation.getTinyHouse().getTitle() + " için rezervasyonunuz onaylandı.\n"
                    + "Giriş: " + reservation.getStartDate() + "\n"
                    + "Çıkış: " + reservation.getEndDate() + "\n"
                    + "Toplam: " + reservation.getTotalPrice() + " TL\n\n"
                    + "İyi tatiller dileriz!\nTinyHouse Ekibi");
            mailSender.send(message);
            log.info("Reservation confirmation email sent to: {}", to);
        } catch (Exception e) {
            log.warn("Failed to send reservation confirmation to {}: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendPaymentConfirmation(String to, String firstName, String transactionId, String amount) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject("TinyHouse - Ödeme Onayı");
            message.setText("Merhaba " + firstName + ",\n\n"
                    + "Ödemeniz başarıyla alındı.\n"
                    + "İşlem No: " + transactionId + "\n"
                    + "Tutar: " + amount + " TL\n\n"
                    + "İyi günler,\nTinyHouse Ekibi");
            mailSender.send(message);
            log.info("Payment confirmation email sent to: {}", to);
        } catch (Exception e) {
            log.warn("Failed to send payment confirmation to {}: {}", to, e.getMessage());
        }
    }
}
