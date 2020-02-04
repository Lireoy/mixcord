package bot.utils;

public class StringUtil {

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
