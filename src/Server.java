/*  Server.java
 *
 *  A real-time chatting application built on multiplexing client-server model
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
    private Charset charset;
    private HashMap<String, SelectionKey> clnt_list; // Hashmap that conatins all Client ID/channel key pairs

    private static int DEF_PORT = 9876; // Default port number to be connected on
    private static int BUF_SIZE = 256; // ByteBuffer's capacity, in bytes

    public Server()
    {
        charset = Charset.forName("UTF-8");
        clnt_list = new HashMap<String, SelectionKey>();
        rx_buf = ByteBuffer.allocate(BUF_SIZE);
        tx_buf = ByteBuffer.allocate(BUF_SIZE);
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
        Server svr = new Server();
        svr.run();
    }

    public void init() throws IOException
    {
        selector = Selector.open();

        // Open a server socket channel in non-blocking mode
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

                // New connection has been made
                if (key.isAcceptable()) 
                {
                    register_client();
                }

                // See if any message has been received
                if (key.isReadable()) 
                {
                    get_msg(key);
                }

                iter.remove();
            }
        }
    }

    // Add a socket client to the list of registered channels
    private void register_client() throws IOException
    {
        // Accept a connection made to this channel's socket
        SocketChannel clnt_sock_chan = svr_sock_chan.accept();
        clnt_sock_chan.configureBlocking(false);
        clnt_sock_chan.register(selector, SelectionKey.OP_READ);

        tx_buf = charset.encode("[I]/S/Unknown/Enter your ID: ");
        clnt_sock_chan.write(tx_buf);

        tx_buf.clear();
    }

    // Receive a message from a client
    private void get_msg(SelectionKey key) throws IOException
    {
        SocketChannel clnt_sock_chan = (SocketChannel)key.channel();
        int byte_cnt = clnt_sock_chan.read(rx_buf);

        // Nothing to read in...
        if (byte_cnt <= 0)
        {
            return;
        }

        // Read from receiving buffer and decode to a Message object (see Message.java)
        Message msg = new Message(rx_buf);
        rx_buf.flip();

        if (msg.type() == 'I') // connection establishment
        {
            // Prevent duplicated user ID
            if (clnt_list.containsKey(msg.TX()))
            {
                System.out.println(String.format("[Error] Duplicated ID: %s!", msg.TX()));
                forward_msg(key, String.format("[E]/S/%s/E001", msg.TX()));
                forward_msg(key, String.format("[I]/S/Unknown/Enter your ID: "));
            }
            else
            {
                clnt_list.put(msg.TX(), key);
                System.out.println(String.format("[Init] New ID: %s!", msg.TX()));
            }
        }
        else if (msg.type() == 'C') // chat
        {
            if(msg.RXs()[0].equals("all")) // forward message to all clients
            {
                Iterator<String> all_nodes = clnt_list.keySet().iterator();

                while (all_nodes.hasNext())
                {
                    String rx_node = all_nodes.next();

                    // Self-Talking?
                    if (msg.TX().equals(rx_node))
                    {
                        continue;
                    }

                    System.out.println(String.format("[Chat] User %s send message to %s", msg.TX(), rx_node));
                    forward_msg(clnt_list.get(rx_node), String.format("[C]/%s/%s/%s", msg.TX(), rx_node, msg.content()));
                }
            }
            else
            {
                for (String rx_node : msg.RXs())
                {
                    // Self-Talking?
                    if (msg.TX().equals(rx_node))
                    {
                        continue;
                    }
    
                    // Alert that the given receiver ID does not exist in client list
                    if (! clnt_list.containsKey(rx_node))
                    {
                        System.out.println(String.format("[Error] User name %s doesn't exist!", rx_node));
                        forward_msg(key, String.format("[E]/S/%s/E002/%s", msg.TX(), rx_node));
                        continue;
                    }
    
                    System.out.println(String.format("[Chat] User %s send message to %s", msg.TX(), rx_node));
                    forward_msg(clnt_list.get(rx_node), String.format("[C]/%s/%s/%s", msg.TX(), rx_node, msg.content()));
                }
            }
        }
        else // msg.type() = 'E'
        {
            // What if the server received an alert message from a client?
        }

        rx_buf.clear();
    }

    // Deliver a message to its recipients
    private void forward_msg(SelectionKey key, String msg) throws IOException
    {
        SocketChannel clnt_sock_chan = (SocketChannel)key.channel();

        tx_buf = charset.encode(msg);
        clnt_sock_chan.write(tx_buf);

        tx_buf.clear();
    }

    // Close the opened channel
    private void close() throws IOException
    {
        if(svr_sock_chan.isOpen())
        {
            svr_sock_chan.close();
        }
    }
}