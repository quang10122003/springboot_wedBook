package com.example.BookManagementServer.Services;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.BookManagementServer.model.Author;
@Service
public class AuthorServices {
    @Autowired
    private Connection connection;
    public AuthorServices(Connection connection2){
        this.connection = connection2;
    }
    public List<Author> getAllAuthors(){
        List<Author> listAuthors = new ArrayList<>();

        try {
            String query = "SELECT * FROM Authors";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int authorId = resultSet.getInt("AuthorID");
                String authorName  = resultSet.getString("AuthorName");

                Author author = new Author(authorId, authorName);
                listAuthors.add(author);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listAuthors;
    }

    public boolean addAuthor(Author author){
        String query = "INSERT INTO Authors (AuthorName) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, author.getName()); 
            
            int rowsAffected = statement.executeUpdate(); // Thực thi câu lệnh INSERT
            System.out.println("Rows affected: " + rowsAffected);  // In ra số dòng bị ảnh hưởng
            return rowsAffected > 0; // Nếu có ít nhất 1 dòng bị ảnh hưởng, tức là đã thêm thành công
        } catch (SQLException e) {
            e.printStackTrace();
            return false; 
        }
    }
    private boolean isAuthorLinkedWithBooks(int authorId) {
        String sql = "SELECT COUNT(*) FROM Books WHERE AuthorID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, authorId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0; // Có liên kết nếu giá trị COUNT > 0
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Mặc định là không có liên kết
    }
    public String deleteAuthor(Author author) {
        String checkAuthorSql = "SELECT COUNT(*) FROM Authors WHERE AuthorID = ?";
        String deleteSql = "DELETE FROM Authors WHERE AuthorID = ?";
    
        try {
            int authorId = author.getId();
    
            // Kiểm tra sự tồn tại của Author
            try (PreparedStatement checkStatement = connection.prepareStatement(checkAuthorSql)) {
                checkStatement.setInt(1, authorId);
    
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next() && resultSet.getInt(1) == 0) {
                        return "AUTHOR_NOT_FOUND"; // Tác giả không tồn tại
                    }
                }
            }
    
            // Kiểm tra xem Author có liên kết với Book không
            if (isAuthorLinkedWithBooks(authorId)) {
                return "BOOK_LINKED"; // Không thể xóa
            }
    
            // Thực hiện xóa
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
                deleteStatement.setInt(1, authorId);
                int rowsAffected = deleteStatement.executeUpdate();
                return rowsAffected > 0 ? "SUCCESS" : "FAILED";
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            return "FAILED";
        }
    }
    
    
     
    
}
