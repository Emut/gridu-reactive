package com.example.reactordemo.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@Data
@Builder
public class User {
    @Id
    String _id;
    String name;
    String phone;
}
