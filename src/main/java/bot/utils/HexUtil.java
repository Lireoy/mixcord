package bot.utils;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class HexUtil {

    private Pattern pattern;
    private Matcher matcher;

    private static final String HEX_PATTERN = "(^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$)|^([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";

    public HexUtil() {
        pattern = Pattern.compile(HEX_PATTERN);
    }

    /**
     * Validate hex with regular expression
     *
     * @param hex hex for validation
     * @return true valid hex, false invalid hex
     */
    public boolean validateHex(final String hex) {
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    private static Color hex2Rgb(String hex) {
        return new Color(
                Integer.valueOf(hex.substring(0, 2), 16),
                Integer.valueOf(hex.substring(2, 4), 16),
                Integer.valueOf(hex.substring(4, 6), 16));
    }

    public static Color formatForEmbed(String hex) {
        if (hex.startsWith("#")) {
            return HexUtil.hex2Rgb(hex.substring(1));
        } else {
            return HexUtil.hex2Rgb(hex);
        }
    }
}
