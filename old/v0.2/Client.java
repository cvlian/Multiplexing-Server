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

public class Client
{

    private static int DEF_PORT = 9876;
    private static int BUF_SIZE = 1024;

    public static void main(String[] args)
    {

        SocketChannel sock_chan = null;

        try
        {
            // Open a socket channel, and then adjust its blocking mode
            sock_chan = SocketChannel.open();
            sock_chan.configureBlocking(true);

            // Connect the channel's socket
            System.out.println("Request for a connection");
            sock_chan.connect(new InetSocketAddress("localhost", DEF_PORT));
            System.out.println("Connection success");

            ByteBuffer buf = null;
            Scanner sc = new Scanner(System.in);

            // Read a single input line from your console
            Charset charset = Charset.forName("UTF-8");
            buf = charset.encode(sc.nextLine());
            sock_chan.write(buf);
            System.out.println("[Send msg]");

            buf = ByteBuffer.allocate(BUF_SIZE);

            int byte_cnt = sock_chan.read(buf);
            buf.flip();

            // Receive a msg from the server
            String msg = charset.decode(buf).toString();
            System.out.println("[Receive msg]"+msg);
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if(sock_chan.isOpen())
        {
            try
            {
                // Close the channel
                sock_chan.close();
            }
            catch (IOException e)
            {
                // ?? Blank line?
            }
        }
    }

}