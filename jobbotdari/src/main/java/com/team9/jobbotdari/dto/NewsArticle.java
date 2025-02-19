package com.team9.jobbotdari.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class NewsArticle {
    private String title;
    private String link;
    private String description;
    private Date publishedDate;
}
