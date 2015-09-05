# Introduction #
This is a list of the client/server commands and what they do.

# Details #
|Command|Description|
|:------|:----------|
|GET SYNC|Will request and download the current sync object from the sync server.|
|GET LIBRARY|Will request and download the most up-to-date library object from the sync server.|
|PUT SYNC 

&lt;sync-obj&gt;

|Will upload the clients sync object if the server allows it. The sync server will respond with either a "FORBIDDEN" or "SUCCESS" code. If sucessful, an update notification will then be broadcast to all clients giving them the opportunity to use **GET SYNC**|
|LOGIN 

&lt;name&gt;

|Will upload an object representing the client. This will then be added to the servers internal list of clients, grouping can be done at this stage.|
|WELCOME 

&lt;boolean&gt;

|Will send a command to the client which welcomes them to the sync-server, it also tells the client if they're the leader or not.|