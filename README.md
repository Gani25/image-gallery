# Image Gallery

Image Gallery is an open-source image sharing platform alternate to google photos will try to replicate like social media platform

## Updates

1. Update 1:
1. In this project in update and delete button of images as well as in profile section, I was sending userId as well as image id and I found out that any user can inspect (browser tool) and may affect some one else images or content.
1. I used the concept and session and I have deleted all the hidden form field from my form.
1. There will be no chances of changing user id from developer tools.
1. Now I am only sending image id and I have validate image id with session user. If matched then only user will be able to update or delete and of the content.

1. Will Start updating this repo once I start pushing the updates

## Information

In this project there is 1 open page where user can interact and when user will click on any of the button login page will be opened.
Only one navbar I have created which I am reusing on multiple pages using thymeleaf replace.

There is one registration page in this page user can signup, and create an account, and during signup user have to provide the profile picture, this profile picture we are storing in DB. On signup new user will be created and by default his role is user and then user will be redirect to login page

After logged in login and signup button will be hidden and logout button will be visible. And on nav bar 1 button will be visible on click he can upload the images.

After login user will be redirected to dashboard where he can view all his photos and even update and delete the photos

### Technologies

- Backend
  - Spring Boot
  - Spring Security
  - Spring DATA JPA
  - Hibernate (ORM -> Relationship between 2 or more tables)
  - MySQL
- Frontend
  - HTML
  - CSS
  - JS
  - Thymeleaf (Rendering Data From Server into Views)

### Dependencies

1. Spring Web
2. Spring Security
3. Spring Dev Tools
4. Spring Data JPA
5. Lombok
6. Thymeleaf
7. Validation
8. MySQL Connector
