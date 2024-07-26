# Image Gallery

Image Gallery is an open-source image sharing platform alternate to google photos will try to replicate like social media platform.

# Table Of Contents:

- [Image Gallery](#image-gallery)
- [Table Of Contents:](#table-of-contents)
  - [Updates](#updates)
    - [Update 4](#update-4)
    - [Update 3](#update-3)
    - [Update 2](#update-2)
    - [Update 1](#update-1)
  - [Project Demo 🔗](#project-demo-)
      - [Live Link](#live-link)
      - [Video Presentation](#video-presentation)
  - [Information](#information)
    - [Technologies](#technologies)
    - [Dependencies](#dependencies)

## Updates

### Update 4

- Delete account bug fixed. On deleting the user **images** with respect to that **particular user** was **not deleting** from database

### Update 3

- Earlier I used **AWS Relational Database Service** but again it started giving emails related to **bill $** then migrate to **MySQL** of [**Railway** App](https://railway.app).
- Earlier I also used **AWS Elastic Beanstalk** but I was not able to make my project live for longer period of time because of pay as you use model, So deployed my porject on [Railway App](https://railway.app).
- Also **implemented pagination** for **images** which are **visible to user**, and implemented **pagination** for **list of all users** as well as **sorting by name, email** for **admin dashboard**.

### Update 2

- Removed **file storing system** of images and added **AWS S3** integration to store images.
- On update first deleting the old image object in S3 and then updating new image.
- Also I have added 1 option in upload image form where user can select **private or public**, if selected as **public** then all the **user without login** can also **view public images**.

### Update 1

- In this project in update and delete button of images as well as in profile section, I was sending **userId** as well as **image id** and I found out that any user can **inspect (browser tool)** and may affect **some one else images or content**.
- I used the concept of session and I have **deleted** all the **hidden form field** from my form.
- There will be no chances of changing user id from developer tools.
- Now I am only sending image id and I have validate image id with session user. If matched then only user will be able to update or delete and of the content.

- Initial Commit:
  - Added Login and Logout authentication with role based authorization.
  - Added forms for signup as well as forms for adding new image.
  - Added dashboard to view image as well as profile page.

## Project Demo 🔗

#### Live Link 

- [Photo Sharing App](https://image-gallery-production.up.railway.app/)

#### Video Presentation

- [Photo Sharing App Working Presentation](https://www.linkedin.com/posts/memon-abdul-gani_tech-springboot-aws-activity-7222093574392193025-bSy1?utm_source=share&utm_medium=member_desktop)

## Information

In this project there is 1 open page where user can interact as well as view **public images** of **other users** and when user will click on any of the button login page will be opened.
**Only one navbar** I have created which I am reusing on multiple pages using **thymeleaf replace**.

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
9. AWS SDK
