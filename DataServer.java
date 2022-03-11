import java.net.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

class DataServer {

  @Retention(RetentionPolicy.RUNTIME)
  public @interface API {
    int flag();
  }

  private final int SERVER_PORT = 5217;
  ServerSocket serverSocket;
  HashMap<Integer, Method> apiMethods;
  Thread serverThread;

  /**
   * A server that implements the DateProtocol
   * 
   * @param apiMethods A hashmap of flags and invokable methods to be loaded into
   *                   the API
   * @throws IOException
   */
  public DataServer(HashMap<Integer, Method> apiMethods) throws IOException {
    serverSocket = new ServerSocket(SERVER_PORT);
    this.apiMethods = apiMethods;
    serverThread = new Thread(new Runnable() {
      public void run() {
        try {
          while (!Thread.interrupted())
            loop();
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (IOException e) {
          if (!Thread.interrupted()) {
            // System.out.println("Unknown IO exception.");
            e.printStackTrace();
          }
        }
      }
    });
  }

  /**
   * The primary operation of accepting a connection, validating the protocol, and
   * returning a result
   * 
   * @throws IOException
   */
  public void loop() throws IOException {
    System.out.println("Waiting for new connection ...");
    Socket soc = serverSocket.accept();
    DataOutputStream out = new DataOutputStream(soc.getOutputStream());
    InputStream in = soc.getInputStream();

    try {
      byte[] arr = new byte[4];
      int read_bytes = in.read(arr, 0, 4); // Read the HEADER

      if (read_bytes == -1)
        return;

      if (!Protocol.HEADER.equals(new String(arr))) {
        throw new EOFException("Unknown protocol"); // Check if the header matches
      }

      in.read(arr, 0, 1); // Read the flag

      if (apiMethods.containsKey((int) arr[0])) {
        // Find the requested method and invoke it
        String result = (String) apiMethods.get((int) arr[0]).invoke(API.class);
        out.writeBytes(result);
      } else {
        throw new EOFException("Flag not found: " + arr[0]);
      }
    } catch (Exception e) {
      e.printStackTrace();
      out.writeBytes("ERROR: ");
      out.writeBytes(e.getMessage());
      System.out.println("Attemping to recover.");
    } finally {

      soc.close();
      System.out.println("Completed old connection.");
    }
  }

  /**
   * Start the server thread. This does not block the primary thread
   */
  public void start() {
    serverThread.start();
  }

  /**
   * Attempt to stop the server
   * @throws InterruptedException
   */
  public void stop() throws InterruptedException {
    serverThread.interrupt();
    serverThread.join(12);
  }

  /**
   * Cleanup of the server to close remaining sessions and the ServerSocket
   */
  public void cleanup() {
    try {
      serverSocket.close();
      System.out.println("Cleanup finished.");
    } catch (IOException e) {
      System.err.println("Something went wrong when cleaning the server.");
      e.printStackTrace();
    }
  }

  static class DateServerAPI {
    private static SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("MM/dd/yyyy");
    private static SimpleDateFormat FORMAT_TIME = new SimpleDateFormat("hh:mm:ss");

    @API(flag = Protocol.FLAG_DATE)
    public static String GetDate() {
      return FORMAT_DATE.format(Calendar.getInstance().getTime());
    }

    @API(flag = Protocol.FLAG_TIME)
    public static String GetTime() {
      return FORMAT_TIME.format(Calendar.getInstance().getTime());
    }
  }

  public static void main(String args[]) throws Exception {
    Method[] methods = DateServerAPI.class.getMethods();
    HashMap<Integer, Method> apiMethods = new HashMap<>();

    for (Method method : methods) {
      API annotation = method.getAnnotation(API.class);
      if (annotation == null)
        continue; // Skip the method as it is not annotated

      int flag = annotation.flag();
      apiMethods.put(flag, method); // Add the method. Assumes each API annotation is unique
    }

    DataServer server = new DataServer(apiMethods);
    server.start();

    // Hook into the shutdown event to provide a graceful shutdown process
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      public void run() {
        try {
          server.stop();
          server.cleanup();
        } catch (InterruptedException e) {
          e.printStackTrace();
        } finally {
          System.out.println("Server closed.");
        }
      }
    }));
  }

}