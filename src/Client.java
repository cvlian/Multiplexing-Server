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
    private boolean is_connected;

    private static int DEF_PORT = 9876;
    private static int BUF_SIZE = 256;
    private static Scanner sc = new Scanner(System.in);

    public Client()
    {
        is_connected = false;
        charset = Charset.forName("UTF-8");
        rx_buf = ByteBuffer.allocate(BUF_SIZE);
    }

    @Override
    public void run()
    {
        try
        {
            init();
            close();
        }
        catch (IOException e)
        {
            e.printStackTrace();;
        }
    }

    public static void main(String[] args)
    {
        Client clnt = new Client();
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

        while (true)
        {
            get_msg();

            if (is_connected)
            {
                send_msg();
            }
        }
    }

    public void register_ID() throws IOException
    {
        cid = sc.nextLine();
        tx_buf = charset.encode(String.format("[I]:%s:S:my ID", cid));
        sock_chan.write(tx_buf);

        tx_buf.clear();

        is_connected = true;
    }  

    public void get_msg() throws IOException
    {
        int byte_cnt = sock_chan.read(rx_buf);

        if (byte_cnt <= 0)
        {
            return;
        }

        // Receive a msg from the server
        Message msg = new Message(new String(rx_buf.array()));

        if (msg.type() == 'I')
        {
            System.out.print(msg.content());
            register_ID();
        }
        else if (msg.type() == 'C')
        {
            System.out.print(msg.TX() + ":" + msg.content());
        }
        else
        {

        }

        rx_buf.clear();
    }

    public void send_msg() throws IOException
    {
        String[] msgs = sc.nextLine().split("-");

        tx_buf = charset.encode(String.format("[C]:%s:%s:%s", cid, msgs[0], msgs[1]));
        sock_chan.write(tx_buf);

        tx_buf.clear();
    }

    private void close() throws IOException
    {
        if(sock_chan.isOpen())
        {
            sock_chan.close();
        }
    }
}