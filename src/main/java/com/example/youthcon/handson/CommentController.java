package com.example.youthcon.handson;

import com.example.youthcon.preparation.Comment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(final CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(@RequestParam("articleId") String articleId) {
        SseEmitter emitter = commentService.connect(articleId);
        return ResponseEntity.ok(emitter);
    }

    @PostMapping("/comment")
    public ResponseEntity<Void> sendComment(@RequestBody Comment comment, @RequestParam("articleId") String articleId) {
        commentService.sendComment(articleId, comment);
        return ResponseEntity.ok().build();
    }
}
