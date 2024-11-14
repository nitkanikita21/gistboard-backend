package com.nitkanikita.notes.controller;

import com.nitkanikita.notes.model.entity.Note;
import com.nitkanikita.notes.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/article")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class NoteController {

    private final NoteRepository noteRepository;

    @GetMapping()
    public Mono<Page<Note>> getAllNotes(Pageable pageable) {
        return this.noteRepository.findAllBy(pageable)
            .collectList()
            .zipWith(this.noteRepository.count())
            .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    @GetMapping("/{id}")
    public Mono<Note> getNoteById(@PathVariable Long id) {
        return noteRepository.findById(id);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Mono<Note> createNote(@RequestBody Note note) {
        return noteRepository.save(note);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Void> deleteNoteById(@PathVariable Long id) {
        return noteRepository.deleteById(id);
    }
}