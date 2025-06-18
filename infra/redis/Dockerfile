# 공식 Redis 이미지를 기반으로 사용
FROM redis:7.2.0

VOLUME ["/data"]
EXPOSE 6379
# Healthcheck 추가
HEALTHCHECK --interval=10s --timeout=5s --retries=5 \
  CMD redis-cli ping || exit 1

CMD ["redis-server"]