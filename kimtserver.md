The kimt-server is a simple synchronization server that allocates clients to groups and provides a mean of synchronising multiple clients.

It currently uses a simple Networking Framework involving the use of NetworkMonitors and NetMessages.

It's important to note that this system uses Serialized objects, and any objects that are to be wrapped and send need to be Serializable.

Simple networking framework.
_com.waikato.kimt.networking_
| **Class Name:** | **Description:** |
|:----------------|:-----------------|
| NetworkMonitor  | Sits at either end of a connection (server / client) and mediates data transfer between the two. |
| NetMessage      | -Base object which can be inherited. Used to store information for sending over the network via NetworkMonitor.|

A NetworkMonitor is created as such:
```
try
{
   //Create socket and use it to instantiate NetworkMonitor
   Socket s = new Socket(host,port);
   NetworkMonitor network = new NetworkMonitor(s);
}
catch (IOException ex}
{
   System.err.println("Error: " + ex.getMessage());
}
```

A Message can be sent via:
```
try
{
   //Create socket and use it to instantiate NetworkMonitor
   Socket s = new Socket(host,port);
   NetworkMonitor network = new NetworkMonitor(s);

   //Send a simple message using the base class NetMessage
   NetMessage msg = new NetMessage("derp");
   network.sendMessage(msg);
}
catch (IOException ex}
{
   System.err.println("Error: " + ex.getMessage());
}
```

Reading Messages is done as so:
```
try
{
   //Create socket and use it to instantiate NetworkMonitor
   Socket s = new Socket(host,port);
   NetworkMonitor network = new NetworkMonitor(s);

   //Send a simple message using the base class NetMessage
   NetMessage msg = new NetMessage("derp");
   network.sendMessage(msg);

   //Loop continuously, reading and printing messages
   NetMessage message;
   while(true)
   {
      m = network.readMessage();  //Get the last read message from the NetworkMonitor
      if (m != null)   //If message is not null
      {
         System.out.println(m.message);   //Print contents of message
         network.disposeMessage();   //Dispose of message (once we are finished with it)
      }
   }
}
catch (IOException ex}
{
   System.err.println("Error: " + ex.getMessage());
}
```



