/*  Server.java
 *
 * 
 */

import java.io.IOException;
import java.util.*;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Server
{

    private static int DEF_PORT = 9876;
    private static int BUF_SIZE = 1024;

    public static void main(String[] args)
    {
        ServerSocketChannel svr_sock_chan = null;

        try
        {    
            svr_sock_chan = ServerSocketChannel.open();
            svr_sock_chan.configureBlocking(true);
            svr_sock_chan.bind(new InetSocketAddress(DEF_PORT));

            while(true){

                System.out.println("[Waiting for a msg...]");
                SocketChannel sock_chan = svr_sock_chan.accept();
                InetSocketAddress isa = (InetSocketAddress)sock_chan.getRemoteAddress();
                System.out.println("[Connection success]" + isa.getHostName());
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
                // ?? 여긴 왜 비어있지?
            }
        }
    }
}
