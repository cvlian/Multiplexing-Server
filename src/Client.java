/*  Client.java
 *
 * 
 */

import java.io.*;
import java.util.regex.Pattern;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.channels.SocketChannel;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class Client
{
    private SocketChannel sock_chan;
    private static WritableByteChannel wb_chan;
    private static ByteBuffer rx_buf, tx_buf;
    private static boolean is_connected;
    private static Charset charset;
    private static String cid;

    private static int DEF_PORT = 9876;
    private static int BUF_SIZE = 256;

    public Client()
    {
        charset = Charset.forName("UTF-8");
        rx_buf = ByteBuffer.allocate(BUF_SIZE);
    }

    public static void main(String[] args) throws IOException
    {
        Client clnt = new Client();
        clnt.init();
    }

    public void init() throws IOException
    {
        // Open a socket channel, and then adjust its blocking mode
        sock_chan = SocketChannel.open();
        sock_chan.configureBlocking(true);

        wb_chan = Channels.newChannel(System.out);

        // Connect the channel's socket
        print_msg("Request for a connection\n");
        sock_chan.connect(new InetSocketAddress("localhost", DEF_PORT));
        print_msg("Connection success\n");

        // Create a thread responsible for handling both standatrd input & message transmission to the server
        Thread systemIn = new Thread(new SystemIn(sock_chan));
        systemIn.start();

        while (true)
        {
            get_msg();
        }
    }

    // Write chat logs/server messages standard out
    public static void print_msg(String s) throws IOException
    {
        tx_buf = charset.encode(s);
        wb_chan.write(tx_buf);
        tx_buf.clear();
    }

    // Receive a message from the server
    public void get_msg() throws IOException
    {
        int byte_cnt = sock_chan.read(rx_buf);

        // Nothing to print out...
        if (byte_cnt <= 0)
        {
            return;
        }

        Message msg = new Message(rx_buf);

        if (msg.type() == 'I') // connection establishment
        {
            print_msg(msg.content());
        }
        else if (msg.type() == 'C') // chat
        {
            print_msg("from "+msg.TX() + ":" + msg.content());
        }
        else // error
        {
            /* Error code list
             *
             * E001 : Duplicated ID
             * E002 [user name] : [user name] doesn't exist
             */

            String[] err_msgs = msg.content().split("/");

            if(err_msgs[0].equals("E001"))
            {
                print_msg("[Error]: Duplicated ID, try another ID\n");
                is_connected = false;
            }
            else if(err_msgs[0].equals("E002"))
            {
                print_msg(String.format("[Error]: User \"%s\" doesn't exist\n", err_msgs[1]));
            }
        }

        rx_buf.clear();
    }

    // Close the activated channel
    private void close() throws IOException
    {
        if(sock_chan.isOpen())
        {
            sock_chan.close();
        }
    }

    private static class SystemIn implements Runnable
    {
        SocketChannel sock_chan;
        ByteBuffer rx_buf, tx_buf;
    
        SystemIn(SocketChannel chan)
        {
            is_connected = false;
            sock_chan = chan;
        }
    
        @Override
        public void run()
        {
            ReadableByteChannel rb_chan = Channels.newChannel(System.in);
            rx_buf = ByteBuffer.allocate(BUF_SIZE);
    
            try
            {
                while(true)
                {
                    int byte_cnt = rb_chan.read(rx_buf);

                    if (byte_cnt <= 0)
                    {
                        continue;
                    }

                    Message msg = new Message(rx_buf);

                    rx_buf.clear();

                    if(! is_connected)
                    {
                        cid = new String(msg.content().replace("\n", ""));

                        if (! Pattern.matches("^[a-zA-Z0-9]*$", cid))
                        {
                            print_msg("[Error]: You can use only alphabetic or numeric characters\n");
                            print_msg("Enter your ID: ");
                        }
                        else if(cid.equals("all"))
                        {
                            print_msg("[Error]: ID cannot be \"all\"\n");
                            print_msg("Enter your ID: ");
                        }
                        else
                        {
                            is_connected = true;
                            send_msg(String.format("[I]/%s/S/my ID", cid));
                        }
                    }
                    else
                    {
                        if (! Pattern.matches("^to [a-zA-Z0-9,]+:.+\n$", msg.content()))
                        {
                            print_msg("[Error]: Wrong message format, follow \"to [user name1, user name2, .../all]: [message]\"\n");
                            continue;
                        }

                        String[] msgs = msg.content().substring(3).split(":");
                        send_msg(String.format("[C]/%s/%s/%s", cid, msgs[0], msgs[1]));
                    }
                }
            }
            catch (IOException e)
            {
    
            }
        }

        void send_msg(String s) throws IOException
        {
            tx_buf = charset.encode(s);
            sock_chan.write(tx_buf);
            tx_buf.clear();
        }
    }
}