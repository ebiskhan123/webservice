package com.csye6225.application.objects;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@Builder
@ToString
public class UserDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private Date account_created;
    private Date account_updated;
}
