package com.example.BookManagementServer.model;

public class Author {
    private int id;
    private String authorName;
    public Author(int id, String AuthorName){
        this.id = id;
        this.authorName = AuthorName;
    }
    public Author() {
        
    }
    public Author(String authorName){
        this.authorName = authorName;
    }
    public String getName() {
        return authorName;
    }
    
    public void setName(String name) {
        this.authorName = name;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
}
