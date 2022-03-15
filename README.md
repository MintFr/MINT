# MINT

Mint the best way to breath 

Project’s description

The main goal of the app is to provide healthier itineraries in regards to atmospheric pollution.
Project’s Content

What does the application

The application's principal feature is to calculate less polluted itineraries within the city of Nantes, France. 
The application also provides pollen alerts thanks to the RNSA’s data, which won’t be available in the first place but can be accessed by asking them, the data format is as follow : 

The application is also able to follow an itinerary step by step thanks to geolocalisation.
	When an itinerary is chosen it also saves the exposure to pollution during this route and can show a graph recapitulating the exposition of the user.
In the profile tab, a lot of options can be defined, such as sensibility towards pollution and pollen, prefered means of transportation, prefered addresses and access to the parameters. Within the parameters tab you can make the writings bigger and access Q&A and legal notices.
For the itinerary calculation we use a home made web application, of which you can find the code here : https://github.com/MintFr/ItineraryServ. Explanations regarding its functioning are available in the read me of this repository.
	

Encountered challenges & futures features

Here is a listing of the various remaining difficulties our team encountered during the dev : 

The way the app is requiring the location permission is not very satisfying. We tried to centralize the corresponding code into a fragment but didn't manage to do so. Right now, every activity which is likely to use the localisation uses the same piece of copy-paste code.
The selection of an itinerary menu is not scrolling accordingly. Sometimes it’s the whole menu which scrolls down to display the map and not the itinerary list.
The exposition graph remains with some bugs that we didn't have the time to fix

Apart of these remaining issues, we have defined some axes of improvement that we wished to do but we lacks of time :

Taking account of the real-time traffic to have a better precision of the duration for the car drive
Delayed departures : we questioned the pertinence of this feature because it needs a total new model. The quantity of work seems not worth, for such a specific feature, for us.
The storage of the personal exposition data is made with the SharedPreference class but we think that maybe a SQLite database would be better.
The integration of the TAN data to have a real time schedule of the common transports
The pollen data are currently only at the scale of the city. With other data it would be possible to have a street by street pollen evolution like we already have for the pollution
We cannot use the autocomplete of addresses because it’s a Google charged API that we couldn’t afford but it would be a great addition.
Taking account of the user’s sensibility to suggest more suitable itineraries 
Allow to choose a maximal lengthening of the duration of the itineraries to try to avoid the pollution
Improve of the accessibility of the app

Recap of the used technologies and why they were chosen (Deswarte)

The main thing we used for the project was android studio to code the application. We used mainly native libraries from android. Android studio is an IDE developed by google specifically to create android, the choice was quite obvious since it is a very powerful tool to program android apps.
We chose to code in java because it was a language we already knew and we needed to start fast and didn’t have the time to learn kotlin properly.

libraries outside native android : 
OSMdroid : open source mapping for android, used to show maps and get addresses for itineraries calculation. It was chosen because we use OSM data for itinerary calculation.
https://github.com/PhilJay/MPAndroidChart : free library to show graphs within android. It was chosen because it was the best free library to show graphs we could find.
How to Install and Run the Project

To get the project for your personal use, it's highly recommended to use android studio. However the zip can be downloaded from github and opened in any IDE. 
In android studio you can either open the project from the unzipped file or connect to your github account and clone the project from the URL.
In the newer version of Android studio there’s an option when you first clone the project, “use the project’s SDK” which is useful to avoid doing part of this settings.  

The relevant settings :
android gradle plugin version : 7.0.3
Gradle version : 7.0.2
Gradle JDK : 1.8 (java 8)
min SDK : 17.0
target SDK : 30.0 (android 11)



How to Use the Project

Choosing & follow an itinerary 

When you enter your itinerary you can specify your mean of transport for this
specific search with the “Option” button and then send your search with the “Rechercher” button.
	The next screen is about selecting which itinerary you want to pick with various information : means of transport, duration, exposition and length. Then by clicking on an itinerary it displays the detailed steps of this itinerary.
Finally after clicking “Suivre cet itinéraire” you enter in the real time itinerary mode with the instructions to follow the itineraries 

Modif of preferences in profile 

You can specify your sensibilities to the pollution or the pollen, add favorite addresses, select your favorite means of transport or access the settings of the app.


Pollen 

When the app opens a pop-up appears to inform the user about the types of pollen present in the city. There is also a button in the right bottom of the main activity, its color changed to match the severity of the pollen alerts. When you press this button, the pop-up appears again.

Recap of the tests & How they work 

For now, only a few tests are implemented. We have not yet succeeded to implement tests for activity classes. The other classes contain only getters, setters and basic constructors, some tests were made for these classes but they are not really that useful.



