package com.tripsok_back.util;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RateLimiter {

	private final int maxRequests;
	private final long windowMillis;
	private final Deque<Long> requestTimestamps = new ArrayDeque<>();

	public RateLimiter(
		@Value("${groq.rate.maxRequests:30}") int maxRequests,
		@Value("${groq.rate.windowSeconds:60}") long windowSeconds
	) {
		this.maxRequests = maxRequests;
		this.windowMillis = Duration.ofSeconds(windowSeconds).toMillis();
	}

	public void acquire() {
		synchronized (requestTimestamps) {
			long now = System.currentTimeMillis();
			while (!requestTimestamps.isEmpty() && now - requestTimestamps.peekFirst() >= windowMillis) {
				requestTimestamps.pollFirst();
			}
			if (requestTimestamps.size() < maxRequests) {
				requestTimestamps.addLast(now);
				return;
			}
			long oldest = requestTimestamps.peekFirst();
			long sleepMs = windowMillis - (now - oldest) + 5; // small buffer
			try {
				if (sleepMs > 0) {
					requestTimestamps.wait(sleepMs);
				}
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
			now = System.currentTimeMillis();
			while (!requestTimestamps.isEmpty() && now - requestTimestamps.peekFirst() >= windowMillis) {
				requestTimestamps.pollFirst();
			}
			requestTimestamps.addLast(now);
		}
	}

	public void signal() {
		synchronized (requestTimestamps) {
			requestTimestamps.notifyAll();
		}
	}
}

