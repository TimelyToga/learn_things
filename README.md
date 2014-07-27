# LearnThings

LearnThings is an app that is meant to serve as a passive flashcard study app. The flashcards will be presented in notifications.

Initially, the app will be targeted to highschool kids studying for the SAT, with plans for general trivia, specific topics, and self-created flash cards.

## Settings Activity 

The settings actity is the place where users can decide how frequently they want notifications to appear. This area is where I am left with the most questions. Currently there are three implemented methods for chosing the frequency. A logarithmic slider for total posts in a day, a list of frequencies (25 times per day, 50 times per day), and a list of time intervals between notification events. 

//TODO: Need to implement hours of operation, so notifications will only arrive between certain hours of the day/night.

![Settings](https://github.com/TimelyToga/learn_things/blob/master/pics/Screenshot_2014-07-27-02-13-11.png "Settings")

## Notification Types
There are two notification types, to handle different learning and testing styles: Multiple Choice and Free Response.All questions will be stored in a single csv file. The format of the individual questions will determine how there are processed and stored in memory. 

I am currently planning to generate the questions from the csv file, and then export them to json. This json file will contain the universal ids, which will be useful to know when on the internet. Also, the JSON format will aid in file transfer and storage. Less ambiguous, troublesome to reinitiate. 

### Multiple Choice

![MC Question](https://github.com/TimelyToga/learn_things/blob/master/pics/Screenshot_2014-07-27-02-12-59.png "MC question")


Format:


`Question text with question mark?,@Correct Answer,#Incorrect Answer 1,#Incorrect Answer2`


Correct Answers are prepended with a `@` without any leading whitespace.

Incorrect Answers are prepended with a `#` without any leading whitespace.


Upon answering the notification's question, the user is a brought to a feedback screen. The success or failure of the answer is immediately reported. After a short timer (currently 1700ms), the app closes automatically. 

![MC Question Feedback](https://github.com/TimelyToga/learn_things/blob/master/pics/Screenshot_2014-07-27-02-13-29.png "MC question Feedback")



### Freeresponse
![FR Question](https://github.com/TimelyToga/learn_things/blob/master/pics/Screenshot_2014-07-27-02-13-48.png "FR question")


`Question text with question mark?,&Correct Answer`

The correct answer is prepended with a `&` without any leading whitespace.

Upon viewing a free response notification, the user is then to think of their response before clicking on the notification. After they have clicked through into the app, the answer is displayed and grading buttons (Correct, Incorrect) are available for the user to indicate their performance. After reporting correctness, the app gives feedback, it closes automatically with the same parameters as the MC Feedback Screen.

![FR Question Display Answer](https://github.com/TimelyToga/learn_things/blob/master/pics/Screenshot_2014-07-27-02-13-54.png "FR question Display Answer") Before reporting correctness.
![FR Question Display Feedback](https://github.com/TimelyToga/learn_things/blob/master/pics/Screenshot_2014-07-27-02-14-00.png "FR question Display Feedback") After reporting correctness.


