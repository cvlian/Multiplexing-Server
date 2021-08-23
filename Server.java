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
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

public class Server extends Thread
{
    private Selector selector;
    private ByteBuffer rx_buf, tx_buf;
    private ServerSocketChannel svr_sock_chan;
    //private InetSocketAddress sock_addr;
    private Charset charset;
    private HashMap<String, SelectionKey> clnt_list;

    private static int DEF_PORT = 9876;
    private static int BUF_SIZE = 256;

    public Server(String host, int port)
    {
        //sock_addr = new InetSocketAddress(host, port);
        charset = Charset.forName("UTF-8");
        clnt_list = new HashMap<String, SelectionKey>();
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
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        Server svr = new Server("localhost", DEF_PORT);
        svr.run();
    }

    public void init() throws IOException
    {
        selector = Selector.open();

        // Open a server socket channel, and then adjust its blocking mode
        svr_sock_chan = ServerSocketChannel.open();
        svr_sock_chan.configureBlocking(false);

        // Bind the channel's socket to a local address (to be revised...)
        // Configure the socket to listen for connections via 'DEF_PORT (9876)'
        svr_sock_chan.bind(new InetSocketAddress(DEF_PORT));
        svr_sock_chan.register(selector, SelectionKey.OP_ACCEPT);

        while (true)
        {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();

            while (iter.hasNext())
            {
                SelectionKey key = iter.next();

                if (key.isAcceptable()) 
                {
                    register_client();
                }

                if (key.isReadable()) 
                {
                    get_msg(key);
                    //send_msg(key);
                }

                iter.remove();
            }
        }
    }

    private void register_client() throws IOException
    {
        // Accept a connection made to this channel's socket
        SocketChannel clnt_sock_chan = svr_sock_chan.accept();
        clnt_sock_chan.configureBlocking(false);
        clnt_sock_chan.register(selector, SelectionKey.OP_READ);
        System.out.println("new client connected");
    }

    private void get_msg(SelectionKey key) throws IOException
    {
        SocketChannel clnt_sock_chan = (SocketChannel)key.channel();
        int byte_cnt = clnt_sock_chan.read(rx_buf);

        if (byte_cnt <= 0)
        {
            // clnt_sock_chan.close();
            // key.cancel();
            return;
        }

        String rx_msg = new String(rx_buf.array());
        String[] msg_fields = rx_msg.split(":", 2);

        if (msg_fields[0].equals("[Init]"))
        {
            if (clnt_list.containsKey(msg_fields[1]))
            {
                System.out.println("Duplicated ID!");
            }
            else
            {
                clnt_list.put(msg_fields[1], key);
                System.out.println("New ID!");
                forward_msg(key, "Hello "+msg_fields[1]+"!");
            }
        }
        else
        {
            System.out.println(String.format("normal msg: %d", byte_cnt));
        }
    }

    private void forward_msg(SelectionKey key, String msg) throws IOException
    {
        SocketChannel clnt_sock_chan = (SocketChannel)key.channel();

        // Reply to the client's msg
        tx_buf = charset.encode(msg);
        clnt_sock_chan.write(tx_buf);
        System.out.println("[Send msg]");
    }

    private void close() throws IOException
    {
        if(svr_sock_chan.isOpen())
        {
            svr_sock_chan.close();
        }
    }
}