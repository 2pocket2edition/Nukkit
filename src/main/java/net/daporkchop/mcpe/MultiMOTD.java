package net.daporkchop.mcpe;

import net.twoptwoe.mobplugin.utils.Utils;

import java.io.*;
import java.nio.charset.Charset;
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
                motds = Files.readAllLines(file.getAbsoluteFile().toPath(), Charset.forName("UTF-8"));
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
            return motds.size() == 0 ? "jeff" : motds.get(Utils.rand(0, motds.size()));
        }
    }

    public static void putMOTD(String motd) {
        synchronized (motds) {
            motds.add(motd);
            try {
                Writer output = new BufferedWriter(new FileWriter(new File(".", "motds.txt"), true));
                output.append(motd);
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
