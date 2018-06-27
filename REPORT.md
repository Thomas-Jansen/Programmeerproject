# Final Report Programmeerproject - PlantBase

## Overview

PlantBase is an app with which you can track the growth of your plants. 
It has a database with information of 19 vegetables and herbs with you can grow in your window sill. 
After creating an account, you can add a plant to your collection and track its growth by uploading pictures and setting a time interval between notifications to remind you to water the plant.
The user can let the app know when a plant dies or when he/she watered the plant.
The app uses a FireBase database and storage to hold all information and photos.
It is possible you insert a PlantNode into the plant pot to track the real-time conditions in which the plant is growing. 
The PlantNode is a battery powered photon particle with a light sensor, soil moisture sensor and a temperature- humidity sensor inside a 3D printed enclosure.


## Technical design

The exists of 8 activities, 4 adapters, 5 requests, 2 classes, 1 helpers and 3 files for notifications.

### Activities
#### SearchActivity
SearchActivity starts when the app is first opened. It shows the option to search for plants in de database, browse plants of go to the user’s current collection.
- When the user searches for a plant (and a match has been found), he/she will be taken to PlantInfoActivity.
- When the user clicks on "Browse", he/she will be taken to SearchListActivity.
- When the user clicks on "My Plants", he/she will be taken to MyPlantListActivity.

#### SearchListActivity
SearchListActivity uses PlantListAdpater to show all plants from the database.
- When the user clicks on a plant, he/she will be taken to PlantInfoActivity.

#### PlantInfoActivity
PlantInfoActivity shows information about a specific plant. It shows the Latin name for the plant, optimal soil pH, temperature range and how much water the plants need on a scale 1 - 5.
- When the user clicks on "Add this plant to my collection", the user will be taken to MyPlantActivity for that plant and the plant is added to his/her collection.

#### MyPlantsListActivity
MyPlantsListActivity shows the users collection of living and deceased plants, using MyPlantListAdpater.
- When the user clicks on a plant, the user will be taken to MyPlantActivity for that plant.

#### MyPlantActivity
MyPlantActivity has three tabs: Status, Watering and Data.
- Status: Here the user can find the current status of the plant, see since when and for how many days the plant has been growing, whether the plant is connected to a PlantNode, see the pictures he has uploaded and let the app know when the plant has died or got watered. 
The user can also delete the plant.
- Watering: The user can set to receive notifications to water the plant. The interval between notifications is never, daily, every two days, weekly or whenever the PlantNode registers a low soil moisture.
- Data: If the plant is connected to a PlantNode, this tab will show a graph with a history of all the values the PlantNode registers. 

#### LinkingActivity
LinkingActivity uses the AdapterLinkMyPlant and AdapterLinkNode to show all living plants and PlantNode.
- When the user clicks on a plant it gets highlighted, if he/she then clicks on a PlantNode, the plant and PlantNode get connected.
- When the user long presses on a plant he/she will be prompted whether he/she wants to disconnect the plant and PlantNode.

#### LoginActivity
In the LoginActivity the user can create a new account or login with an existing account.
After creating an account or logging in, the user will be send to the AccountActivity.

#### AccountActivity
In the AccountActivity the user can see how many plants he/she is growing and how many have died.
- When the user presses "Logout", he/she will be send to LoginActivity.

### Adapters
- PlantListAdpater
- MyPlantListAdpater
- AdapterLinkMyPlant
- AdapterLinkNode

### Requests
#### RequestPlants
RequestPlants queries the database for all Plants and returns an Arraylist<Plant> in the callback.

#### RequestMyPlants
RequestMyPlants queries the database for all MyPlants and returns an Arraylist<MyPlant> in the callback, sorted by living and deceased plants.

#### UpdateMyPlantRequest
UpdateMyPlantRequest can update a MyPlant in the database by reuploading the object.
It can also delete a MyPlant from the database.

#### StorageRequest
StorageRequest queries the storage for all photos for a specific MyPlant. It can also delete a photo from the storage.

#### RequestPlantNode
RequestPlantNode queries the database for the different values uploaded by the PlantNode. It can either return last 1000 value sets, or last singe value set.

### Helpers
#### BottomNavigationViewHelper 
BottomNavigationViewHelper handles the bottom navigation bar for the app. It switches between activities and highlights current activity.

### Notification
#### BackgroundTask 
BackgroundTask performs calculations in the background and determines whether a plant needs water. If that's the case BackgroundTask asks for a new notification.

#### SensorRestarterBroadcastReceiver
SensorRestarterBroadcastReceiver is a simple helper for BackgroundTask.

#### NotificationClass
NotificationClass creates a new notification with the names of the plants that need water.

### Classes
#### Plant
The Plant class creates an object with the following values:
- String name
- String latinName
- float maxTemp
- float maxpH
- float minTemp
- float minpH
- float watering
- String imageID

#### MyPlant
The MyPlant class creates an object with the following values:
private String name
- String latinName
- String status
- float maxTemp
- float maxpH
- float minTemp
- float minpH
- float watering
- Long startdate
- boolean alive
- boolean connected
- String arduinoName
- int waternotify
- String imageID
- ArrayList<String> addedImages
- String avatarImage
- Long lastwatered

## Development process

The biggest challenges during the making of the app were the following:
#### Getting plant data stored in the database in the right format.
It took several tries to get the information about the plants in the database in the right format. After a couple of tries someone suggested uploading Plant and MyPlant classes to the database. This helped a lot in uploading and retrieving the information. 

#### PlantNode
At first I ordered a NodeMCU to be the brain for the PlantNode. Upon arriving I saw it had only 1 analog input and the device needed at least 2 analog inputs. I ordered a Photon Particle and started looking for code to send data to the FireBase Database. I found some code, but it took quite a while to learn how the code worked and how to change is so it sends the data I need. In the end it works great.

#### FireBase Storage
Writing code to upload images to the storage didn't take much time at all. Retrieving the images from the storage so they can be used in the app was the problem. I searched the web for a solution but couldn't find any that worked for me. Marijn Jansen proposed directly using the Uri that gets returned when uploading an image. This worked perfectly and it is a more elegant solution to the problem. 

#### Adding pictures
Implementing a function for taking a photo or choosing an existing photo from the gallery wasn't that difficult. The problem was that the two methods produce different formats as output. The solution is chose was creating a bitmap from the output of both methods and converting that to JPG. This made the dimensions of the photo taken quite small. If I had more time, I would try to refine the function so it saves larger images.

#### Notifications
It took a couple of days to get notifications working. I tried different methods of timed functions so the app would keep sending notifications when a plant needs water. One of the problems was caused by an int overflow. I tried calculating the time between dates using the milliseconds, which caused the app to send notifications continuesly. After a while i found a method that works, but it is still not perfect. It would be best to let a server send notifications instead of the app itself, but I didn't implement this due to the time limit.

#### Updating MyPlant
Several functions require update the MyPlant object in the database. This caused a lot of problems and loops. Setting a SingleEvenListener on the database solved most of this. If I had more time I would implement more checks to make sure a loop can never happen again, but for now it works fine.

## Changes

#### Swipe tabs
I wanted to implement a swipe listener on the tabs in MyPlantActivity, to make it more easy to change between tabs. This took a lot of time and i gave up in the end.

#### Notifiations
My plan was to send notifications whenever the PlantNode measured values that were outside the optimal growing conditions of the plant. 
Due to lack if time I only implemented notifications for water.

#### Sharing photos
I wanted to let users share pictures of the plants they are growing. This is something I didn't have time for.

#### Setting avatars for the plant
The plan was to let users set the pictures they took as an avatar for their plant. But it was very difficult to make the picture appear pretty because of the small pictures and the fact that different phones take different size pictures.

#### Logs
My first plan was to let users write logs about how the plants are growing. In the end I chose not to implement this feature en let users take pictures instead.

#### Letting users create new plants
I wanted to let users add new plants to the database and let them share it with other users. I didn't add this feature because it is very difficult to check whether the information they entered is correct.


