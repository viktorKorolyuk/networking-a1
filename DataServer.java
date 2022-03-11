import java.net.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

class DateServer {
    private static final String HEADER = "DSRV";

    @Retention(RetentionPolicy.RUNTIME)
    public @interface API {
        int flag();
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

    public static class DataServer {
        private final int SERVER_PORT = 5217;
        ServerSocket s;
        HashMap<Integer, Method> apiMethods;
        Thread serverThread;

        public DataServer(HashMap<Integer, Method> apiMethods) throws IOException {
            s = new ServerSocket(SERVER_PORT);
            this.apiMethods = apiMethods;
            serverThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        while (!Thread.interrupted())
                            loop();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        if (!Thread.interrupted())
                            e.printStackTrace();
                    }
                }
            });
        }

        public void loop()
                throws IOException,
                IllegalAccessException,
                IllegalArgumentException,
                InvocationTargetException {
            try {
                System.out.println("Waiting For Connection ...");
                Socket soc = s.accept();
                InputStream i = soc.getInputStream();
                if (i.available() == 0)
                    throw new EOFException("No data available.");
                byte[] arr = new byte[6];
                int read_bytes = i.read(arr, 0, 6);

                if (read_bytes == -1)
                    return;

                assert HEADER.equals(new String(arr)); // Read the HEADER
                i.read(arr, 0, 1); // Read the flag

                apiMethods.get((int) arr[0]).invoke(API.class);
                DataOutputStream out = new DataOutputStream(soc.getOutputStream());
                out.writeBytes(new Date().toString());
                out.close();
                soc.close();
            } catch (EOFException e) {
                e.printStackTrace();
                System.out.println("Attemping to recover.");
            }
        }

        public void start() {
            serverThread.start();
        }

        public void stop() throws InterruptedException {
            serverThread.interrupt();
            serverThread.join(12);
        }

        public void cleanup() {
            try {
                s.close();
                System.out.println("Cleanup finished.");
            } catch (IOException e) {
                System.err.println("Something went wrong when cleaning the server.");
                e.printStackTrace();
            }
        }
    }

    static class DateServerAPI {
        private static final int FLAG_DATE = 0b01;
        private static final int FLAG_TIME = 0b10;
        private static SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("MM/dd/yyyy");
        private static SimpleDateFormat FORMAT_TIME = new SimpleDateFormat("h:m:s");

        @API(flag = FLAG_DATE)
        public static String GetDate() {
            return FORMAT_DATE.format(Calendar.getInstance().getTime());
        }

        @API(flag = FLAG_TIME)
        public static String GetTime() {
            return FORMAT_DATE.format(Calendar.getInstance().getTime());
        }
    }
}