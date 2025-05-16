# Используем чистый nginx для раздачи статики
FROM nginx:alpine

# Копируем собранный фронтенд (папку build/)
COPY ./frontend/build /usr/share/nginx/html

# Копируем конфиг nginx (если нужен)
COPY ./frontend/nginx.conf /etc/nginx/conf.d/default.conf

# Открываем порт (обычно 80 для nginx)
EXPOSE 3001

# Запускаем nginx
CMD ["nginx", "-g", "daemon off;"]