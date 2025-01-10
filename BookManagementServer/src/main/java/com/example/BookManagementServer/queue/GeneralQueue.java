package com.example.BookManagementServer.queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.example.BookManagementServer.Services.AuthorServices;
import com.example.BookManagementServer.model.Author;

import jakarta.annotation.PostConstruct;

@Component
public class GeneralQueue {

    private static final BlockingQueue<GeneralRequest> queue = new LinkedBlockingQueue<>();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private AuthorServices authorServices;

    // @Autowired
    // private BookServices bookServices;

    @PostConstruct
    public void startProcessingQueue() {
        processQueue(); // Bắt đầu xử lý hàng đợi khi khởi động
    }

    public static void addRequest(GeneralRequest request) {
        queue.add(request);
    }

    public void processQueue() {
        new Thread(() -> {
            while (true) {
                try {
                    GeneralRequest request = queue.take();
                    handleRequest(request);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    public static class GeneralRequest {
        private final String trackingId;
        private final String serviceType;
        private final String action;
        private final Object payload; // Payload có thể là bất kỳ loại dữ liệu nào

        public GeneralRequest(String trackingId, String serviceType, String action, Object payload) {
            this.trackingId = trackingId;
            this.serviceType = serviceType;
            this.action = action;
            this.payload = payload;
        }

        public String getTrackingId() {
            return trackingId;
        }

        public String getServiceType() {
            return serviceType;
        }

        public String getAction() {
            return action;
        }

        public Object getPayload() {
            return payload;
        }
}


private void handleRequest(GeneralRequest request) {
    String status = "FAILED"; // Mặc định là FAILED

    switch (request.getServiceType()) {
        case "AuthorService":
            status = handleAuthorService(request);
            break;
        // case "BookService":
        //     status = handleBookService(request);
        //     break;
        // Thêm các dịch vụ khác ở đây
    }
    try {
        Thread.sleep(700); // Chờ 100ms trước khi gửi phản hồi
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    System.out.println("Sending status to /topic/status/" + request.getTrackingId() + ": " + status);
    messagingTemplate.convertAndSend("/topic/status/" + request.getTrackingId(), status);

   
    
}

private String handleAuthorService(GeneralRequest request) {
    String action = request.getAction();
    Author author = (Author) request.getPayload();

    switch (action) {
        case "add":
            boolean isAdded = authorServices.addAuthor(author);
            return isAdded ? "SUCCESS" : "FAILED";
        case "delete":
            return authorServices.deleteAuthor(author); // Trả về "SUCCESS", "BOOK_LINKED", hoặc "FAILED"
        // Thêm các hành động khác ở đây
    }
    return "FAILED"; // Trường hợp không xác định
}


    // private boolean handleBookService(GeneralRequest request) {
    //     String action = request.getAction();
    //     Book book = (Book) request.getPayload();

    //     switch (action) {
    //         case "add":
    //             return bookServices.addBook(book);
    //         case "delete":
    //             return bookServices.deleteBook(book);
    //         // Thêm các hành động khác ở đây
    //     }
    //     return false;
    // }
}
