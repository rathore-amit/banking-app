package com.bank.notification.controller;

import com.bank.notification.entity.Notification;
import com.bank.notification.repository.NotificationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 📍 Sirf demo ke liye — dekhne ke liye ki notification saved hui ya nahi */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/account/{accountId}")
    public List<Notification> getForAccount(@PathVariable Long accountId) {
        return notificationRepository.findByAccountId(accountId);
    }

    @GetMapping
    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }
}
