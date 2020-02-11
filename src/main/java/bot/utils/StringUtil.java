package bot.utils;

/**
 * This class is a not so official extension of {@link String} class,
 * to be able to write custom methods.
 */
public class StringUtil {

    /**
     * This method is written to
     * create a method to {@link String#contains(CharSequence)}
     * which supports case insensitivity.
     *
     * @param baseString the {@link String} in which you want to search
     * @param toSearch   the {@link String} to look for in the baseString
     * @return true if a match was found, otherwise false
     */
    public static boolean containsIgnoreCase(String baseString, String toSearch) {
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
}
