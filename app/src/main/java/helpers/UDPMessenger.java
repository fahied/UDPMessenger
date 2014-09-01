package helpers;

import android.os.StrictMode;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Spider on 02-Sep-14.
 */
public class UDPMessenger {
    private DatagramSocket datagramSocket;

    public void startUDPMessenger() throws SocketException {
        //Allow to run Network ops on main thread just for trail mode
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        datagramSocket = new DatagramSocket();
    }
    public void stopUDPMessenger() throws SocketException {
        //Close the port!
        datagramSocket.close();
    }

    public void sendUDPMessage(String messageStr, int destinationPort,String destinationIP) throws IOException {

        //datagramSocket.setBroadcast(false);
        InetAddress local = InetAddress.getByName(destinationIP);
        int msg_length=messageStr.length();
        byte[] message = messageStr.getBytes();
        DatagramPacket p = new DatagramPacket(message, msg_length,local,destinationPort);
        datagramSocket.send(p);
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
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, destinationPort);
                        datagramSocket.send(sendPacket);
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                    System.out.println(getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }

            //System.out.println(getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");

            //Wait for a response
            //byte[] recvBuf = new byte[15000];
            //DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            //c.receive(receivePacket);

            //We have a response
            //System.out.println(getClass().getName() + ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
