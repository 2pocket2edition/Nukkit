package net.daporkchop.mcpe;

import net.twoptwoe.mobplugin.utils.Utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Stores multiple MOTDs and allows for grabbing random ones
 */
public class MultiMOTD {
    private static List<String> motds;

    static {
        try {
            File file = new File(".", "motds.txt");
            if (file.exists()) {
                motds = Files.readAllLines(file.getAbsoluteFile().toPath(), StandardCharsets.UTF_8);
                motds.removeIf(String::isEmpty);
                motds.sort(String.CASE_INSENSITIVE_ORDER);
            } else {
                file.createNewFile();
                motds = new ArrayList<>();
            }
        } catch (Throwable t)   {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public static String getMOTD() {
        synchronized (motds) {
            return motds.size() == 0 ? "\u00A7cjeff" : "\u00A7c" + motds.get(Utils.rand(0, motds.size()));
        }
    }

    public static void putMOTD(String motd) {
        synchronized (motds) {
            motds.add(motd);
            motds.sort(String.CASE_INSENSITIVE_ORDER);
            try (Writer output = new BufferedWriter(new FileWriter(new File(".", "motds.txt"), false))) {
                for (String s : motds)   {
                    output.append(s).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
