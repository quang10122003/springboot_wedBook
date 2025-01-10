package com.example.BookManagementServer.controller;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.BookManagementServer.DatabaseConnection;
import com.example.BookManagementServer.Services.AuthorServices;
import com.example.BookManagementServer.model.Author;
import com.example.BookManagementServer.queue.GeneralQueue;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("api/author")
public class AuthorController{
    private Connection connection = DatabaseConnection.getConnection();
    private AuthorServices authorServices = new AuthorServices(connection);
    @GetMapping("list")
    public List<Author> getAuthorAll() throws RemoteException{
       return authorServices.getAllAuthors();
    }

    // @PostMapping("add")
    // public String addAuthor(@RequestBody JsonNode json) {
    //     String authorName = json.get("authorName").asText();
    //     Author author = new Author();
    //     author.setName(authorName); // Gán tên tác giả từ tham số authorName
    //     boolean isAdded = authorServices.addAuthor(author);
        
    //     if (isAdded) {
    //         return "Author added successfully!";
    //     } else {
    //         return "Failed to add author!";
    //     }
    // }

    @PostMapping("add")
    public String addAuthor(@RequestBody JsonNode json) {
        // Lấy tên tác giả từ JSON trong request
        String authorName = json.get("authorName").asText();
        Author newAuthor = new Author(authorName);

        // Tạo mã tracking ID (mã duy nhất để theo dõi yêu cầu này)
        String trackingId = UUID.randomUUID().toString();

        // Thêm yêu cầu vào hàng đợi
        GeneralQueue.addRequest(new GeneralQueue.GeneralRequest(trackingId,"AuthorService","add",newAuthor));

        // Trả về thông báo cho người dùng kèm tracking ID
        return "Your request has been added to the queue. Tracking ID: " + trackingId;
    }

    @DeleteMapping("delete")
    public String deleteAuthor(@RequestParam("id") String idAuthor) {
        int id = Integer.parseInt(idAuthor);
        Author newAuthor = new Author(id);
        String trackingId = UUID.randomUUID().toString(); 
        GeneralQueue.addRequest(new GeneralQueue.GeneralRequest(trackingId, "AuthorService", "delete", newAuthor));
        return "Your request has been added to the queue. Tracking ID: " + trackingId;
    }
}


 

// @Controller
// public class AuthorController {
//     private Connection connection = DatabaseConnection.getConnection();
//     private AuthorServices authorServices = new AuthorServices(connection);

//     @GetMapping("authors")
//     public String getAuthors(ModelMap model) {
//         List<Author> authors = authorServices.getAllAuthors();
//         model.addAttribute("authors", authors); // Chỉ rõ tên thuộc tính là "authors"
//         return "authorList";
//     }

// }