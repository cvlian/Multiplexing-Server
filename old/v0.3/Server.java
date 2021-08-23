/*  Server.java
 *
 * 
 */

import java.io.IOException;
import java.util.*;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;

public class Server
{

    private static int DEF_PORT = 9876;
    private static int BUF_SIZE = 1024;

    public static void main(String[] args)
    {
        ServerSocketChannel svr_sock_chan = null;

        try
        {   
            // Open a server socket channel, and then adjust its blocking mode
            svr_sock_chan = ServerSocketChannel.open();
            svr_sock_chan.configureBlocking(false);

            // Bind the channel's socket to a local address (to be revised...)
            // Configure the socket to listen for connections via 'DEF_PORT (9876)'
            svr_sock_chan.bind(new InetSocketAddress(DEF_PORT));

            while(true)
            {
                System.out.println("[Waiting for a msg...]");

                // Accept a connection made to this channel's socket
                SocketChannel sock_chan = svr_sock_chan.accept();

                if (sock_chan != null)
                {
                    // Returns the remote address (localhost, in our case) to which this channel's socket is connected
                    InetSocketAddress isa = (InetSocketAddress)sock_chan.getRemoteAddress();
                    System.out.println("[Connection success]" + isa.getHostName());

                    // Allocate a new byte buffer
                    ByteBuffer buf = null;
                    Charset charset = Charset.forName("UTF-8");
                    buf = ByteBuffer.allocate(BUF_SIZE);

                    int byte_cnt = sock_chan.read(buf);
                    buf.flip(); // Flip?? (See https://jamssoft.tistory.com/221)

                    // Receive a msg from the client
                    String msg = charset.decode(buf).toString();
                    System.out.println("[Receive msg]" + msg);

                    // Reply to the client's msg
                    buf = charset.encode("I got msg!!");
                    sock_chan.write(buf);
                    System.out.println("[Send msg]");
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if(svr_sock_chan.isOpen())
        {
            try
            {
                svr_sock_chan.close();
            }
            catch (IOException e)
            {
                // ?? Blank line?
            }
        }
    }
}