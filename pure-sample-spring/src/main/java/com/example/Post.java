package com.example;

import lombok.*;

import java.util.UUID;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Post {
    private UUID id;
    private String title;
    private String content;
}
