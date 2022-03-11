# networking-a1

`DataClient` Provides a helper method (`DataClient.requestData`) for communicating with a `DataServer` instance through a callback to relay server responses. The class provides three simple tests when run as a standalone program.

`DataServer` Implements the DateProtocol protocol (see [protocol.txt](protocol.txt)) and opens a socket server to run on port `5217`. Java reflection and a custom `API` annotation is used to define available API operations at runtime.
```java
@API(flag = Protocol.FLAG_DATE)
public static String GetDate() {
  return FORMAT_DATE.format(Calendar.getInstance().getTime());
}
```

`UIExample` Provides a Java Swing UI to demonstrate communication between a client and the server.
