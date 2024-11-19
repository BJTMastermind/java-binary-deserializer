package me.bjtmastermind.java_binary_deserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.thoughtworks.xstream.XStream;

import me.bjtmastermind.java_binary_deserializer.utils.BinaryUtils;
import me.bjtmastermind.java_binary_deserializer.utils.PrintUtils;
import me.bjtmastermind.jtuples.Tuple;

public class Deserializer {
    ByteBuffer input;
    ArrayList<Tuple> output;

    /**
     * @param input Binary data that must be deserialized
     */
    public Deserializer(byte[] input) {
        this.input = ByteBuffer.wrap(input);
        this.output = new ArrayList<Tuple>();
    }

    /**
     * Scan input (file) for Java Serialized data to deserialize
     *
     * @return List of tuples (type, data) where:
     *     * type can be:
     *         * object: for deserialized Java object
     *         * block: raw data corresponding to primitive types (boolean, byte, char...)
     *     * data is the deserialized data (in XML form) when type = object, otherwise the raw data
     */
    public ArrayList<Tuple> execute() {
        // Java Serialized stream begins with:
        // AC ED: STREAM_MAGIC
        // 00 05: STREAM_VERSION
        try {
            int header = BinaryUtils.find(this.input, new byte[] {(byte) 0xAC, (byte) 0xED, (byte) 0x00, (byte) 0x05});
            if (header >= 0) {
                PrintUtils.printSuccess(String.format("Java Serialized data header found at offset 0x%x\n", header));
                this.deserializeStream();
                this.printDeserializedData();
            } else {
                PrintUtils.printError("Unable to find Java Serialized data into input, assuming it is only raw data");
                this.output.add(new Tuple("block", this.input.array()));
            }
        } catch (IOException e) {}
        return this.output;
    }

    /**
     * Deserialize stream, put the result inside this.output
     *
     * @throws IOException
     */
    private void deserializeStream() throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(this.input.array());
        ObjectInputStream ois = new ObjectInputStream(bis);
        int i = 0;

        while (true) {
            // Block raw data ?
            ArrayList<String> blockData = new ArrayList<>();
            try {
                while (true) {
                    String abyte = BinaryUtils.toHex(ois.readByte());
                    // String raw_byte = Character.toString(Integer.parseInt(abyte, 16));
                    // block_data.add(raw_byte);
                    blockData.add(abyte);
                }
            } catch (Exception e) {
                if (blockData.size() > 0) {
                    // System.out.println(String.format("Block data detected - length = %d:", block_data.size()));
                    this.output.add(new Tuple("block", blockData));
                    i += 1;
                    System.out.println();
                }
            }

            // Object ?
            try {
                Object obj = ois.readObject();

                // converting Java object to XML structure
                XStream xs = new XStream();
                String xml = xs.toXML(obj);
                //this.output += xml + "\n"
                this.output.add(new Tuple("object", xml));
                i += 1;
                continue;
            } catch (Exception e) {}

            // if we arrive here, it means there is nothing more
            break;
        }
    }

    /**
     * Print deserialized data nicely in the terminal
     */
    private void printDeserializedData() {
        if (this.output.size() == 0) {
            PrintUtils.printError("No deserialized data to print");
            return;
        }

        PrintUtils.printDelimiter();
        int i = 0;
        for (Tuple tuple : this.output) {
            String type_ = (String) tuple.get(0);
            String data = (String) tuple.get(1);

            if (type_.equals("block")) {
                PrintUtils.printInfo(String.format("[0x%02x] Block raw data - length = %d (0x%02x) bytes:", i, data.length(), data.length()));
                // PrintUtils.hexDump("".join(list(map(lambda x: chr(int(x, 16)), data))));
                PrintUtils.hexDump(IntStream.range(0, data.length() / 2)
                    .mapToObj(x -> data.substring(x * 2, x * 2 + 2))
                    .map(hex -> (char) Integer.parseInt(data, 16))
                    .map(String::valueOf)
                    .collect(Collectors.joining())
                );
                // System.out.println(this.output.get(i).get(1));
            } else if (type_.equals("object")) {
                PrintUtils.printInfo(String.format("[0x%02x] Java Object (converted into XML):", i));
                PrintUtils.printXMLHighlighted(data);
            }
            i += 1;
            PrintUtils.printDelimiter();
        }
        return;
    }
}
