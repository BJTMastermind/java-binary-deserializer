package me.bjtmastermind.java_binary_deserializer.utils;

import java.util.ArrayList;

import me.bjtmastermind.java_binary_deserializer.utils.terminal.Fore;
import me.bjtmastermind.java_binary_deserializer.utils.terminal.Style;

public class PrintUtils {

    public static void hexDump(String src) {
        hexDump(src, 16);
    }

    public static void hexDump(String src, int length) {
        String FILTER = "";
        for (int x = 0; x < 256; x++) {
            char c = (char) x;
            if (Character.isISOControl(c) || Character.isWhitespace(c) && c != ' ') {
                FILTER += ".";
            } else {
                FILTER += c;
            }
        }
        ArrayList<String> lines = new ArrayList<String>();
        for (int c = 0; c < src.length(); c += length) {
            String chars = src.substring(c, c + length);
            String hex = "";
            for (int x = 0; x < chars.length(); x++) {
                hex = String.join(" ", String.format("%02x ", (int) chars.charAt(x)));
            }
            String printable = "";
            for (int x = 0; x < chars.length(); x++) {
                if (x <= 127) {
                    printable = String.join(" ", FILTER.substring(x, x + 1));
                }
            }
            lines.add(String.format("%04x  %s  %s\n", c, hex, printable));
        }
        System.out.println(String.join("", lines));
    }

    public static void printXMLHighlighted(String xml) {
        System.out.println(xml);
    }

    public static void printTitle(String title) {
        System.out.println(Style.BRIGHT + Fore.YELLOW + title + Style.RESET_ALL);
    }

    public static void printError(String reason) {
        System.out.println(Style.BRIGHT + Fore.RED + "[!] " + reason.strip() + Style.RESET_ALL);
    }

    public static void printWarning(String reason) {
        System.out.println(Style.BRIGHT + Fore.YELLOW + "[!] " + Style.RESET_ALL + reason.strip());
    }

    public static void printSuccess(String reason) {
        System.out.println(Style.BRIGHT + Fore.GREEN + "[+] " + Style.NORMAL + reason.strip() + Style.RESET_ALL);
    }

    public static void printInfo(String info) {
        System.out.println(Style.BRIGHT + "[~] " + Style.RESET_ALL + info.strip());
    }

    public static void printDelimiter() {
        System.out.println("-".repeat(80));
    }
}
