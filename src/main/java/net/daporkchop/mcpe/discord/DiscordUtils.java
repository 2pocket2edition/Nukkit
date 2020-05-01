package net.daporkchop.mcpe.discord;

import cn.nukkit.utils.TextFormat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class DiscordUtils {
    public static String getToken() {
        File f = new File(System.getProperty("user.dir") + "/discordtoken.txt");
        String token = "";

        if (!f.exists()) {
            try {
                PrintWriter writer = new PrintWriter(f.getAbsolutePath(), "UTF-8");
                Scanner s = new Scanner(System.in);

                System.out.println("Please enter your discord bot token");
                token = s.nextLine();
                writer.println(token);
                System.out.println("Successful. Starting...");

                s.close();
                writer.close();
            } catch (FileNotFoundException e) {
                System.out.println("impossible error kek");
                e.printStackTrace();
                System.exit(0);
            } catch (UnsupportedEncodingException e) {
                System.out.println("File encoding not supported!");
                e.printStackTrace();
                System.exit(0);
            }
        } else {
            try {
                Scanner s = new Scanner(f);

                token = s.nextLine();

                s.close();
            } catch (FileNotFoundException e) {
                System.out.println("impossible error kek");
                e.printStackTrace();
                System.exit(0);
            }
        }

        return token.trim();
    }

    public static String escape(String msg)  {
        return TextFormat.clean(msg)
                .replaceAll("(?<!\\\\)([_*~`])", "\\\\$1");
    }

    public static String clean(String msg)  {
        return TextFormat.clean(msg)
                .replaceAll("\\\\([_*~`])", "$1")
                .replaceAll("[^\\x20-\\xff]", "?");
    }
}
