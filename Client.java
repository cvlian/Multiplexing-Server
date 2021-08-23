/*  Client.java
 *
 * 
 */

import java.io.*;
import java.util.*;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.channels.SocketChannel;

public class Client extends Thread
{
    private SocketChannel sock_chan;
    private ByteBuffer rx_buf, tx_buf;
    private Charset charset;
    private String cid;

    private static int DEF_PORT = 9876;
    private static int BUF_SIZE = 256;
    //private static Scanner sc = new Scanner(System.in);

    public Client(String host, int port, String id)
    {
        charset = Charset.forName("UTF-8");
        cid = id;
        rx_buf = ByteBuffer.allocate(BUF_SIZE);
    }

    @Override
    public void run()
    {
        try
        {
            init();

            while(true)
            {
                get_msg();
                
                if (0 > 0) break;
            }

            close();
        }
        catch (IOException e)
        {
            e.printStackTrace();;
        }
    }

    public static void main(String[] args)
    {
        if (args.length != 1)
        {
            System.out.println("Enter your ID!");
            System.exit(0);
        }

        Client clnt = new Client("localhost", DEF_PORT, args[0]);
        clnt.run();
    }

    public void init() throws IOException
    {
        // Open a socket channel, and then adjust its blocking mode
        sock_chan = SocketChannel.open();
        sock_chan.configureBlocking(true);

        // Connect the channel's socket
        System.out.println("Request for a connection");
        sock_chan.connect(new InetSocketAddress("localhost", DEF_PORT));
        System.out.println("Connection success");

        // Send your ID to the server
        tx_buf = charset.encode("[Init]:"+cid);
        sock_chan.write(tx_buf);
    }

    public void get_msg() throws IOException
    {
        int byte_cnt = sock_chan.read(rx_buf);

        if (byte_cnt <= 0)
        {
            return;
        }

        // Receive a msg from the server
        String rx_msg = new String(rx_buf.array());
        System.out.println("[Receive msg]: "+rx_msg);
    }

    private void close() throws IOException
    {
        if(sock_chan.isOpen())
        {
            sock_chan.close();
        }
    }
}