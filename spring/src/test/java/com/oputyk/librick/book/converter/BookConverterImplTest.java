package com.oputyk.librick.book.converter;

import com.oputyk.librick.author.converter.AuthorConverter;
import com.oputyk.librick.author.domain.AuthorEntity;
import com.oputyk.librick.author.dto.AuthorDto;
import com.oputyk.librick.book.domain.BookEntity;
import com.oputyk.librick.book.domain.BookRepository;
import com.oputyk.librick.book.dto.FullBookDto;
import com.oputyk.librick.bookinstance.converter.BookInstanceConverter;
import com.oputyk.librick.common.converter.EntityDtoConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by kamil on 24/02/2018.
 */

@RunWith(SpringRunner.class)
public class BookConverterImplTest {
    @Autowired
    private BookConverter bookConverter;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private EntityDtoConverter entityDtoConverter;

    @MockBean
    private AuthorConverter authorConverter;

    @MockBean
    private BookInstanceConverter bookInstanceConverter;

    private BookEntity bookEntity;
    private FullBookDto fullBookDto;
    private BookEntity oldBookEntity;
    private BookEntity halfConvertedBookEntity;

    private List<AuthorEntity> authorEntities;
    private List<AuthorDto> authorDtos;
    private List<AuthorEntity> oldAuthorEntities;

    @TestConfiguration
    static public class BookConverterImplTestConfig {
        @Bean
        public BookConverter bookConverterImpl() {
            return new BookConverterImpl();
        }
    }

    @Before
    public void setUp() throws Exception {
        Date now = new Date();

        initAuthorEntities();
        initAuthorDtos();
        initOldAuthorEntities();

        initBookEntity(now);
        initFullBookDto(now);
        initOldBookEntity();
        initHalfConverterBookEntity(now);
    }

    @Test
    public void testToFullBookDto() throws Exception {
        Mockito.when(entityDtoConverter.toDto(bookEntity, FullBookDto.class))
                .thenReturn(fullBookDto);

        assertThat(bookConverter.toFullBookDto(bookEntity))
                .isEqualToComparingFieldByFieldRecursively(fullBookDto);
    }

    @Test
    public void testFullBookDtoToBookEntity() throws Exception {
        Mockito.when(bookRepository.findOne(1L))
                .thenReturn(oldBookEntity);

        Mockito.when(entityDtoConverter.toEntity(oldBookEntity, fullBookDto))
                .thenReturn(halfConvertedBookEntity);

        Mockito.when(authorConverter.toAuthorEntity(fullBookDto.getAuthorDtos().get(0)))
                .thenReturn(bookEntity.getAuthorEntities().get(0));
        Mockito.when(authorConverter.toAuthorEntity(fullBookDto.getAuthorDtos().get(1)))
                .thenReturn(bookEntity.getAuthorEntities().get(1));

        assertThat(bookConverter.toBookEntity(fullBookDto))
                .isEqualToComparingFieldByFieldRecursively(bookEntity);
    }

    private void initAuthorDtos() {
        authorDtos = new ArrayList<>(Arrays.asList(
                new AuthorDto(1L, "firstName", "lastName", 1),
                new AuthorDto(2L, "firstName2", "lastName2", 2)
        ));
    }

    private void initOldAuthorEntities() {
        oldAuthorEntities = new ArrayList<>(Arrays.asList(
                new AuthorEntity(3L, "firstName3", "lastName3", 3, new ArrayList<>(Arrays.asList(oldBookEntity)))));
    }

    private void initBookEntity(Date now) {
        bookEntity = new BookEntity(1L, "name", authorEntities, "description", now, null);
    }

    private void initFullBookDto(Date now) {
        fullBookDto = new FullBookDto(1L, "name", authorDtos, "description", now, null);
    }

    private void initOldBookEntity() {
        oldBookEntity = new BookEntity(2L, "name2", oldAuthorEntities, "description2", null, null);
    }

    private void initHalfConverterBookEntity(Date now) {
        halfConvertedBookEntity = new BookEntity(1L, "name", oldAuthorEntities, "description", now, null);
    }

    private void initAuthorEntities() {
        authorEntities = new ArrayList<>(Arrays.asList(
                new AuthorEntity(1L, "firstName", "lastName", 1, new ArrayList<>(Arrays.asList(bookEntity))),
                new AuthorEntity(2L, "firstName2", "lastName2", 2, new ArrayList<>(Arrays.asList(bookEntity)))));
    }

}