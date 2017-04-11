package rest.server;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.core.UriBuilder;

import javax.swing.Timer;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import rest.client.RemoveEndpoint;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class RendezVousServer {


    private static Map<String, Long> liveservers = new ConcurrentHashMap<>();
    private static String host = null;
    private static int port = -9999;
    private static List<String> ghosts = new LinkedList<>();

    public static void main(String[] args) throws Exception {
        port = 8080;
        if (args.length > 0)
            port = Integer.parseInt(args[0]);

        URI baseUri = UriBuilder.fromUri("http://0.0.0.0/").port(port).build();

        ResourceConfig config = new ResourceConfig();
        config.register(new RendezVousResources());

        JdkHttpServerFactory.createHttpServer(baseUri, config);

        System.err.println("REST RendezVous Server ready @ " + baseUri + " : local IP = "
                + InetAddress.getLocalHost().getHostAddress());

        host = InetAddress.getLocalHost().getHostAddress();


        // multicast
        String multAddress = "229.229.229.229";//igual no servidor de indexa��o
        int multPort = 9999;//igual no servidor de indexa��o

        final InetAddress address = InetAddress.getByName(multAddress);
        MulticastSocket socket = new MulticastSocket(multPort);
        socket.joinGroup(address);
        byte[] buffer = new byte[65536];
        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
        DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
        String serverUrl = null;


        long response_time = 10000; //miliseconds
        int delay = 8000;
        //runs checkup on servers
        int finalPort = port;
        Timer timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {

                try {

                    System.err.println("final port = " + finalPort +
                            " liveservers size = " + liveservers.size());
                    if (!(finalPort == -9999) && liveservers.size() > 0) {
                        System.err.println(" checking table");
                        table_cleanup(liveservers, response_time, host, finalPort);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
        timer.setRepeats(true);
        timer.start();


        while (true) {


            socket.receive(request);
            long t = NANOSECONDS.toSeconds(System.nanoTime());
            String msg = new String(buffer, 0, request.getLength());

            //checking liveservers
            //verify if it is really the indexer adress
            //host = request.getAddress().getHostAddress();

            String[] s = msg.split(" ");

            //avoid ghosts
            if (msg.contains("alive") && !ghosts.contains(s[1])) {


//                System.err.println("received heartbeat: " + t);


                long currenttime = t;
                check_servers(msg, liveservers, currenttime, response_time, host, port);

            } else if (msg.contains("rendezvous")) {
                serverUrl = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port+"/contacts/";
                reply.setSocketAddress(new InetSocketAddress(request.getAddress(), request.getPort()));
                reply.setData(serverUrl.getBytes());
                socket.send(reply);
            }

        }


    }


    //MULTICAST
    //check if the message was received on time
    private static void check_servers(String msg,
                                      Map liveservers,
                                      long currentTime,
                                      long mintime, String host, int port) throws IOException {


        String[] s = msg.split(" ");


        if (!liveservers.containsKey(s[1])) {
            liveservers.put(s[1], currentTime);
            // System.err.println("doesnt contain s1 =" + s[1] );

        } else {

            //  System.err.println("contains s1 =" + s[1] );
            long lastresponse = Long.parseLong(liveservers.get(s[1]).toString());


            //if time of response is greater then mintime it is removed and killed
            if (currentTime - lastresponse > mintime / 1000) {
                liveservers.remove(s[1]);
                new RemoveEndpoint(s[1], host, port);
                ghosts.add(s[1]);
            } else {
                //updates last response time

                liveservers.replace(s[1], lastresponse, currentTime);
//                System.err.println("updated time = " +  Long.parseLong(liveservers.get(s[1]).toString()));

            }

        }

    }

    //cleans the table after a designated time has passed
    private static void table_cleanup(Map liveservers,
                                      long elapsedTime,
                                      String host, int port) throws IOException {

        if (!liveservers.isEmpty()) {
            System.out.println("table cleanup");


            for (Object key : liveservers.keySet()) {

                long lst_msg = Long.parseLong(liveservers.get(key).toString());

                //if (lst_msg + 3 seconds) is not equal or bigger then current time
                //it was sent more than 3 secs ago
                //    int currenttime = currentTimeMillis(System.currentTimeMillis()) / 1000;

                long currenttime = NANOSECONDS.toSeconds(System.nanoTime());

                System.err.println("lst_msg = \n" + lst_msg);
                System.err.println("currentTime = \n" + (currenttime));
                if (currenttime - lst_msg > elapsedTime / 1000) {
                    new RemoveEndpoint(key.toString(), host, port);
                    liveservers.remove(key);
                    ghosts.add(key.toString());
                    System.err.println("removed = \n" + key.toString());
                }
            }
        }

    }

}