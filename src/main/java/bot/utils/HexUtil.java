package bot.utils;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class helps validate and convert hex colors.
 * Also helps formatting the color for {@link net.dv8tion.jda.api.EmbedBuilder}.
 */
@Slf4j
public class HexUtil {

    private static final String HEX_PATTERN = "(^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$)|^([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";

    private Pattern pattern;
    private Matcher matcher;

    /**
     * Creates a new instance for {@link HexUtil} and compiles the pattern for validating HEX color values.
     */
    public HexUtil() {
        pattern = Pattern.compile(HEX_PATTERN);
    }

    /**
     * Validates hex with regular expression.
     *
     * @param hex hex for validation
     * @return true valid hex, false invalid hex
     */
    public boolean validateHex(final String hex) {
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    /**
     * Converts hex color to rgb.
     *
     * @param hex hex for color convert
     * @return the rgb color component
     */
    private static Color hex2Rgb(final String hex) {
        return new Color(
                Integer.valueOf(hex.substring(0, 2), 16),
                Integer.valueOf(hex.substring(2, 4), 16),
                Integer.valueOf(hex.substring(4, 6), 16));
    }

    /**
     * Formats a hex color and then converts it to rgb
     *
     * @param hex hex for color convert
     * @return the rgb color component
     */
    public static Color formatForEmbed(final String hex) {
        if (hex.startsWith("#")) {
            return HexUtil.hex2Rgb(hex.substring(1));
        } else {
            return HexUtil.hex2Rgb(hex);
        }
    }
}
