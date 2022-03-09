import java.awt.*;
import java.awt.event.*;
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
        printDate();
      }
    });

    time.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        printTime();
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

  public void printDate() {

    Calendar date = Calendar.getInstance();
    l.setText("Date: " + (date.get(Calendar.MONTH + 1) + "/" + date.get(Calendar.DAY_OF_MONTH)) + "/"
        + date.get(Calendar.YEAR));

  }

  public void printTime() {

    Calendar time = Calendar.getInstance();
    l.setText("Time: " + time.get(Calendar.HOUR) + ":" + time.get(Calendar.MINUTE) + ":" + time.get(Calendar.SECOND));

  }

  // all programs must have a main
  // this main simply creates a new UIExample object
  public static void main(String[] args) {
    new UIExample();
  }

}