# EZSocket
A simple Java library that provides an easy interface for exchanging messages over TCP / IP.

## Usage
Easily connect to a `MessagingServer` and send a message.

```java
OnMessageReceiveListener messageListener = new OnMessageReceiveListener() {
  @Override
  public void onMessageReceive(Message message) {
    System.out.printf("Server sent a message that is %d bytes size!", message.getBytes().length);
    //do something with the message
  }
};
MessagingClient client = new SocketMessagingClient(messageListener);
client.connect("localhost", 8089); //connect to 127.0.0.1:8089
client.sendMessage("So EZ!".getBytes());
```
