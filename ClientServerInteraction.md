# Introduction #

The client server will need a standard format for commands to be sent over the network.

# Proposal 1 #

We can use a combination of **ObjectOutputStream** / **ObjectInputStream** and send serialized objects over the network. A suggested way would be to send two strings as headers then follow the headers by any other objects.

One string describes the protocol (so other random programs don't accidentally connect). Another string describes the command and finally we have any other serialized objects required by the command type.

## Message Format ##
|KIMT _Version_|
|:-------------|
|_Command Type_|
|_Serialized Objects_|

## Example (from client) ##
| KIMT 1.0 |
|:---------|
| UPLOAD LIBRARY |
| _Serialized Library Object_ |

An in-code example can be found in the doInBackground method of the UploadLibraryTask private class. This private class is nested within the KIMTSync class.