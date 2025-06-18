FROM nginx:1.25
ARG NGINX=local_nginx.conf
ENV NGINX_CONF=${NGINX}
RUN rm -rf /usr/share/nginx/html/*


COPY ${NGINX_CONF} /etc/nginx/nginx.conf

EXPOSE 80
EXPOSE 443
#
CMD ["nginx", "-g", "daemon off;"]
