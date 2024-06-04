package com.example.vocabulary.http.resp;

import com.example.vocabulary.entity.Vocabulary;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PageableResp<T> {
    private final List<T> contents;
    private final long total;

    private final int totalPages;
    private final int current;

    private final Pageable next;
    private final Pageable previous;

    private PageableResp(List<T> contents, long total, int totalPages, int current, Pageable next, Pageable previous) {
        this.contents = contents;
        this.total = total;
        this.totalPages = totalPages;
        this.current = current;
        this.next = next;
        this.previous = previous;
    }


    public static <T> PageableResp<T> toPageableResp(List<T> contents, long total, int totalPages, int current, Pageable next, Pageable previous){
        return new PageableResp<>(contents, total, totalPages, current, next, previous);
    }
}
