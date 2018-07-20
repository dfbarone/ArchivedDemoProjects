# CatacombSearch

AirtTIme test android app

Build:
- Android Studio should build/run this app without issue. 

Decisions:
- DroneManager class initializes drones and access necessary web services. Web services are accessed via Volley. 
- DroneManager starts Drones to search the labyrinths. Drone class uses depth first search. End of drone search results in /report being sent.

Issues:
- Batching explore/read commands in JSON did not appear to work, response data would only hold result for explore or read, but not both.

Success!  Please send your source code and report to challenge@airtime.com

Thanks,
Dominic Barone
