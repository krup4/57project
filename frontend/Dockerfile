FROM node:20-alpine as build

WORKDIR /app

COPY ./frontend/package.json ./frontend/yarn.lock ./

RUN yarn install

COPY ./frontend .

RUN yarn build

FROM nginx:alpine

COPY --from=build /app/build /usr/share/nginx/html

COPY ./frontend/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 3001

CMD ["nginx", "-g", "daemon off;"]