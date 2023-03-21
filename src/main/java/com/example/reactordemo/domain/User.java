package com.example.reactordemo.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@Getter
@Setter
@Builder
public class User {
    @Id
    String _id;
    String name;
    String phone;
}
