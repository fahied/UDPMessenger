package helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import com.spider.udpmessenger.MessengerActivity;
import com.spider.udpmessenger.R;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Created by Spider on 02-Sep-14.
 */
public class UDPMessenger implements Runnable
{
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;

    private SharedPreferences sharedPreferences;
    private Context context;

    private String messageSequence;
    private boolean shouldBroadcast;
    private int destinationPort;
    private String destinationIP;

    public void stopUDPMessenger() throws SocketException {
        //Close the port!
        datagramSocket.close();
    }


    public UDPMessenger(Context context, String messageSequence, boolean shouldBroadcast) throws InterruptedException, IOException
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        datagramSocket = new DatagramSocket();

        this.context = context;
        this.messageSequence = messageSequence;
        this.shouldBroadcast = shouldBroadcast;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        loadPref();
    }


    private void loadPref() {
        // broad to all preference
        boolean checkbox_preference = sharedPreferences.getBoolean("checkbox_preference", false);
        shouldBroadcast = checkbox_preference;

        //Destination IP preference
        destinationIP   = sharedPreferences.getString(context.getString(R.string.key_destination_ip),"127.0.0.1");
        String port = sharedPreferences.getString(context.getString(R.string.key_port_number), "20002");
        destinationPort = Integer.parseInt(port);

    }


    public void sendUDPMessage(String messageStr, int destinationPort,String destinationIP) {
        InetAddress local = null;
        try {
            local = InetAddress.getByName(destinationIP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int msg_length=messageStr.length();
        byte[] message = messageStr.getBytes();
        datagramPacket = new DatagramPacket(message, msg_length,local,destinationPort);
        try {
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(getClass().getName() + ">>> Request packet sent to: "+destinationIP +":"+destinationPort);
    }


    public void broadcastUDPMessage(String message, int destinationPort)
    {
        // Find the server using UDP broadcast
        try {
            //Open a random port to send the package
            datagramSocket.setBroadcast(true);

            byte[] sendData =message.getBytes();

            // Broadcast the message over all the network interfaces
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't want to broadcast to the loopback interface
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }

                    // Send the broadcast package!
                    try {
                        datagramPacket = new DatagramPacket(sendData, sendData.length, broadcast, destinationPort);
                        datagramSocket.send(datagramPacket);
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                    System.out.println(getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void run() {
        MessengerActivity messengerActivity = (MessengerActivity)context;

        if (this.messageSequence.equalsIgnoreCase(""))
        {
            messengerActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog("Empty Sequence", "Go to settings and define message sequence");
                }
            });
            return;
        }
        String[]parts = this.messageSequence.split("\n");
        for (int i = 0; i < parts.length;i++)
        {
            String part = parts[i];
            //if the message part is numeric then probably it is delay otherwise a message to be sent
            if (isNumeric(part))
            {
                int delay  = Integer.parseInt(parts[i]);
                //the time can only be positive int
                delay = Math.abs(delay);
                try {
                    Thread.currentThread().sleep(delay);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else if (!part.equalsIgnoreCase(""))
            {
                System.out.println(part);
                if (shouldBroadcast)
                    broadcastUDPMessage(part,destinationPort);
                else sendUDPMessage(part, destinationPort, destinationIP);
            }
            // if all messages has been sent
            if (i == parts.length-1)
            {
                messengerActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDialog("Messages sent successfully", messageSequence);
                    }
                });
            }
        }
    }

    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }


    private void showDialog(String dialogTitle, String errorString){
        String okButtonString = "OK";
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle(dialogTitle);
        ad.setMessage(errorString);
        ad.show();
        return;
    }
}
