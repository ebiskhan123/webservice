package com.csye6225.application.objects;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@Entity
@Table(name = "Image")
@NamedQueries({
        @NamedQuery(name = "Image.findAll", query = "select i from Image i")
})
public class Image {

    public Image(){

    }
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "uuid"
    )
    @Column(nullable = false)
    private String id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = true)
    private String url;

    @Column(nullable = false)
    @CreationTimestamp
    private Date uploadDate;

    @Column(name = "userid",nullable = false,unique = true)
    private String userId;
}


