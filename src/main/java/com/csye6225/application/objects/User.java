package com.csye6225.application.objects;

import com.csye6225.application.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@Entity
@Table(name = "user")
public class User {

    public User(){

    }
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "uuid"
    )

    private String id;

    private String firstName;
    private String lastName;
//^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$
    @Column(name = "username", nullable = false,updatable = false)
    @Email()
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date account_created;

    @Column(nullable = false)
    @UpdateTimestamp
    private Date account_updated;

}
