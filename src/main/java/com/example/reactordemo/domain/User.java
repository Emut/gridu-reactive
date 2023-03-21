package com.example.reactordemo.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@Getter
@Setter
@Builder
@ToString
public class User {
    @Id
    String _id;
    String name;
    String phone;
}
