'use strict';

/** Simple in-memory fixed-window limiter. For several server instances use a shared store (e.g. Redis). */
function createRateLimiter({ limit, windowMs = 60000 }) {
  const hits = new Map();
  const timer = setInterval(() => {
    const now = Date.now();
    for (const [key, entry] of hits) if (entry.resetAt <= now) hits.delete(key);
  }, windowMs);
  timer.unref();

  return {
    /** @returns {{allowed:boolean, retryAfterSec:number}} */
    check(key) {
      const now = Date.now();
      let entry = hits.get(key);
      if (!entry || entry.resetAt <= now) {
        entry = { count: 0, resetAt: now + windowMs };
        hits.set(key, entry);
      }
      entry.count += 1;
      return {
        allowed: entry.count <= limit,
        retryAfterSec: Math.max(1, Math.ceil((entry.resetAt - now) / 1000)),
      };
    },
    stop() {
      clearInterval(timer);
    },
  };
}

module.exports = { createRateLimiter };
