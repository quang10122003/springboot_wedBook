package com.example.BookManagementServer.queue;

import com.example.BookManagementServer.model.Author;

import jakarta.annotation.PostConstruct;

import com.example.BookManagementServer.Services.AuthorServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class AuthorQueue {

    private static final BlockingQueue<AuthorRequest> queue = new LinkedBlockingQueue<>();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private AuthorServices authorServices;

    // Phương thức này sẽ được gọi ngay khi ứng dụng khởi động
    @PostConstruct
    public void startProcessingQueue() {
        processQueue();  // Đảm bảo rằng queue bắt đầu xử lý khi ứng dụng khởi động
    }

    // Phương thức thêm yêu cầu vào hàng đợi
    public static void addRequest(AuthorRequest request) {
        queue.add(request);
    }

    // Luồng xử lý yêu cầu trong hàng đợi
    public void processQueue() {
        new Thread(() -> {
            while (true) {
                try {
                    AuthorRequest request = queue.take(); // Lấy yêu cầu từ hàng đợi
                    String action = request.getAction();
                    Author author = request.getAuthor();
                    boolean isSuccess = false;

                    switch (action) {
                        case "add":
                            isSuccess = authorServices.addAuthor(author);
                            break;
                    }

                    String status = isSuccess ? "SUCCESS" : "FAILED";
                    System.out.println("Sending status to /topic/status/" + request.getTrackingId() + ": " + status);
                    messagingTemplate.convertAndSend("/topic/status/" + request.getTrackingId(), status);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Nếu luồng bị gián đoạn, thoát khỏi vòng lặp
                }
            }
        }).start();
    }

    public static class AuthorRequest {
        private final String trackingId;
        private final String action;
        private final Author author;

        public AuthorRequest(String trackingId, String action, Author author) {
            this.trackingId = trackingId;
            this.author = author;
            this.action = action;
        }

        public String getTrackingId() {
            return trackingId;
        }
        public String getAction() {
            return action;
        }
        public Author getAuthor() {
            return author;
        }
    }
}