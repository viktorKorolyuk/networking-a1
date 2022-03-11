import java.io.*;
import java.net.*;

public class DataClient {
  public static void main(String args[]) throws Exception {
    // Expect: Date
    requestData(0b01, new Callback<String>() {
      @Override
      public void onResult(String s) {
        System.out.println(s);
      }
    });

    // Expect: Time
    requestData(0b10, new Callback<String>() {
      @Override
      public void onResult(String s) {
        System.out.println(s);
      }
    });

    // Expect: Error message
    requestData(0b11, new Callback<String>() {
      @Override
      public void onResult(String s) {
        System.out.println(s);
      }
    });
  }

  /**
   * Request data from the DataServer
   * 
   * @param flag     Main body to be sent
   * @param callback A callback listener that runs when the server returns data
   */
  public static void requestData(int flag, Callback<String> callback) {
    Thread th = new Thread(new Runnable() {

      @Override
      public void run() {
        try (Socket soc = new Socket(InetAddress.getLocalHost(), 5217)) {
          OutputStream os = soc.getOutputStream();

          os.write(Protocol.HEADER.getBytes()); // Write the header
          os.write(flag); // Write the flag
          os.flush();

          BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
          String time = in.readLine();
          callback.onResult(time);
        } catch (IOException e) {
          e.printStackTrace();
          callback.onResult(e.getMessage());
        }
      }
    });
    th.start();
  }

  /**
   * Used to create a callback that triggers when the server returns data
   */
  public interface Callback<V> {
    void onResult(V s);
  }
}