package bot.utils;

import bot.constants.BotConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This class is a complementary class for the {@link String} class,
 * with custom methods, written for Mixcord's use cases.
 */

@Slf4j
public class StringUtil {

    /**
     * This method checks for matches between two
     * {@link String}s with case insensitivity.
     *
     * @param baseString the {@link String} in which you want to search
     * @param toSearch   the {@link String} to look for in the baseString
     * @return true if a match was found, otherwise false
     */
    public static boolean containsIgnoreCase(final String baseString, final String toSearch) {
        if (baseString == null || toSearch == null) return false;

        final int length = toSearch.length();
        if (length == 0)
            return false;

        for (int i = baseString.length() - length; i >= 0; i--) {
            if (baseString.regionMatches(true, i, toSearch, 0, length))
                return true;
        }
        return false;
    }

    /**
     * This method splits a {@link String} into
     * two parts at the first occurrence of a coma.
     *
     * @param args the {@link String} to split
     * @return a {@link String} array with two elements in it
     */
    public static String[] separateArgs(final String args, final int numberOfArgs) {
        return args.trim().split(",", numberOfArgs);
    }

    public static String replaceLastComma(final String text) {
        return text.replaceFirst("(?s)(.*)" + ", ", "$1" + "");
    }

    public static String generateRandomString(int minLength, int maxLength) {
        List<Character> letters = new ArrayList<>();
        for (char c : BotConstants.RANDOM_GEN_STRING.toCharArray())
            letters.add(c);

        Collections.shuffle(letters);
        StringBuilder randomString = new StringBuilder();

        Random random = new Random();
        for (int i = random.nextInt(maxLength - minLength) + minLength; i > 0; --i) {
            randomString.append(letters.get(random.nextInt(letters.size())));
        }

        return randomString.toString();
    }

    public static void displayAscii() {
        try (FileReader fr = new FileReader("MixcordASCII.txt")) {
            int i;
            while ((i = fr.read()) != -1) {
                System.out.print((char) i);
            }
        } catch (IOException e) {
            log.warn("Could not find ASCII art.");
        }
    }
}
