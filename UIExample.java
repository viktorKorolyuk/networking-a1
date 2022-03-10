import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class UIExample {

  Frame f; // a frame to hold our components

  Button date, time;

  JLabel l;

  public UIExample() {
    f = new Frame(); // create a new frame
    f.setLayout(new FlowLayout()); // make the layout of the frame a flowlayout

    date = new Button("Get Date");
    time = new Button("Get Time");
    // add an actionlistener that will take action when the button is pushed
    // pass it the text field so that the listener can get the text out of the
    // textfield
    // and take appropriate action

    time.addActionListener(null);

    f.add(date);
    f.add(time);

    l = new JLabel("");
    f.add(l);

    date.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        try {
          printDate();
        } catch (UnknownHostException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    });

    time.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        try {
          printTime();
        } catch (UnknownHostException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    });

    f.setSize(450, 300); // set the size of the frame

    // add the windowlistener that will exit the program when the user closes the
    // window
    f.addWindowListener(new WindowAdapter() {
      // This method is called after a window is closed
      public void windowClosing(WindowEvent evt) {
        System.exit(0);
      }
    });

    f.show(); // show the frame with the components
  }

  public void printDate() throws UnknownHostException, IOException {

    Socket soc = new Socket(InetAddress.getLocalHost(), 5217);
    BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
    String date = in.readLine();
    l.setText("Server Date: " + date.substring(0, 10) + date.substring(23, 28) + "\n");
    soc.close();
  }

  public void printTime() throws UnknownHostException, IOException {

    Socket soc = new Socket(InetAddress.getLocalHost(), 5217);
    BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
    String time = in.readLine();
    l.setText("Server Time: " + time.substring(11, 23) + "\n");
    soc.close();
  }

  // all programs must have a main
  // this main simply creates a new UIExample object
  public static void main(String[] args) {
    new UIExample();
  }

}