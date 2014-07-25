# LearnThings

LearnThings is an app that is meant to serve as a passive flashcard study app. The flashcards will be presented in notifications.

Initially, the app will be targeted to highschool kids studying for the SAT, with plans for general trivia, specific topics, and self-created flash cards.

## Notification Types
There are two notification types, to handle different learning and testing styles: Multiple Choice and Free Response.

All questions will be stored in a csv file.

### Multiple Choice

![MC Question](https://github.com/TimelyToga/learn_things/blob/master/pics/mc_question_notif.png "MC question")



Format:


`Question text with question mark?,@Correct Answer,#Incorrect Answer 1,#Incorrect Answer2`


Correct Answers are prepended with a `@` without any leading whitespace.

Incorrect Answers are prepended with a `#` without any leading whitespace.


### Freeresponse
![FR Question](https://github.com/TimelyToga/learn_things/blob/master/pics/fr_question_notif.png "FR question")
`Question text with question mark?,&Correct Answer`

The correct answer is prepended with a `&` without any leading whitespace.
