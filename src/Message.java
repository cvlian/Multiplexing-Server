/*  Message.java
 *
 * 
 */

public class Message
{
    private char msg_type;
    private String tx_node;
    private String[] rx_nodes;
    private String msg_content;

    public Message(String raw_msg)
    {
        String[] msg_fields = (raw_msg).split(":", 4);

        msg_type = msg_fields[0].charAt(1);
        tx_node = msg_fields[1];
        rx_nodes = msg_fields[2].split(",");
        msg_content = msg_fields[3];
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