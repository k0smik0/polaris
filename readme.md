#Polaris

Polaris (the name come from Polaris Northern Star) is an Android component for geolocation.  

It just provides a set of interfaces which on to develop a geolocation engine better than standard one.  

You know about LocationListener, callback, and so on;  
but we want a simple class which offers "getLocation" and "startUpdates"/"stopUpdates".  

Ok, Polaris does it. Really, it just offers interfaces and some util classes, but nothing about implementation.  
Why not?  
Because location strategies are often evolving: first was LocationListener, now using PendingIntent is recommended (according to Reto Meier tips), and so on.  

So, an implementation for Polaris is provided by [Kusor](../../../kusor).
