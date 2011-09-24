Server uses nested groups to represent groups and subgroups in the server.
A group has an owner device, a title and 0 or more nested groups

Upon connection to the server, the client retrieves a list of available servers
The client then sends a connectToGroup message, and then retrieves stuff like the greenstone url

All networkable objects (objects that send state information to remote devices) implement networkable interface