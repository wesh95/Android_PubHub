# Android_PubHub

PubHub was the final project for the CS 3714: Mobile Software Development with Android Course at Virginia Tech. The premise of
the application is similar to that of the app "Hooked". The app pulls from an external database to get restaurant and bar deals for a
given area. The bars and restaraunts listed in the project are in downtown Blacksburg, VA. They advertise drink specials which are only
available for a limited time. The user can view the list of deals, sort them by wait time, distance to them, and cover fee. When selected
a detailed description of the deal appears on the screen, with it's location on the map. If the map Icon is selected, a google maps fragment
appears on the screen. It has pins dropped at the locations of all the deals. Final grade on the assignment: 100%.

Project Requirements:

Stability: It is essential that your project doesn’t crash. Any sort of trivial app crashing (e.g. crashing on rotation,
on wrong/blank input, switching off Wi-Fi toggle, reboot, fast input etc.) will result in a 30 point penalty. The TAs will try 
their best to reveal any unstable behavior of the app. It is very important that you thoroughly test your app before the demo. 
In general, your app shouldn’t crash or become unresponsive.

Features: While you are free to come up with the features that your app will contain, it is important that you adhere to the features 
that you determined during the previous stages of this assignment.

User Interface: The user interface will be evaluated based on the quality of the layouts and the views. 
Some considerations:
Do your layouts look awkward when moving to landscape mode?
Are you wasting a lot of screen space (unless deliberately done to make an artistic statement)?
Are the images blurry and stretched disproportionately?
Are you using fixed resolution PNGs vs using vector graphics?
Are you using plain Views vs customized ones (ex. Buttons with backgrounds) to add some style and professionalism to the 
overall look and feel?

Handling lifecycle:
We will pay a lot of attention to how well you handle lifecycle events:

Persistence: 
Does your app persist information across lifecycle events? 
Do you differentiate between short-term persistence (screen rotation) and long-term persistence (total app shutdown/reboot)?

Network: Does your app finish network operations during short-term lifecycle events? 
Does your app explicitly cancel network requests when the user exits the app?

Long-running operations: On lifecycle events, do you recreate long running operations (e.g. AsyncTask) 
or you maintain references to the existing ones?

Fragments: Are you retrieving fragments by their TAGs via fragment manager or you create new ones?

Coding style:
For this project you should ensure that the coding style clean and efficient:

Is your app using hardcoded UI strings or its using strings.xml to store strings?
Is your app using interfaces to define interactions between classes (e.g. activity inside fragment)?
Are you using colors.xml to store color values or you hardcode them?
Do you use uppercase static strings to retain constants such as fragment tags?
Are you following “high in cohesion and low in coupling”?

SCREENSHOTS:

![Image](https://github.com/wesh95/Android_PubHub/blob/master/pubhub_ScreenShots/pubhub1.JPG)
![Image](https://github.com/wesh95/Android_PubHub/blob/master/pubhub_ScreenShots/pubhub2.JPG)
