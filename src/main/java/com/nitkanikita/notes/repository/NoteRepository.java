package com.nitkanikita.notes.repository;

import com.nitkanikita.notes.model.entity.Note;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface NoteRepository extends R2dbcRepository<Note, Long> {
    Flux<Note> findAllBy(Pageable pageable);
}