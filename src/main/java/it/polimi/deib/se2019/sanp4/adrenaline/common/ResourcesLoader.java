package it.polimi.deib.se2019.sanp4.adrenaline.common;

import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.BoardCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCardCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;

public class ResourcesLoader {
    public static void loadCreatorResources(){
        // Load schemas
        JSONUtils.loadActionCardPackSchema("/schemas/action_card_pack.schema.json");
        JSONUtils.loadAmmoCardPackSchema("/schemas/ammo_card_pack.schema.json");
        JSONUtils.loadBoardPackSchema("/schemas/board_pack.schema.json");
        JSONUtils.loadBoardSchema("/schemas/board.schema.json");
        JSONUtils.loadPowerupPackSchema("/schemas/powerup_pack.schema.json");
        JSONUtils.loadWeaponPackSchema("/schemas/weapon_pack.schema.json");
        JSONUtils.loadWeaponSchema("/schemas/weapon.schema.json");

        // Load assets
        ActionCardCreator.loadActionCardPack("/assets/standard_actioncards.json");
        AmmoCardCreator.loadAmmoCardPack("/assets/standard_ammocards.json");
        BoardCreator.loadBoardPack("/assets/standard_boards.json");
        PowerupCreator.loadPowerupPack("/assets/standard_powerups.json");
        WeaponCreator.loadWeaponPack("/assets/standard_weapons.json");
    }
}
