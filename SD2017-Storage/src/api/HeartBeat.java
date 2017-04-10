package api;
import java.net.*;
import java.io.*;

public class HeartBeat extends Thread
{// sends a heartbeat message to the multicast group every 60 seconds
    public HeartBeat (MulticastSocket sock, InetAddress addr, int port, String indexerID)
    {

        this.sock = sock;
        this.addr= addr;
        this.port = port;
        this.indexerID = indexerID;
    }


    public static MulticastSocket sock;
    public static InetAddress addr;
    public static int port;
    public static String indexerID;
    private DatagramPacket hbMsg ;
    static private long TmHB = 5000;  //heartbeat frequency in milliseconds

    public void run(){
        // setup the hb datagram packet then run forever
        // setup the line to ignore the loopback we want to get it too
        String line = "alive: " +  indexerID ;

        hbMsg = new DatagramPacket(line.getBytes(),
                line.length(),
                addr,
                port);

        hbMsg.setAddress(addr);
        hbMsg.setPort(port);
        // continually loop and send this packet every TmHB seconds
        while (true){
            try{
                sock.send(hbMsg);
                // System.err.println("beating \n");
                sleep(TmHB);
            }
            catch (IOException e){System.err.println("Server can't send heartbeat");
                System.exit(-1);
            }
            catch (InterruptedException e){}
        }
    }// end run

}// end class