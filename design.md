# Design

DataServer
- Uses the `@API(int flag)` annotation and reflection to define accessible API's.
- Uses a thread to avoid blocking the primary thread.
- Hooks into the shutdown hook to gracefully cleanup and exit the server process.

DataClient
- Defines a static method for sending requests to the server. It uses an interface as a callback method to indicate that the server returned a response.

UIExample
- Uses `DataClient.requestData` to request data from the server without blocking the primary thread.