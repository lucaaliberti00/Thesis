package commons.mining.model;

import commons.idea.Idea;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;



/**
 * Item in Sequential Rules terminology
 */
public class Item {

    private String nodeName;
    private String category;
    private Integer port;

    public Item(String nodeName, String category, Integer port) {
        //String[] splitted = nodeName.split("\\.");
        //nodeName = splitted[0] + "." + splitted[1];

        this.nodeName = nodeName;
        this.category = category;

        if (port == null)
            this.port = null;
        /*
        else if (port > 49151)
            this.port = 2;
        else if(port > 1023)
            this.port = 1;
        else
            this.port = 0;*/
        this.port = port;
    }

    public Item(Idea idea) {
        try {
            nodeName = idea.getNode().get(0).getName();
            //String[] splitted = nodeName.split("\\.");
            //nodeName = splitted[0] + "." + splitted[1];
        } catch (Exception ex){
            nodeName = null;
        }

        category = idea.getCategory().get(0);
        try {
            port = idea.getTarget().get(0).getPort().get(0);
            /*if (port > 49151)
                port = 2;
            else if(port > 1023)
                port = 1;
            else
                port = 0;*/
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            port = null;
        }
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getCategory() {
        return category;
    }

    public Integer getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Item item = (Item) o;
        return Objects.equals(nodeName, item.nodeName) &&
                Objects.equals(category, item.category) &&
                Objects.equals(port, item.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeName, category, port);
    }

    /**
     * Represent {@link Item} as String with underscore separated attributes.
     * {@link #fromString(String)} is reverse operation. See it for more information.
     *
     * @return string
     */
    @Override
    public String toString() {
        String port = "None";
        if (this.port != null) {
            port = this.port.toString();
        }

        return String.join("_", nodeName, category, port);
    }

    /**
     * Parse {@link Item} in the form of "{category}_{node}_{port}".
     * Port set as "None" will results with {@code null} being set to {@code port} attribute.
     *
     * {@link #toString()} is reverse operation.
     *
     * Example of item:
     *      cz.cesnet.nemea.hoststats_Recon.Scanning_None
     *
     * @param item string representation of an item
     * @return Item
     */
    public static Item fromString(String item) {
        List<String> parts = splitFromEnd(item, "_", 3);

        Integer port = null;
        if (!"None".equals(parts.get(2))) {
            port = Integer.valueOf(parts.get(2));
        }

        return new Item(parts.get(0), parts.get(1), port);
    }

    private static List<String> splitFromEnd(String input, String delimiter, int limit) {
        List<String> parts = new LinkedList<>();
        for (int i = 0; i < limit-1; i++) {
            int position = input.lastIndexOf(delimiter);
            if (position == -1) {
                break;
            }

            String item = input.substring(position + delimiter.length());
            parts.add(0, item);
            input = input.substring(0, position);
        }
        parts.add(0, input);
        return parts;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
