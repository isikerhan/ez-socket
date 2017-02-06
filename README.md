# EZSocket
A simple Java library that provides an easy interface for exchanging messages over TCP / IP.

## Usage
Easily connect to a `MessagingServer` and send a message.

```java
OnMessageReceivedListener messageListener = new OnMessageReceivedListener() {
  @Override
  public void onMessageReceived(Message message) {
    System.out.printf("Server sent a message that is %d bytes size!\n", message.getBytes().length);
    //do something with the message
  }
};
MessagingClient client = new SocketMessagingClient(messageListener);
client.connect("localhost", 8089); //connect to 127.0.0.1:8089
client.sendMessage("Hello World!".getBytes());
```

## The Server Side
```java
OnMessageReceivedListener messageListener = new OnMessageReceivedListener() {
  @Override
  public void onMessageReceived(Message message) {
    System.out.printf("%s says %s.\n", message.getSender().toString(), new String(message.getBytes()));
    // do something with the message
  }
};

final MessagingServer server = new SocketMessagingServer(messageListener);
server.listen(8888); //listen on localhost:8888
server.setConnectionStatusListener(new ConnectionStatusListener() {

  @Override
  public void onPeerDisconnected(Socket socket) {
    //peer disconnected
    System.out.printf("Good bye %s :(", socket.getInetAddress());
  }

  @Override
  public void onConnectionEstablished(Socket socket) {
    //say welcome to the client
    SimpleAddress address = new SimpleAddress(socket.getInetAddress());
    server.sendMessage(String.format("Welcome %s!", address), address);
  }
});
```
