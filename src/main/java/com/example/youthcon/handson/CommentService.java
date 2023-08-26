package com.example.youthcon.handson;

import com.example.youthcon.preparation.Comment;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CommentService {
    private static final String CONNECT_EVENT_NAME = "connect";
    private static final String SEND_COMMENT_EVENT_NAME = "newComment";
    private static final long SSE_EMITTER_TIME_OUT_MILLIS = 300_000L;
    private static final long EVENT_RECONNECT_TIME_MILLIS = 3_000L;
    private final ConcurrentHashMap<String, Set<SseEmitter>> container = new ConcurrentHashMap<>();


    private static void sendEvent(final SseEmitter emitter, final SseEmitter.SseEventBuilder sseEventBuilder) {
        try {
            emitter.send(sseEventBuilder);
        } catch (IOException e) {
            emitter.complete();
            throw new RuntimeException(e);
        }
    }

    public SseEmitter connect(final String articleId) {
        // 1. 새로운 Emitter 생성
        final SseEmitter sseEmitter = new SseEmitter(SSE_EMITTER_TIME_OUT_MILLIS);

        // 2. 전송할 이벤트 작성
        final SseEmitter.SseEventBuilder sseEventBuilder = SseEmitter.event()
                .name(CONNECT_EVENT_NAME)
                .data("connected!")
                .reconnectTime(EVENT_RECONNECT_TIME_MILLIS);

        // 3. 작성한 이벤트를 생성한 Emitter에 전송
        sendEvent(sseEmitter, sseEventBuilder);

        // 4. Article과 연결된 Emitter Container 생성
        final Set<SseEmitter> existSSeEmitters = container.getOrDefault(articleId, new HashSet<>());
        existSSeEmitters.add(sseEmitter);
        container.put(articleId, existSSeEmitters);
        return sseEmitter;
    }

    public void sendComment(final String articleId, final Comment comment) {
        // 1. Article과 연결된 모든 Emitter 가져오기
        final Set<SseEmitter> emitters = container.getOrDefault(articleId, new HashSet<>());

        // 2. 가져온 Emitter 에게 댓글 전송하기
        final SseEmitter.SseEventBuilder sseEventBuilder = SseEmitter.event()
                .name(SEND_COMMENT_EVENT_NAME)
                .data(comment)
                .reconnectTime(EVENT_RECONNECT_TIME_MILLIS);
        emitters.forEach(sseEmitter -> sendEvent(sseEmitter, sseEventBuilder));
    }
}
