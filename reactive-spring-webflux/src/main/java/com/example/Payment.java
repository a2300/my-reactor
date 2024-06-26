package com.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    String id;
    String user;

    public Payment withUser(String user) {
        return this.user.equals(user) ? this : new Payment(this.id, user);
    }
}
