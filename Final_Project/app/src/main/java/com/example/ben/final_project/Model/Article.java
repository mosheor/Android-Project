package com.example.ben.final_project.Model;

import java.util.List;

public class Article {
    public String articleID;//defult
    public String imageUrl;
    public String mainTitle;
    public String subTitle;
    public String author;
    public double publishDate;
    public String content;
    public List<Comment> comments;
    public double lastUpdateDate;
    public boolean wasDeleted;
}
