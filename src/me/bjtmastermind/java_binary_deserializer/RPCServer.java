package me.bjtmastermind.java_binary_deserializer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;

import me.bjtmastermind.java_binary_deserializer.utils.BinaryUtils;
import me.bjtmastermind.java_binary_deserializer.utils.PrintUtils;
import me.bjtmastermind.jtuples.Tuple;

public class RPCServer implements Serializable {

    /**
     * Deserialize Java serialized
     *
     *  @param b64encoded_data Java serialized data, encoded in Base64
     *  @return Base64(PythonSerialized(   list of tuples (type, data)  ))
     *
     * In order to recover the list of tuples from the caller (on client-side):
     *  pickle.loads(base64.b64decode(result))
     */
    public void deserialize(String b64encoded_data) {
        byte[] input_data = Base64.getDecoder().decode(b64encoded_data);
        System.out.println();
        PrintUtils.printTitle("Receive RPC Call deserialize");
        System.out.println();
        PrintUtils.hexDump(input_data);
        System.out.println();

        Deserializer deserializer = new Deserializer(input_data);
        ArrayList<Tuple> output = deserializer.execute();

        return Base64.getDecoder().decode(BinaryUtils.dumps(output));
    }

    public void serialize(byte[] b64encoded_data) {
        Object input_data = BinaryUtils.loads(Base64.getDecoder().decode(b64encoded_data));
        System.out.println();
        PrintUtils.printTitle("Receive RPC Call serialize");
        System.out.println();
        System.out.println(input_data);
        System.out.println();

        Serializer serializer = new Serializer(input_data);
        output = serializer.execute();

        // Serializer ser = new Serializer(input_data);
        // output = ser.execute();

        return Base64.getEncoder().encode(output);
    }
}
