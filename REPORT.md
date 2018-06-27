# Final Report Programmeerproject - PlantBase

## Overview

PlantBase is an app with which you can track the growth of your plants. 
It has a database with infoemation of 19 vegetables and herbs with you can grow in your window sill. 
After creating an account, you can add a plant to your collection and track its growth by uploading pictures and setting a time interval between notifications to remind you to water the plant.
The user can let the app know when a plant dies or when he/she wattered the plant.
The app uses a FireBase databse and storage to hold all information and photos.
It is possible you insert a PlantNode into the plant pot to track the realtime conditions in which the plant is growing. 
The PlantNode is a battery powered photon particle with a light sensor, soil moisture sensor and a temperture- humidity sensor inside a 3D printed enclosure.


## Technical design

The exists of 8 activities, 4 adapters, 5 requests, 2 classes, 1 helpers and 3 files for notifications.

### Activities
#### SearchActivity
SearchActivity starts when the app is first openend. It shows the option to search for plants in de database, browse plants of go to the users current collection.
- When the user searches for a plant (and a match has been found), he/she will be taken to PlantInfoActivity.
- When the user clicks on "Browse", he/she will be taken to SearchListActivity.
- When the user cicks on "My Plants", he/she will be taken to MyPlantListActivity.

#### SearchListActivity
SearchListActivity uses PlantListAdpater to show all plants from the database.
- When the user clicks on a plant, he/she will be taken to PlantInfoActivity.

#### PlantInfoActivity
PlantInfoActivity shows information about a specific plant. It shows the latin name for the plant, optimal soil pH, temperature range and how much water the plants need on a scale 1 - 5.
- When the user clicks on "Add this plant to my collection", the user will be taken to MyPlantActivity for that plant and the plant is added to his/her collection.

#### MyPlantsListActivity
MyPlantsListActivity shows the users collection of living and deceased plants, using MyPlantListAdpater.
- When the user clicks on a plant, the user will be taken to MyPlantActivity for that plant.

#### MyPlantActivity
MyPlantActivity has three tabs: Status, Watering and Data.
- Status: Here the user can find the current status of the plant, see since when and for how many days the plant has been growing, whether the plant is connected to a PlantNode, see the pictures he has uploaded adn let the app know when the plant has died or got wattered. 
The user can also delete the plant.
- Watering: The user can set to receive notifications to water the plant. The interval between notifications is nver, daily, every two days, weekly or wehnever the PlantNode registers a low soil moisture.
- Data: If the plant is connected to a PlantNode, this tab will show a graph with a history of all the values the PlantNode registers. 

#### LinkingActivity
LinkingActivity uses the AdapterLinkMyPlant and AdapterLinkNode to show all living plants and PlantNode.
- When the user clicks on a plant it gets highlighted, if he/she then clicks on a PlantNode, the plant and PlantNode get connected.
- When the user long presses on a plant he/she will be prompted wheter he/she wants to disconnect the plant and PlantNode.

#### LoginActivity
In the LoginActivity the user can create a new account or login with an existing account.
After creating an account or loging in, the user will be send to the AccountActivity.

#### AccountActivity
In the AccountActivity the user can see how many plants he/she is growing and how many have died.
- When the user presses "Logout", he/she will be send to LoginActivity.

### Adpaters
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
RequestPlantNode queries the databse for the different values uploaded by the PlantNode. It can either return last 1000 value sets, or last singe value set.

### Helpers
#### BottomNavigationViewHelper 
BottomNavigationViewHelper handles the bottom navigation bar for the app. It swtiches between activities and highlights current activity.

### Notification
#### BackgroundTask 
BackgroundTask performs calculations in the background and determines wheter a plant needs water. If that's the case BackgroundTask asks for a new notification.

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


