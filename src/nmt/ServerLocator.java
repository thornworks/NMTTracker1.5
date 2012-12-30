package nmt;

import java.net.*;
import java.io.*;
import javax.swing.JOptionPane;

/*
 * Listens for packets send to port 3141.
 * Responds to all requests for server addresses sent to this port.
 * This allows the user of the Buzzer to not need to know
 * the address of the BuzzerServer, provided that they are in the same
 * local broadcast domain.
 *
 */

public class ServerLocator extends Thread {
    DatagramPacket packet;
    DatagramSocket socket;
    byte[] data;

    String locateMessage = "Here, server, server, server, server!?";

    public ServerLocator() {
        data = new byte[38];
        try {
            socket = new DatagramSocket(3141);//UDP is datagram
            packet = new DatagramPacket(data, data.length);
        } catch (SocketException se) {
            System.out.println("Unable to create ServerLocator:" + se);
        }
    }

    public ServerLocator(int port, String message) {
        locateMessage = message;
        data = new byte[38];
        try {
            socket = new DatagramSocket(port);
            packet = new DatagramPacket(data, data.length);
        } catch (SocketException se) {
            System.out.println("Unable to create ServerLocator:" + se);
        }
    }

    public ServerLocator(int port) {
        data = new byte[38];
        try {
            socket = new DatagramSocket(port);
            packet = new DatagramPacket(data, data.length);
        } catch (SocketException se) {
            System.out.println("Unable to create ServerLocator:" + se);
        }
    }

    public void run() {
        while (true) {
            try {
                for (int i = 0; i < data.length; i++) {
                    data[i] = 0;
                }
                socket.setSoTimeout(200); //waits for 1/5 seconds for a response.
                socket.receive(packet); //waits until data arrives
                String received = new String(packet.getData());
                received = received.trim();
                if (received.equals(locateMessage)) {
                    sendReply(packet.getAddress());
                }
            } catch (SocketException se) {
                System.out.println("Socket problem with Datagram Thread.");
            } catch (IOException ioe) {
                //System.out.println("Problem with Datagram Thread:" + ioe);
            }
        }
    } //end run

    private void sendReply(InetAddress aClient) throws IOException {
        //System.out.println("Server Locator sending my address to: " +
        //      aClient.toString());
        socket.send(packet);
    }


    /*
     *  Returns the IP address of the Server (machine running ServerLocator on the
     *  specified port).  The default locateMessage is used:
     *              "Here, server, server, server, server!?"
     *
     */
    public static InetAddress clientFindServer(int port, int numAttempts) {
        return clientFindServer(port, numAttempts,
                                "Here, server, server, server, server!?");
    }

    /*
     * Once a serverlocator has been launched, this method can be used to find it.
     * It searches for a ServerLocator on the given port and returns the
     * address if it is found.  If no server can be found, null is returned.
     * @param numAttempts = the number of attempts at getting a response from
     *  server before returning null.  The serverCall must match the String
     *  configured on the ServerLocator (locateMessage).
     *
     *
     */
    public static InetAddress clientFindServer(int port,
                                               int numAttempts,
                                               String serverCall) {
        byte[] data = serverCall.getBytes();
        DatagramPacket packet = null;
        DatagramSocket socket = null;

        try {
            InetAddress broadcast = InetAddress.getByName("255.255.255.255");
            socket = new DatagramSocket();
            packet = new DatagramPacket(data, data.length, broadcast, port);
            socket.send(packet);
            System.out.println("Broadcast sent: " + data.length + " bytes.");
            socket.setSoTimeout(1000); //waits one second for a response.

        } catch (UnknownHostException utoh) {
            JOptionPane.showMessageDialog(null, "Invalid IP address", "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (SocketException utoh) {
            JOptionPane.showMessageDialog(null, "Socket Problem", "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (IOException utoh) {
            JOptionPane.showMessageDialog(null, "IO Problem", "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return null;
        }

        //wait for response.
        try {
            socket.receive(packet);
        } catch (SocketTimeoutException timed) {
            //check to see if this is the last attempt and return null
            //otherwise, try again to locateServer (recursively)
            if (numAttempts == 1) {
                JOptionPane.showMessageDialog(null,
                                              "Request for server timed out.",
                                              "Server Not Found Error!",
                                              JOptionPane.ERROR_MESSAGE);
                return null;
            } else {
                return clientFindServer(port, numAttempts - 1, serverCall);
            }
        } catch (SocketException utoh) {
            JOptionPane.showMessageDialog(null, "Socket Problem", "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (IOException utoh) {
            JOptionPane.showMessageDialog(null, "IO Problem", "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return null;
        }
        System.out.println("Found Server at: " + packet.getAddress());
        return packet.getAddress();
    } //end locatServer method

    public static void main(String[] args) {
        ServerLocator imaServer = new ServerLocator();
        imaServer.start();
        //Demonstrate the client side:
        InetAddress ip = clientFindServer(3141, 5);
        System.out.println(ip.toString());
    }
} // end ServerLocator class
