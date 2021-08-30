/*  Message.java
 *
 *  A real-time chatting application built on multiplexing client-server model
 */

import java.nio.ByteBuffer;

public class Message
{
    private char msg_type;      // I : initialization, C: chat, E: error
    private String tx_node;     // Transmitting host
    private String[] rx_nodes;  // Receiving hosts
    private String msg_content; // raw message

    public Message(ByteBuffer buf)
    {
        // Flip the buffer to start reading
        buf.limit(buf.position());
        buf.flip();

        byte[] b = new byte[buf.limit()];
        buf.get(b);

        String raw_msg = new String(b);

        /* message format: [msg type]/[tx_node]/[rx_nodes]/msg_content
         *      ex. [C]/kim/lee,park/hello?
         * 
         */
        String[] msg_fields = (raw_msg).split("/", 4);

        if (msg_fields.length == 4)
        {
            msg_type = msg_fields[0].charAt(1);
            tx_node = msg_fields[1];
            rx_nodes = msg_fields[2].split(",");
            msg_content = msg_fields[3];
        }
        else
        {
            msg_content = msg_fields[0];
        }
    }

    public char type()
    {
        return msg_type;
    }

    public String TX()
    {
        return tx_node;
    }

    public String[] RXs()
    {
        return rx_nodes;
    }

    public String content()
    {
        return msg_content;
    }
}