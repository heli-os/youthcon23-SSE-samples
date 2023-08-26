package com.example.youthcon.handson;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class CommentService {
    private static final String EVENT_NAME = "connect";
    private static final long EVENT_RECONNECT_TIME = 3_000L;

    public SseEmitter connect(String articleId) {
        // 1. 새로운 Emitter 생성
        SseEmitter emitter = new SseEmitter(300_000L);

        // 2. 전송할 이벤트 작성
        SseEmitter.SseEventBuilder sseEventBuilder = SseEmitter.event()
                .name(EVENT_NAME)
                .data("connected!")
                .reconnectTime(EVENT_RECONNECT_TIME);
        
        // 3. 작성한 이벤트를 생성한 Emitter에 전송
        // 4. Article과 연결된 Emitter Container 생성
    }
}
