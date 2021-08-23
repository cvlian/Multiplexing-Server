/*  Client.java
 *
 * 
 */

import java.io.*;
import java.util.*;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Client
{

    private static int DEF_PORT = 9876;

    public static void main(String[] args)
    {
        SocketChannel sock_chan = null;

        try
        {
            sock_chan = SocketChannel.open();
            sock_chan.configureBlocking(true);
            System.out.println("Request for a connection");
            sock_chan.connect(new InetSocketAddress("localhost", DEF_PORT));
            System.out.println("Connection success");
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if(sock_chan.isOpen())
        {
            try
            {
                sock_chan.close();
            }
            catch (IOException e)
            {
                // ?? 여긴 왜 비어있지?
            }
        }
    }
}