package it.polimi.deib.se2019.sanp4.adrenaline.model;

import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.BoardCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCardCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerColor;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;

import java.util.Arrays;
import java.util.List;
import java.util.logging.LogManager;

/**
 * Utility class to be used by other tests classes
 * Provides static methods to perform repetitive tasks
 */
public class ModelTestUtil {

    /**
     * Loads json schemas in {@link JSONUtils},
     * then loads standard resources in:
     * <ul>
     *     <li>{@link BoardCreator}</li>
     *     <li>{@link ActionCardCreator}</li>
     *     <li>{@link AmmoCardCreator}</li>
     *     <li>{@link PowerupCreator}</li>
     *     <li>{@link WeaponCreator}</li>
     * </ul>
     * Using the methods in this class without loading JSON resources in these creators first
     * might lead to unexpected behavior (e.g. empty card stacks)
     */
    public static void loadCreatorResources() {
        /* First load the schemas */
        JSONUtils.loadActionCardPackSchema("/schemas/action_card_pack.schema.json");
        JSONUtils.loadAmmoCardPackSchema("/schemas/ammo_card_pack.schema.json");
        JSONUtils.loadBoardPackSchema("/schemas/board_pack.schema.json");
        JSONUtils.loadBoardSchema("/schemas/board.schema.json");
        JSONUtils.loadPowerupPackSchema("/schemas/powerup_pack.schema.json");
        JSONUtils.loadWeaponPackSchema("/schemas/weapon_pack.schema.json");
        JSONUtils.loadWeaponSchema("/schemas/weapon.schema.json");
        /* TODO: Add player character schema */

        /* Then load the assets */
        ActionCardCreator.loadActionCardPack("/assets/standard_actioncards.json");
        AmmoCardCreator.loadAmmoCardPack("/assets/standard_ammocards.json");
        BoardCreator.loadBoardPack("/assets/standard_boards.json");
        PowerupCreator.loadPowerupPack("/assets/standard_powerups.json");
        WeaponCreator.loadWeaponPack("/assets/standard_weapons.json");
    }

    public static void disableLogging() {
        LogManager.getLogManager().reset();
    }

    public static ActionCard generateActionCard() {
        List<ActionEnum> actions = Arrays.asList(ActionEnum.ADRENALINE_SHOOT, ActionEnum.ADRENALINE_GRAB);
        return new ActionCard(2, ActionCardEnum.ADRENALINE2, actions, ActionEnum.RELOAD);
    }

    public static Player generatePlayer(String name) {
        return new Player(name, generateActionCard(), PlayerColor.GREEN);
    }
}
