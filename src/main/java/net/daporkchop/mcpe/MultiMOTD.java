package net.daporkchop.mcpe;

import net.twoptwoe.mobplugin.utils.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

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
            } else {
                file.createNewFile();
                motds = new ArrayList<>();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public static synchronized String getMOTD() {
        return "\u00A7c" + (motds.size() == 0 ? "2p2e - 2pocket2edition" : motds.get(Utils.rand(0, motds.size()))) + "\u00A7r\u00A7f";
    }

    public static synchronized void putMOTD(String motd) {
        motds.add(motd);
        motds.sort(String.CASE_INSENSITIVE_ORDER);
        try (Writer output = new BufferedWriter(new FileWriter(new File(".", "motds.txt"), false))) {
            for (String s : motds) {
                output.append(s).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
