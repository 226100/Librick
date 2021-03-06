package com.oputyk.librick.author.dto;

import com.oputyk.librick.book.domain.BookEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * Created by kamil on 03/02/2018.
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthorDto {
    private Long id;
    private String firstName;
    private String lastName;
    private Integer age;
}
