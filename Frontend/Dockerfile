# Stage 1: Build the Angular app
FROM node:18.20 as build-stage

WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build --prod

# Stage 2: Serve the app using nginx
FROM nginx:alpine
COPY --from=build-stage /app/dist/it-marathon-v4/browser /usr/share/nginx/html
EXPOSE 3580
COPY nginx.conf /etc/nginx/conf.d/default.conf
CMD ["nginx", "-g", "daemon off;"]
