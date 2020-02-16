package bot.utils;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.regex.Pattern;

/**
 * This class helps validate and convert hex colors.
 * Also helps formatting the color for {@link net.dv8tion.jda.api.EmbedBuilder}.
 * Only six characters long hex values are supported.
 */
@Slf4j
public class HexUtil {

    private static Pattern pattern;

    /**
     * Creates a new instance for {@link HexUtil} and compiles the pattern for validating HEX color values.
     */
    public HexUtil() {
        //String HEX_PATTERN = "(^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$)|^([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        String HEX_PATTERN = "(^#([A-Fa-f0-9]{6})$)|^([A-Fa-f0-9]{6})$";
        pattern = Pattern.compile(HEX_PATTERN);
    }

    /**
     * Validates hex with regular expression.
     *
     * @param hex hex for validation
     * @return true for valid hex, otherwise false
     */
    public boolean validateHex(final String hex) {
        return pattern.matcher(hex).matches();
    }

    /**
     * Converts hex color to rgb.
     *
     * @param hex hex to convert to {@link Color}
     * @return the rgb {@link Color} component
     */
    private Color hex2Rgb(final String hex) {
        return new Color(
                Integer.valueOf(hex.substring(0, 2), 16),
                Integer.valueOf(hex.substring(2, 4), 16),
                Integer.valueOf(hex.substring(4, 6), 16));
    }

    /**
     * Formats a hex color and then converts it to rgb.
     *
     * @param hex hex to convert to {@link Color}
     * @return the rgb {@link Color} component
     */
    public Color formatForEmbed(final String hex) {
        return this.hex2Rgb(formatHex(hex));
    }

    /**
     * Removes the hash symbol from the beginning of the hex value if found.
     *
     * @param hex the hex value to format
     * @return a hex value in a {@link String} without a hash symbol
     */
    public String formatHex(final String hex) {
        if (hex.startsWith("#")) {
            return hex.substring(1);
        } else {
            return hex;
        }
    }
}
