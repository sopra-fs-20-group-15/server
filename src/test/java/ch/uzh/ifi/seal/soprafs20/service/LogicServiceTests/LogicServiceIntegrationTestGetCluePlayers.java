package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.PlayerNameDTO;
import ch.uzh.ifi.seal.soprafs20.service.*;
import ch.uzh.ifi.seal.soprafs20.service.StatesForLogicService.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ch.uzh.ifi.seal.soprafs20.Helper.TestSETUPCreatesActiveGame;

import static org.junit.jupiter.api.Assertions.*;


import java.util.List;
import java.util.Map;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceIntegrationTestGetCluePlayers extends TestSETUPCreatesActiveGame{

    @Autowired
    public LogicServiceIntegrationTestGetCluePlayers(@Qualifier("cardService") CardService cardService, @Qualifier("gameSetUpService") GameSetUpService gameSetUpService, @Qualifier("logicService") LogicService logicService, @Qualifier("playerService") PlayerService playerService, @Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("gameSetUpEntityRepository") GameSetUpRepository gameSetUpRepository, @Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("activeGameService") ActiveGameService activeGameService, LSStateChooseMysteryWord lsStateChooseMysteryWord, LSSGiveClues lssGiveClues, LSSGiveGuess lssGiveGuess, LSSWordReveal lssWordReveal, LSSGameHasEnded lssGameHasEnded) {
        super(cardService, logicService, gameSetUpService, playerService, playerRepository, activeGameService, gameRepository, gameSetUpRepository, lsStateChooseMysteryWord, lssGiveClues, lssGiveGuess, lssWordReveal, lssGameHasEnded);
    }

    @Test
    public void getCluePlayersWorksWithMultipleCluesGiven() {

        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();

        cluePostDTO.setPlayerToken("One");
        cluePostDTO.setClue("OneClue");
        logicService.giveClue(createdActiveGame.getId(), cluePostDTO);

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("ThreeClue");
        logicService.giveClue(createdActiveGame.getId(), cluePostDTO);
        Map<String,String> test= createdActiveGame.getClueMap();
        test.put(createdActiveGame.getBots().get(0).getToken(), "CaillouTheBaldBoy");
        createdActiveGame.setClueMap(test);

        List<PlayerNameDTO> playerNames= logicService.getCluePlayers(createdActiveGame);
        assertEquals(3, playerNames.size());
        //create list with player names and bot names for assertion
        List<String> names =new ArrayList<>();
        names.add("TwoName");
        names.add("ThreeName");
        names.add(createdActiveGame.getBots().get(0).getName());
        //check that all playerNames are the expected ones
        for (PlayerNameDTO pn: playerNames) {
            assertTrue(names.contains(pn.getPlayerName()));
        }

    }

    @Test
    public void getCluePlayersWorksWithNoCluesGiven() {
        createdActiveGame.setActiveMysteryWord("Test");

        assertTrue(createdActiveGame.getValidClues().isEmpty());

        List<PlayerNameDTO> playerNames= logicService.getCluePlayers(createdActiveGame.getId());
        assertEquals(0, playerNames.size());
        assertNotNull(playerNames);
    }
}
