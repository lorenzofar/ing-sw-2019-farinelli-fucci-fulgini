package it.polimi.deib.se2019.sanp4.adrenaline.client.cli;

import it.polimi.deib.se2019.sanp4.adrenaline.client.UIRenderer;

public class CLIRenderer extends UIRenderer {

    private static final String ADRENALINE_TITLE =
            "    ___    ____  ____  _______   _____    __    _____   ________\n" +
            "   /   |  / __ \\/ __ \\/ ____/ | / /   |  / /   /  _/ | / / ____/\n" +
            "  / /| | / / / / /_/ / __/ /  |/ / /| | / /    / //  |/ / __/\n" +
            " / ___ |/ /_/ / _, _/ /___/ /|  / ___ |/ /____/ // /|  / /_\n" +
            "/_/  |_/_____/_/ |_/_____/_/ |_/_/  |_/_____/___/_/ |_/_____/ \n";

    @Override
    public void showSplashScreen() {
        CLIHelper.printf(ADRENALINE_TITLE);
    }
}
