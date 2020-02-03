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

    @Deprecated
    public static boolean containsString(String original, String tobeChecked, boolean caseSensitive) {
        if (caseSensitive) {
            return original.contains(tobeChecked);
        } else {
            return original.toLowerCase().contains(tobeChecked.toLowerCase());
        }
    }
}
