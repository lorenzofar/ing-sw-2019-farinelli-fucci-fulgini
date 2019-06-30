package it.polimi.deib.se2019.sanp4.adrenaline.model.player;

import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.PlayerBoardView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionCardEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.action.ActionEnum;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.RoomColor;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class PlayerBoardTest {

    private final static String validName = "player1";
    private final static String enemyName = "Brutus";
    private static ActionCard validActionCard;
    private static PlayerColor validColor = PlayerColor.YELLOW;
    private static Player player;
    private static Player enemyPlayer;

    @BeforeClass
    public static void setup(){
        int validMaxActions = 2;
        Collection<ActionEnum> validActions = new ArrayList<>();
        validActions.add(ActionEnum.ADRENALINE_GRAB);
        validActions.add(ActionEnum.ADRENALINE_SHOOT);
        ActionCardEnum validType = ActionCardEnum.ADRENALINE1;
        ActionEnum validFinalAction = ActionEnum.RELOAD;
        String validDescription = "description1";
        RoomColor validColor = RoomColor.BLUE;

        validActionCard = new ActionCard(validMaxActions, validType, validActions, validFinalAction);

        player = new Player(validName, validActionCard, PlayerColor.BLUE);
        enemyPlayer = new Player(enemyName, validActionCard, PlayerColor.BLUE);
        PlayerBoard playerBoard = new PlayerBoard(player);
    }

    @Test(expected = NullPointerException.class)
    public void createBoard_nullPlayerProvided_shouldThrowException() {
        new PlayerBoard(null);
    }

    @Test
    public void createBoard_validPlayerProvided_shouldNotThrowException(){
        Player player = new Player(validName, validActionCard, validColor);
        PlayerBoard playerBoard = new PlayerBoard(player);
        assertEquals(player, playerBoard.getPlayer());
        assertEquals(0, playerBoard.getDeaths());
        assertEquals(0, playerBoard.getDamageCount());
        assertEquals(0, playerBoard.getDamages().size());
        assertNull(playerBoard.getKillshot());
        assertNull(playerBoard.getOverkill());
        assertTrue(playerBoard.getPlayerScores().values().stream().noneMatch(i -> i > 0));
        assertEquals(playerBoard.getDamageCount(), playerBoard.getDamages().size());
    }

    @Test
    public void addDeath_shouldIncreaseDeathsCount(){
        Player player = new Player(validName, validActionCard, validColor);
        PlayerBoard playerBoard = new PlayerBoard(player);
        int oldDeaths = playerBoard.getDeaths();
        playerBoard.addDeath();
        assertEquals(oldDeaths + 1, playerBoard.getDeaths());
    }

    @Test(expected = NullPointerException.class)
    public void addDamage_nullPlayerProvided_shouldThrowException(){
        Player player = new Player(validName, validActionCard, validColor);
        PlayerBoard playerBoard = new PlayerBoard(player);
        playerBoard.addDamage(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addDamage_negativeCountProvided_shouldThrowException(){
        Player player = new Player(validName, validActionCard, validColor);
        Player shooter = new Player("Another player", validActionCard, validColor);
        PlayerBoard playerBoard = new PlayerBoard(player);
        playerBoard.addDamage(shooter, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addDamage_playerIsBoardOwner_shouldThrowException() {
        Player player = new Player(validName, validActionCard, validColor);
        PlayerBoard playerBoard = new PlayerBoard(player);
        playerBoard.addDamage(player, 1);
    }

    @Test
    public void addDamage_validParametersProvided_shouldIncreaseDamageCount(){
        Player player = new Player(validName, validActionCard, validColor);
        Player shooter = new Player("Another player", validActionCard, validColor);
        PlayerBoard playerBoard = new PlayerBoard(player);
        int damage = 2;
        List<Player> oldDamages = playerBoard.getDamages();
        playerBoard.addDamage(shooter, damage);
        List<Player> currentDamages = playerBoard.getDamages();
        assertEquals(oldDamages.size() + damage, currentDamages.size());
        // Keep only the newly inserted damage
        oldDamages.forEach(currentDamages::remove);
        assertEquals(damage, currentDamages.size());
        currentDamages.forEach(p -> assertEquals(p, shooter));
    }

    @Test
    public void addDamage_hugeDamageProvided_DamageCountShouldNotExceedMax() {
        Player player = new Player(validName, validActionCard, validColor);
        Player shooter = new Player("Another player", validActionCard, validColor);
        PlayerBoard playerBoard = new PlayerBoard(player);
        int damage = 773;
        playerBoard.addDamage(shooter, damage);
        List<Player> currentDamages = playerBoard.getDamages();
        assertEquals(PlayerBoard.MAX_DAMAGES, currentDamages.size());
    }

    /* ===== MARKS ===== */
    @Test(expected = NullPointerException.class)
    public void addMark_nullPlayerProvided_shouldThrowException(){
        Player player = new Player(validName, validActionCard, validColor);
        PlayerBoard playerBoard = new PlayerBoard(player);
        playerBoard.addMark(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addMark_negativeCountProvided_shouldThrowException(){
        Player player = new Player(validName, validActionCard, validColor);
        Player shooter = new Player("Another player", validActionCard, validColor);
        PlayerBoard playerBoard = new PlayerBoard(player);
        playerBoard.addMark(shooter, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addMark_playerIsBoardOwner_shouldThrowException() {
        Player player = new Player(validName, validActionCard, validColor);
        PlayerBoard playerBoard = new PlayerBoard(player);
        playerBoard.addMark(player, 1);
    }

    @Test
    public void addMark_validParametersProvided_shouldUpdateMarksCount(){
        Player player = new Player(validName, validActionCard, validColor);
        Player shooter = new Player("Another player", validActionCard, validColor);
        PlayerBoard playerBoard = new PlayerBoard(player);
        int marks = 2;
        int oldShooterMarks = playerBoard.getMarksByPlayer(shooter);
        playerBoard.addMark(shooter, marks);
        assertEquals(oldShooterMarks + marks, playerBoard.getMarksByPlayer(shooter));
    }

    @Test
    public void addMark_tooManyMarksProvided_marksCountShouldBeCapped(){
        Player player = new Player(validName, validActionCard, validColor);
        Player shooter = new Player("Another player", validActionCard, validColor);
        PlayerBoard playerBoard = new PlayerBoard(player);
        int marks = PlayerBoard.MAX_MARKS_PER_PLAYER + 1;
        playerBoard.addMark(shooter, marks);
        assertEquals(PlayerBoard.MAX_MARKS_PER_PLAYER, playerBoard.getMarksByPlayer(shooter));
    }

    @Test(expected = NullPointerException.class)
    public void getMarksByPlayer_nullPlayerProvided_shouldThrowException(){
        Player player = new Player(validName, validActionCard, validColor);
        PlayerBoard playerBoard = new PlayerBoard(player);
        playerBoard.getMarksByPlayer(null);
    }

    @Test
    public void getMarksByPlayer_validPlayerProvided_shouldReturnNonNegativeNumber(){
        Player player = new Player(validName, validActionCard, validColor);
        Player shooter = new Player("Another player", validActionCard, validColor);
        PlayerBoard playerBoard = new PlayerBoard(player);
        assertTrue(playerBoard.getMarksByPlayer(shooter) >= 0);
    }

    @Test
    public void getKillshot_playerDead_shouldReturnNonNullPlayer(){
        Player player = new Player(validName, validActionCard, validColor);
        Player shooter = new Player("SHOOTER", validActionCard, validColor);
        player.getPlayerBoard().addDamage(shooter, PlayerBoard.KILLSHOT_DAMAGE);
        assertEquals(shooter, player.getPlayerBoard().getKillshot());
    }

    @Test
    public void getOverkill_playerDead_shouldReturnNonNullPlayer(){
        Player player = new Player(validName, validActionCard, validColor);
        Player shooter = new Player("SHOOTER", validActionCard, validColor);
        player.getPlayerBoard().addDamage(shooter, PlayerBoard.OVERKILL_DAMAGE);
        assertEquals(shooter, player.getPlayerBoard().getOverkill());
    }

    @Test
    public void getPlayerScores_damagesNotEmpty_retunedMapShouldOnlyContainShooters(){
        Player player = new Player(validName, validActionCard, validColor);
        List<Player> shooters = new ArrayList<>();
        for(int i=0; i<4; i++){
            shooters.add(new Player(String.format("Player%d", i), validActionCard, validColor));
        }
        shooters.forEach(shooter -> player.getPlayerBoard().addDamage(shooter, 2));
        Map<Player, Integer> playerScores = player.getPlayerBoard().getPlayerScores();
        assertTrue(shooters.containsAll(playerScores.keySet()));
        playerScores.values().forEach(score -> assertTrue(score >= 0));
    }

    @Test
    public void getPlayerScores_damagesNotEmptyAllShootersWithSameDamageCount_retunedMapShouldContainOneShooterOnly(){
        Player player = new Player(validName, validActionCard, validColor);
        List<Player> shooters = new ArrayList<>();
        for(int i=0; i<4; i++){
            shooters.add(new Player(String.format("Player%d", i), validActionCard, validColor));
        }
        shooters.forEach(shooter -> player.getPlayerBoard().addDamage(shooter, 2));
        Map<Player, Integer> playerScores = player.getPlayerBoard().getPlayerScores();
        assertTrue(shooters.containsAll(playerScores.keySet()));
        assertEquals(1, playerScores.values().stream().filter(i -> i > 0).count());
    }

    @Test
    public void getPlayerScores_damagesNotEmptyAllShootersWithDifferentDamageCount_retunedMapShouldContainAllShooters(){
        Player player = new Player(validName, validActionCard, validColor);
        List<Player> shooters = new ArrayList<>();
        for(int i=0; i<4; i++){
            shooters.add(new Player(String.format("Player%d", i), validActionCard, validColor));
        }
        for(int i=0; i<4; i++){
            player.getPlayerBoard().addDamage(shooters.get(i), i+1);
        }
        Map<Player, Integer> playerScores = player.getPlayerBoard().getPlayerScores();
        assertTrue(shooters.containsAll(playerScores.keySet()));
        assertTrue(playerScores.keySet().containsAll(shooters));
        playerScores.values().forEach(score -> assertTrue(score >= 0));
    }

    @Test
    public void getPlayerScores_frenzy_shouldNotCountFirstBlood() throws Exception {
        Player player = new Player(validName, validActionCard, validColor);
        PlayerBoard playerBoard = player.getPlayerBoard();
        playerBoard.turnFrenzy();
        Player shooter = new Player("player", validActionCard, validColor);
        playerBoard.addDamage(shooter, 3);
        Map<Player, Integer> playerScores = player.getPlayerBoard().getPlayerScores();

        /* Check no point for first blood */
        assertEquals(2, (int) playerScores.get(shooter));
    }

    @Test
    public void turnFrenzy_playerHasNoDamages_shouldTurnTheBoard() throws PlayerException {
        Player player = new Player(validName, validActionCard, validColor);
        PlayerBoard playerBoard = new PlayerBoard(player);
        // Here the player has no damages

        playerBoard.turnFrenzy(); /* Should throw no exception */
        assertEquals("frenzy", playerBoard.getState().toString());
    }

    @Test
    public void turnFrenzy_playerHasDamages_shouldThrowException(){
        Player player = new Player(validName, validActionCard, validColor);
        Player shooter = new Player("Another name", validActionCard, validColor);
        PlayerBoard playerBoard = new PlayerBoard(player);
        playerBoard.addDamage(shooter, 2);
        try {
            playerBoard.turnFrenzy();
            fail();
        } catch (PlayerException e) {
            assertFalse(playerBoard.getDamages().isEmpty());
        }
    }

    @Test
    public void updateDeathsAndReset_playerNotDead_shouldThrowException(){
        Player player = new Player(validName, validActionCard, validColor);
        PlayerBoard playerBoard = new PlayerBoard(player);
        try {
            playerBoard.updateDeathsAndReset();
        } catch (PlayerException e) {
            assertTrue(true);
        }
    }

    @Test
    public void updateDeathsAndReset_playerDead_deathsShouldBeIncrementedAndDamagesReset(){
        Player player = new Player(validName, validActionCard, validColor);
        Player shooter = new Player("Another name", validActionCard, validColor);
        PlayerBoard playerBoard = new PlayerBoard(player);
        playerBoard.addDamage(shooter, PlayerBoard.KILLSHOT_DAMAGE);
        int oldDeaths = playerBoard.getDeaths();
        try {
            playerBoard.updateDeathsAndReset();
        } catch (PlayerException e) {
            fail();
        }
        assertEquals(oldDeaths + 1, playerBoard.getDeaths());
        assertEquals(0, playerBoard.getDamages().size());
        assertEquals(0, playerBoard.getDamageCount());
    }

    @Test
    public void checkPlayerDead_playerNotDead_shouldReturnFalse(){
        Player player = new Player(validName, validActionCard, validColor);
        PlayerBoard playerBoard = new PlayerBoard(player);
        assertFalse(playerBoard.isDead());
    }

    @Test
    public void checkPlayerDead_playerDead_shouldReturnTrue(){
        Player player = new Player(validName, validActionCard, validColor);
        Player shooter = new Player("Another name", validActionCard, validColor);
        PlayerBoard playerBoard = new PlayerBoard(player);
        playerBoard.addDamage(shooter, PlayerBoard.KILLSHOT_DAMAGE);
        assertTrue(playerBoard.isDead());
    }

    @Test
    public void generateView_ShouldSucceed() throws PlayerException {
        PlayerBoard playerBoard = new PlayerBoard(player);
        playerBoard.addDeath();
        playerBoard.addDeath();
        playerBoard.turnFrenzy();
        playerBoard.addDamage(enemyPlayer, 6);
        playerBoard.addMark(enemyPlayer,2);

        PlayerBoardView view = playerBoard.generateView();

        assertEquals(playerBoard.getDamages().stream()
                                .map(Player::getName)
                                .collect(Collectors.toList()), view.getDamages());
        assertEquals(playerBoard.getDeaths(), view.getDeaths());
        assertEquals(playerBoard.getDeaths(), view.getDeaths());
        assertEquals(playerBoard.getState().toString(), view.getState());
    }
}
