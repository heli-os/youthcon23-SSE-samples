package com.example.youthcon.handson;

import com.example.youthcon.preparation.Comment;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Service
public class CommentService {
    private static final String EVENT_NAME = "connect";
    private static final long SSE_EMITTER_TIME_OUT_MILLIS = 1_000L;
    private static final long EVENT_RECONNECT_TIME_MILLIS = 500L;
    private final HashMap<String, Set<SseEmitter>> container = new HashMap<>();


    private static void sendEvent(SseEmitter emitter, SseEmitter.SseEventBuilder sseEventBuilder) {
        try {
            emitter.send(sseEventBuilder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SseEmitter connect(String articleId) {
        // 1. 새로운 Emitter 생성
        SseEmitter sseEmitter = new SseEmitter(SSE_EMITTER_TIME_OUT_MILLIS);

        // 2. 전송할 이벤트 작성
        SseEmitter.SseEventBuilder sseEventBuilder = SseEmitter.event()
                .name(EVENT_NAME)
                .data("connected!")
                .reconnectTime(EVENT_RECONNECT_TIME_MILLIS);

        // 3. 작성한 이벤트를 생성한 Emitter에 전송
        sendEvent(sseEmitter, sseEventBuilder);

        // 4. Article과 연결된 Emitter Container 생성
        Set<SseEmitter> existSSeEmitters = container.getOrDefault(articleId, new HashSet<>());
        existSSeEmitters.add(sseEmitter);
        container.put(articleId, existSSeEmitters);
        return sseEmitter;
    }

    public void sendComment(String articleId, Comment comment) {
        
    }
}
