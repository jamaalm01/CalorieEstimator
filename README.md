# Image-To-Calories

This is the source code for Android app that collects a food image through camera or gallery, predicts the food items and estimates the calories.
The prediction (Machine Learning task) and cal estimation part are done in a server to which this app acts as a client.

The user is generally expected to take top view photos of the food items. Each classification and calorie estimation task given by the user is considered as a project within the app.
It has the ability to store the processed output data (resultant food names and the estimated calories) of the project locally in the phone which can be viewed anytime. 
When a project is initiated it sends the project related data to a remote server and receives the result data back from the server after processing.

