# LearnThings

LearnThings is an app that is meant to serve as a passive flashcard study app. The flashcards will be presented in notifications.

Initially, the app will be targeted to highschool kids studying for the SAT, with plans for general trivia, specific topics, and self-created flash cards.

## Notification Types
There are two notification types, to handle different learning and testing styles: Multiple Choice and Free Response.

All questions will be stored in a csv file.

### Multiple Choice

![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Logo Title Text 1")



Format:


`Question text with question mark?,@Correct Answer,#Incorrect Answer 1,#Incorrect Answer2`


Correct Answers are prepended with a `@` without any leading whitespace.

Incorrect Answers are prepended with a `#` without any leading whitespace.


### Freeresponse
![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Logo Title Text 1")

`Question text with question mark?,&Correct Answer`

The correct answer is prepended with a `&` without any leading whitespace.
