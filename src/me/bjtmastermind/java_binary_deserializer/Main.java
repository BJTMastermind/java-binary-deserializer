package me.bjtmastermind.java_binary_deserializer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import me.bjtmastermind.java_binary_deserializer.utils.FileUtils;
import me.bjtmastermind.java_binary_deserializer.utils.PrintUtils;
import me.bjtmastermind.java_binary_deserializer.utils.terminal.Style;
import me.bjtmastermind.jtuples.Tuple;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.action.StoreArgumentAction;
import net.sourceforge.argparse4j.impl.action.StoreTrueArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class Main {
    private static final String BANNER = """
===============================================================================
                -- Java Serializer/Deserializer --
===============================================================================
""";

    public static void main(String[] args) throws ArgumentParserException {
        // -----------------------------------------------------------------------------
        // --- Command parsing ---------------------------------------------------------
        // -----------------------------------------------------------------------------
        System.out.println(Style.BRIGHT + BANNER + Style.RESET_ALL);

        ArgumentParser parser = ArgumentParsers.newFor("javaDeserializer").build();

        ArgumentGroup io = parser.addArgumentGroup("Input/Output");
        io.addArgument("-f").help("Input file").action(new StoreArgumentAction()).type(String.class).dest("input_file");
        io.addArgument("-o").help("Output file").action(new StoreArgumentAction()).type(String.class).dest("output_file");
        io.addArgument("--offset").help("Force offset (in binary data)").action(new StoreArgumentAction()).type(Integer.class).dest("offset").setDefault(0);

        ArgumentGroup mode = parser.addArgumentGroup("Mode");
        mode.addArgument("--deserialize").help("Java Serialized Binary to XML").action(new StoreTrueArgumentAction()).dest("deserialize").setDefault(false);
        mode.addArgument("--serialize").help("XML to Java Serialized Binary").action(new StoreTrueArgumentAction()).dest("serialize").setDefault(false);

        ArgumentGroup rpc = parser.addArgumentGroup("RPC Server - Serialize/Deserialize Java on-the-fly");
        rpc.addArgument("--rpcserver").help("Start RPC Server").action(new StoreTrueArgumentAction()).dest("rpcserver").setDefault(false);
        rpc.addArgument("-p", "--port").help("Listening port").action(new StoreArgumentAction()).type(Integer.class).dest("port").setDefault(8000);

        Namespace parsedArgs = parser.parseArgs(args);

        byte[] inputData = null;

        // Check input/output
        if (!parsedArgs.getBoolean("rpcserver")) {
            if (parsedArgs.getString("input_file") == null) {
                PrintUtils.printError("An input file must be provided");
                showHelpAndExit(parser);
            } else {
                if (!new File(parsedArgs.getString("input_file").strip()).exists()) {
                    PrintUtils.printError(String.format("Input file (%s) does not exist", parsedArgs.get("input_file")));
                    System.exit(0);
                }
            }

            if (parsedArgs.getBoolean("serialize") && parsedArgs.getInt("offset") != 0) {
                PrintUtils.printError("Cannot specify offset when serializing data");
                System.exit(0);
            }

            try {
                byte[] data = Files.readAllBytes(Paths.get(parsedArgs.getString("input_file")));
                inputData = Arrays.copyOfRange(data, parsedArgs.getInt("offset"), data.length);
            } catch (Exception e) {}

            if (!parsedArgs.getString("output_file").equals(null)) {
                String filename = parsedArgs.getString("output_file").strip();
                if (new File(filename).exists()) {
                    PrintUtils.printError(String.format("Output file (%s) already exists, choose a new file", parsedArgs.getString("output_file")));
                    System.exit(0);
                }
            }

            // Check mode
            if ((!parsedArgs.getBoolean("deserialize") && !parsedArgs.getBoolean("serialize"))) {
                PrintUtils.printError("A mode (serialize or deserialize) must be selected");
                showHelpAndExit(parser);
            }

            if ((parsedArgs.getBoolean("deserialize") && parsedArgs.getBoolean("serialize"))) {
                PrintUtils.printError("Choose one mode only");
                showHelpAndExit(parser);
            }
        }

        // -----------------------------------------------------------------------------
        // --- Processing --------------------------------------------------------------
        // -----------------------------------------------------------------------------
        ArrayList<Tuple> output = new ArrayList<>();

        if (parsedArgs.getBoolean("deserialize")) {
            PrintUtils.printTitle("Java Deserialization");
            System.out.println();
            Deserializer deserializer = new Deserializer(inputData);

            PrintUtils.printInfo(String.format("Input file length: %d (0x%02x) bytes", inputData.length, inputData.length));
            // System.out.println();
            // PrintUtils.hexDump(new String(deserializer.input.array()));

            PrintUtils.printInfo("Scanning input for Java Serialized data and try to deserialize it...");
            output = deserializer.execute();

            // Output into file
            if (parsedArgs.getString("output_file") != null && !output.isEmpty()) {
                if (FileUtils.writeToFile(parsedArgs.getString("output_file").strip(), output.toString())) {
                    PrintUtils.printSuccess(String.format("Output written into file \"%s\"", parsedArgs.getString("output_file")));
                } else {
                    PrintUtils.printError("An error occured when writing to file. Check permissions");
                }
            }
        } else if (parsedArgs.getBoolean("serialize")) {

        }

        if (parsedArgs.get("rpcserver")) {
            try {
                SimpleXMLRPCServer server = SimpleXMLRPCServer(("", parsedArgs.getInt("port")), requestHandler=RequestHandler);
                server.register_introspection_functions();
                server.register_instance(new RPCServer());
            } catch (Exception e) {
                PrintUtils.printError("Error occured when trying to start RPC server. Check if port already used.");
                System.exit(0);
            }

            PrintUtils.printTitle("RPC Server: ON - Waiting for calls...");
            System.out.println();
            signal.signal(signal.SIGINT, signal_handler);
            PrintUtils.printInfo("Press Ctrl+C to stop the server");
            System.out.println();
            server.serve_forever();
        }
    }

    private static void showHelpAndExit(ArgumentParser parser) {
        System.out.println();
        parser.printHelp();
        System.out.println();
        System.out.println("Examples:");
        System.out.println("---------");
        System.out.println();
        System.out.println("- Deserialize Java Serialized Binary data:");
        System.out.println("java -jar javaDeserializer.jar --deserialize -f data_serialized.raw -o data_deserialized.txt");
        System.out.println();
        System.out.println("- Serialize into Java binary data:");
        System.out.println("java -jar javaDeserializer.jar --serialize -f data_deserialized.txt -o data_serialized.raw");
        System.out.println();
        System.exit(0);
    }
}
