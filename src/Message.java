/*  Message.java
 *
 * 
 */

import java.nio.ByteBuffer;

public class Message
{
    private char msg_type;
    private String tx_node;
    private String[] rx_nodes;
    private String msg_content;

    public Message(ByteBuffer buf)
    {
        buf.limit(buf.position());
        buf.flip();

        byte[] b = new byte[buf.limit()];
        buf.get(b);

        String raw_msg = new String(b);

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