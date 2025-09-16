package com.tripsok_back.util;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenBucketRateLimiter {

	private final int capacity = 30;
	private final int refillTokens = 30;
	private final long refillPeriodMillis = 60;

	private double availableTokens = 30;
	private long lastRefillTimeMs = System.currentTimeMillis();

	public void acquire(int cost) {
		if (cost <= 0)
			cost = 1;
		synchronized (this) {
			for (; ; ) {
				refill();
				if (availableTokens >= cost) {
					availableTokens -= cost;
					return;
				}
				long waitMs = timeUntilNextRefillMs();
				try {
					this.wait(Math.max(1L, waitMs));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
			}
		}
	}

	private void refill() {
		long now = System.currentTimeMillis();
		if (now <= lastRefillTimeMs)
			return;
		long elapsed = now - lastRefillTimeMs;
		if (elapsed >= refillPeriodMillis) {
			long periods = elapsed / refillPeriodMillis;
			double add = periods * (double)refillTokens;
			availableTokens = Math.min(capacity, availableTokens + add);
			lastRefillTimeMs += periods * refillPeriodMillis;
			this.notifyAll();
		}
	}

	private long timeUntilNextRefillMs() {
		long now = System.currentTimeMillis();
		long next = lastRefillTimeMs + refillPeriodMillis;
		return Math.max(0L, next - now);
	}
}

