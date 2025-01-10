// package com.example.BookManagementServer.queue;

// import com.example.BookManagementServer.model.Author;

// import jakarta.annotation.PostConstruct;

// import com.example.BookManagementServer.Services.AuthorServices;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Component;

// import java.util.concurrent.BlockingQueue;
// import java.util.concurrent.LinkedBlockingQueue;

// @Component
// public class GeneralQueue {

//     private static final BlockingQueue<GeneralRequest> queue = new LinkedBlockingQueue<>();

//     @Autowired
//     private SimpMessagingTemplate messagingTemplate;

//     @Autowired
//     private AuthorServices authorServices;

//     // @Autowired
//     // private BookServices bookServices;

//     @PostConstruct
//     public void startProcessingQueue() {
//         processQueue(); // Bắt đầu xử lý hàng đợi khi khởi động
//     }

//     public static void addRequest(GeneralRequest request) {
//         queue.add(request);
//     }

//     public void processQueue() {
//         new Thread(() -> {
//             while (true) {
//                 try {
//                     GeneralRequest request = queue.take();
//                     handleRequest(request);
//                 } catch (InterruptedException e) {
//                     Thread.currentThread().interrupt();
//                 }
//             }
//         }).start();
//     }
//     public class GeneralRequest {
//         private final String trackingId;
//         private final String serviceType;
//         private final String action;
//         private final Object payload; // Payload có thể là bất kỳ loại dữ liệu nào
    
//         public GeneralRequest(String trackingId, String serviceType, String action, Object payload) {
//             this.trackingId = trackingId;
//             this.serviceType = serviceType;
//             this.action = action;
//             this.payload = payload;
//         }
    
//         public String getTrackingId() {
//             return trackingId;
//         }
    
//         public String getServiceType() {
//             return serviceType;
//         }
    
//         public String getAction() {
//             return action;
//         }
    
//         public Object getPayload() {
//             return payload;
//         }
//     }
    

//     private void handleRequest(GeneralRequest request) {
//         boolean isSuccess = false;
//         String status;

//         switch (request.getServiceType()) {
//             case "AuthorService":
//                 isSuccess = handleAuthorService(request);
//                 break;
//             // case "BookService":
//             //     isSuccess = handleBookService(request);
//             //     break;
//             // Thêm các dịch vụ khác ở đây
//         }

//         status = isSuccess ? "SUCCESS" : "FAILED";
//         System.out.println("Sending status to /topic/status/" + request.getTrackingId() + ": " + status);
//         messagingTemplate.convertAndSend("/topic/status/" + request.getTrackingId(), status);
//     }

//     private boolean handleAuthorService(GeneralRequest request) {
//         String action = request.getAction();
//         Author author = (Author) request.getPayload();

//         switch (action) {
//             case "add":
//                 return authorServices.addAuthor(author);
//             // case "delete":
//             //     return authorServices.deleteAuthor(author);
//             // // Thêm các hành động khác ở đây
//         }
//         return false;
//     }

//     // private boolean handleBookService(GeneralRequest request) {
//     //     String action = request.getAction();
//     //     Book book = (Book) request.getPayload();

//     //     switch (action) {
//     //         case "add":
//     //             return bookServices.addBook(book);
//     //         case "delete":
//     //             return bookServices.deleteBook(book);
//     //         // Thêm các hành động khác ở đây
//     //     }
//     //     return false;
//     // }
// }
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
