package bot.factories;

import bot.utils.HexUtil;

public class HexUtilFactory {

    private static HexUtil hexUtil;

    private HexUtilFactory() {
        hexUtil = new HexUtil();
    }

    public static HexUtil getHexUtil() {
        if (hexUtil == null) {
            new HexUtilFactory();
        }

        return hexUtil;
    }
}
