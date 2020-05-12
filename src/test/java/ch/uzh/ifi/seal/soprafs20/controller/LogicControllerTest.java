
package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.*;
import ch.uzh.ifi.seal.soprafs20.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.*;


import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(LogicController.class)
public class LogicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private ActiveGameService gameService;

    @MockBean
    private ValidationService validationService;

    @MockBean
    private PlayerService playerService;

    @MockBean
    private LogicService logicService;


    /**Tests a Put-Request to /games/{gameId}/initializations*/
    @Test
    public void putInitializesSuccessfully() throws Exception {
        // given
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setPlayerToken("AAJKHS");
        GameEntity game = new GameEntity();
        //returns

        given(validationService.checkPlayerIsActivePlayerOfGame(Mockito.anyString(), Mockito.anyLong())).willReturn(true);
        given(logicService.initializeTurn(Mockito.any())).willReturn(game);

        // when/then -> do the request + validate the result

        MockHttpServletRequestBuilder putRequest = put("/games/{gameId}/initializations", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(tokenDTO));

        // then

        mockMvc.perform(putRequest)
                .andExpect(status().isOk());


    }

    /**Tests a put-Request to /games/{gameId}/mysteryWord*/
    @Test
    public void putRequestActivePlayerChoosesWordOnCardSuccessfully() throws Exception {
        // given
        GameEntity game = new GameEntity();
        CardPostDTO cardPostDTO = new CardPostDTO();
        cardPostDTO.setPlayerToken("df");
        cardPostDTO.setWordId(1L);
        //returns
        WordPostDTO wordPostDTO = new WordPostDTO();
        wordPostDTO.setWord("Eis");

        given(validationService.checkPlayerIsActivePlayerOfGame(Mockito.anyString(), Mockito.anyLong())).willReturn(true);
        given(gameService.getGameById(Mockito.any())).willReturn(new GameEntity());
        given(cardService.getCardById(Mockito.any())).willReturn(new CardEntity());
        given(cardService.chooseWordOnCard(Mockito.any(), Mockito.any())).willReturn("Eis");

        // when/then -> do the request + validate the result

        MockHttpServletRequestBuilder putRequest = put("/games/{gameId}/mysteryWord", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(cardPostDTO));

        // then

        mockMvc.perform(putRequest)
                .andExpect(status().isCreated());


    }

    /**
     * Tests a get-Request to /games/{gameId}/guesses/{playerToken}/
     */
    @Test
    public void getGuessRequestPassivePlayerWorksSuccessful() throws Exception {
        // given
        GuessGetDTO guessGetDTO = new GuessGetDTO();
        guessGetDTO.setGuess("test");
        guessGetDTO.setIsValidGuess(true);

        //Game
        GameEntity game = new GameEntity();
        game.setGuess("test");
        game.setIsValidGuess(true);

        /**
         doReturn(true).when(validationService.checkPlayerIsPassivePlayerOfGame(Mockito.any(),Mockito.any()));
         doReturn(game).when(gameService.getGameById(Mockito.any()));*/
        given(validationService.checkPlayerIsPassivePlayerOfGame(Mockito.any(), Mockito.any())).willReturn(true);
        given(gameService.getGameById(Mockito.any())).willReturn(game);

        // when/then -> do the request + validate the result

        MockHttpServletRequestBuilder getRequest = get("/games/{gameId}/guesses/{playerToken}/", 1, "df")
                .contentType(MediaType.APPLICATION_JSON);

        // then

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guess", is(guessGetDTO.getGuess())))
                .andExpect(jsonPath("$.isValidGuess", is(guessGetDTO.getIsValidGuess())));
    }

    /**
     * Tests a get-Request to /games/{gameId}/guesses/{playerToken}/
     */
    @Test
    public void setGuessRequestActivePlayerWorksSuccessful() throws Exception {
        // given
        GuessPostDTO guessPostDTO = new GuessPostDTO();
        guessPostDTO.setGuess("test");
        guessPostDTO.setPlayerToken("token");

        //Game
        GameEntity game = new GameEntity();
        game.setGuess("");
        game.setActiveMysteryWord("test");
        Map<String, String> map= new HashMap<>();
        map.put("Test", "Test");
        game.setValidClues(map);
        /**
         doReturn(true).when(validationService.checkPlayerIsPassivePlayerOfGame(Mockito.any(),Mockito.any()));
         doReturn(game).when(gameService.getGameById(Mockito.any()));*/
        given(validationService.checkPlayerIsActivePlayerOfGame(Mockito.anyString(),Mockito.anyLong())).willReturn(true);
        given(gameService.getGameById(Mockito.any())).willReturn(game);

        // when/then -> do the request + validate the result

        MockHttpServletRequestBuilder postRequest = post("/games/{gameId}/guesses","1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(guessPostDTO));

        // then

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated());
    }

    /**
     * Tests a get-Request to /games/{gameId}/activeWord/{playerToken}/
     */
    @Test
    public void getRequestPassivePlayerGetWordSuccessful() throws Exception {
        // given
        CardPostDTO cardPostDTO = new CardPostDTO();
        cardPostDTO.setPlayerToken("df");
        cardPostDTO.setWordId(1L);
        //Game
        GameEntity game = new GameEntity();
        game.setActiveMysteryWord("Eis");
        //returns
        WordPostDTO wordPostDTO = new WordPostDTO();
        wordPostDTO.setWord("Eis");

        /**
         doReturn(true).when(validationService.checkPlayerIsPassivePlayerOfGame(Mockito.any(),Mockito.any()));
         doReturn(game).when(gameService.getGameById(Mockito.any()));*/
        given(validationService.checkPlayerIsPassivePlayerOfGame(Mockito.any(), Mockito.any())).willReturn(true);
        given(gameService.getGameById(Mockito.any())).willReturn(game);
        given(logicService.getMysteryWord(Mockito.any())).willReturn("Eis");

        // when/then -> do the request + validate the result

        MockHttpServletRequestBuilder postRequest = get("/games/{gameId}/mysteryWord/{playerToken}/", 1, "df")
                .contentType(MediaType.APPLICATION_JSON);

        // then

        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.word", is(wordPostDTO.getWord())));

    }
    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input can be processed
     * <p>
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     *
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new SopraServiceException(String.format("The request body could not be created.%s", e.toString()));
        }
    }


    /**Tests a post-Request to /games/{gameId}/Clues/*/
    @Test
    public void postRequestPassivePlayerSuccessfullyGivesClue() throws Exception {
        CluePostDTO clue=new CluePostDTO();
        clue.setClue("clue");
        clue.setPlayerToken("token");
        GameEntity game = new GameEntity();
        game.setActiveMysteryWord("Test");
        Map<String, String> clueList= new HashMap<>();
        List<PlayerEntity> players= new ArrayList<>();
        game.setClueMap(clueList);
        game.setPlayers(players);

        PlayerEntity player = new PlayerEntity();
        player.setUsername("playerName");
        game.getPlayers().add(player);
        game.getPlayers().add(new PlayerEntity());
        game.setActiveMysteryWord("RandomThing");

        given(validationService.checkPlayerIsPassivePlayerOfGame(Mockito.anyString(),Mockito.anyLong())).willReturn(true);
        given(gameService.getGameById(Mockito.any())).willReturn(game);
        given(playerService.getPlayerByToken(Mockito.anyString())).willReturn(player);

        // when/then -> do the request + validate the result

        MockHttpServletRequestBuilder postRequest = post("/games/{gameId}/clues/", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(clue));

        // then

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated());

    }

    @Test
    public void postRequestPassivePlayerTriesToGiveClueSecondTime() throws Exception {
        CluePostDTO clue=new CluePostDTO();
        clue.setClue("clue");
        clue.setPlayerToken("token");
        GameEntity game = new GameEntity();
        game.setActiveMysteryWord("Test");
        Map<String, String> clueList= new HashMap<>();
        List<PlayerEntity> players= new ArrayList<>();
        game.setClueMap(clueList);
        game.setPlayers(players);
        game.setActiveMysteryWord("RandomThing");

        PlayerEntity player = new PlayerEntity();
        player.setUsername("playerName");
        game.getPlayers().add(player);
        game.getPlayers().add(new PlayerEntity());

        given(validationService.checkPlayerIsPassivePlayerOfGame(Mockito.anyString(),Mockito.anyLong())).willReturn(true);
        given(gameService.getGameById(Mockito.any())).willReturn(game);
        given(playerService.getPlayerByToken(Mockito.anyString())).willReturn(player);
        doThrow(new UnauthorizedException("Test")).when(logicService).giveClue(Mockito.any(),Mockito.any());

        MockHttpServletRequestBuilder postRequest = post("/games/{gameId}/clues/", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(clue));

        // then

        mockMvc.perform(postRequest)
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void postRequestActivePlayerTriesToGiveClue() throws Exception {
        CluePostDTO clue=new CluePostDTO();
        clue.setClue("clue");
        clue.setPlayerToken("token");
        GameEntity game = new GameEntity();
        Map<String, String> clueList= new HashMap<>();
        List<PlayerEntity> players= new ArrayList<>();
        game.setClueMap(clueList);
        game.setPlayers(players);

        PlayerEntity player = new PlayerEntity();
        player.setUsername("playerName");
        game.getPlayers().add(player);
        game.getPlayers().add(new PlayerEntity());

        given(validationService.checkPlayerIsPassivePlayerOfGame(Mockito.anyString(),Mockito.anyLong())).willThrow(new UnauthorizedException(""));
        given(gameService.getGameById(Mockito.any())).willReturn(game);
        given(playerService.getPlayerByToken(Mockito.anyString())).willReturn(player);

        // when/then -> do the request + validate the result

        MockHttpServletRequestBuilder postRequest = post("/games/{gameId}/clues/", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(clue));

        // then

        mockMvc.perform(postRequest)
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void playerNotInGameTriesToGiveClueOrGameDoesNotExistOrBoth() throws Exception {
        CluePostDTO clue=new CluePostDTO();
        clue.setClue("clue");
        clue.setPlayerToken("token");
        GameEntity game = new GameEntity();
        Map<String, String> clueList= new HashMap<>();
        List<PlayerEntity> players= new ArrayList<>();
        game.setClueMap(clueList);
        game.setPlayers(players);

        PlayerEntity player = new PlayerEntity();
        player.setUsername("playerName");
        game.getPlayers().add(player);
        game.getPlayers().add(new PlayerEntity());

        given(validationService.checkPlayerIsPassivePlayerOfGame(Mockito.anyString(),Mockito.anyLong())).willThrow(new NotFoundException(""));
        given(gameService.getGameById(Mockito.any())).willReturn(game);
        given(playerService.getPlayerByToken(Mockito.anyString())).willReturn(player);

        // when/then -> do the request + validate the result

        MockHttpServletRequestBuilder postRequest = post("/games/{gameId}/clues/", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(clue));

        // then

        mockMvc.perform(postRequest)
                .andExpect(status().isNotFound());

    }

    /**Tests a get-Request to /games/{gameId}/clues/{playerToken}*/
    @Test
    public void playerGetsAllValidClues() throws Exception {
        GameEntity game=new GameEntity();
        ClueGetDTO clue1= new ClueGetDTO();
        ClueGetDTO clue2= new ClueGetDTO();
        clue1.setPlayerName("joe");
        clue1.setClue("test");
        clue2.setPlayerName("charlotte");
        clue2.setClue("toast");
        List<ClueGetDTO> list =new ArrayList<>();


        given(validationService.checkPlayerIsPartOfGame(Mockito.anyString(),Mockito.anyLong())).willReturn(true);
        given(gameService.getGameById(Mockito.any())).willReturn(game);
        given(logicService.getClues(Mockito.any())).willReturn(list);



        // when/then -> do the request + validate the result

        MockHttpServletRequestBuilder postRequest = get("/games/{gameId}/clues/{playerToken}", "123","test");
        // then

        mockMvc.perform(postRequest)
                .andExpect(status().isOk());

    }

    @Test
    public void playerNotFromGameTriesGetClues() throws Exception {
        given(validationService.checkPlayerIsPartOfGame(Mockito.anyString(),Mockito.anyLong())).willThrow(new UnauthorizedException("test"));

        MockHttpServletRequestBuilder postRequest = get("/games/{gameId}/clues/{playerToken}", "123","test");

        mockMvc.perform(postRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void playerOrGameDoesNotExistGetClues() throws Exception {
        given(validationService.checkPlayerIsPartOfGame(Mockito.anyString(),Mockito.anyLong())).willThrow(new NotFoundException("test"));

        MockHttpServletRequestBuilder postRequest = get("/games/{gameId}/clues/{playerToken}", "123","test");

        mockMvc.perform(postRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void validCluesNotSetYet() throws Exception {
        GameEntity game=new GameEntity();


        given(validationService.checkPlayerIsPartOfGame(Mockito.anyString(),Mockito.anyLong())).willReturn(true);
        given(gameService.getGameById(Mockito.any())).willReturn(game);
        given(logicService.getClues(Mockito.any())).willThrow(new NoContentException("Test"));

        MockHttpServletRequestBuilder postRequest = get("/games/{gameId}/clues/{playerToken}", "123","test");
        // then

        mockMvc.perform(postRequest)
                .andExpect(status().isNoContent());

    }

    /**Tests a get-Request to /games/{gameId}/clues/players/{playerToken}*/
    @Test
    public void getCluePlayersWorks() throws Exception {
        GameEntity game=new GameEntity();
        List<PlayerNameDTO> list=new ArrayList<>();
        PlayerNameDTO playerNameDTO= new PlayerNameDTO();
        playerNameDTO.setPlayerName("Test");
        list.add(playerNameDTO);

        given(validationService.checkPlayerIsPartOfGame(Mockito.anyString(),Mockito.anyLong())).willReturn(true);
        given(gameService.getGameById(Mockito.any())).willReturn(game);
        given(logicService.getCluePlayers(Mockito.any())).willReturn(list);

        MockHttpServletRequestBuilder postRequest = get("/games/{gameId}/clues/players/{playerToken}", "123","test");


        mockMvc.perform(postRequest)
                .andExpect(status().isOk());

    }


    @Test
    public void getCluePlayersFailsBecausePlayerNotInGame() throws Exception {

        given(validationService.checkPlayerIsPartOfGame(Mockito.anyString(),Mockito.anyLong())).willThrow(new UnauthorizedException("Test"));

        MockHttpServletRequestBuilder postRequest = get("/games/{gameId}/clues/players/{playerToken}", "123","test");


        mockMvc.perform(postRequest)
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void getCluePlayersFailsBecauseGameIdHasWrongFormat() throws Exception {
        MockHttpServletRequestBuilder postRequest = get("/games/{gameId}/clues/players/{playerToken}", "abc","test");
        // then

        mockMvc.perform(postRequest)
                .andExpect(status().isConflict());

    }

    /**Tests get Request to /games/{gameId}/statistics*/
    @Test
    public void getStatistics() throws Exception {


        given(logicService.getStatistics(Mockito.any())).willReturn(new ArrayList<StatisticsGetDTO>());

        MockHttpServletRequestBuilder getRequest = get("/games/{gameId}/statistics", "1");
        // then

        mockMvc.perform(getRequest)
                .andExpect(status().isOk());

    }

    /**Tests a get-Request to /games/{gameId}/cards/remainder/{playerToken}*/
    @Test
    public void getCardAmountWokrs() throws Exception {
        GameEntity game= new GameEntity();
        CardsRemainingDTO cardsRemainingDTO= new CardsRemainingDTO();
        cardsRemainingDTO.setCardsOnStack(8);

        given(validationService.checkPlayerIsPartOfGame(Mockito.anyString(),Mockito.anyLong())).willReturn(true);
        given(gameService.getGameById(Mockito.any())).willReturn(game);
        given(logicService.getCardAmount(Mockito.any())).willReturn(cardsRemainingDTO);

        // when/then -> do the request + validate the result

        MockHttpServletRequestBuilder postRequest = get("/games/{gameId}/cards/remainder/{playerToken}", "123","test");
        // then

        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardsOnStack", is( 8)));

    }

    @Test
    public void getCardAmountFailsBecauseOfWrongFormatOfGameId() throws Exception {
        GameEntity game= new GameEntity();
        CardsRemainingDTO cardsRemainingDTO= new CardsRemainingDTO();
        cardsRemainingDTO.setCardsOnStack(8);

        given(validationService.checkPlayerIsPartOfGame(Mockito.anyString(),Mockito.anyLong())).willReturn(true);
        given(gameService.getGameById(Mockito.any())).willReturn(game);
        given(logicService.getCardAmount(Mockito.any())).willReturn(cardsRemainingDTO);

        // when/then -> do the request + validate the result

        MockHttpServletRequestBuilder postRequest = get("/games/{gameId}/cards/remainder/{playerToken}", "12ddaa3","test");
        // then

        mockMvc.perform(postRequest)
                .andExpect(status().isBadRequest());

    }

    @Test
    public void getCardAmountFailsBecausePlayerOrGameNotFound() throws Exception {
        given(validationService.checkPlayerIsPartOfGame(Mockito.anyString(),Mockito.anyLong())).willThrow(NotFoundException.class);


        // when/then -> do the request + validate the result

        MockHttpServletRequestBuilder postRequest = get("/games/{gameId}/cards/remainder/{playerToken}", "123","test");
        // then

        mockMvc.perform(postRequest)
                .andExpect(status().isNotFound());

    }

    @Test
    public void getCardAmountFailsBecausePlayerNotPartOfGame() throws Exception {
        given(validationService.checkPlayerIsPartOfGame(Mockito.anyString(),Mockito.anyLong())).willThrow(UnauthorizedException.class);
        // when/then -> do the request + validate the result

        MockHttpServletRequestBuilder postRequest = get("/games/{gameId}/cards/remainder/{playerToken}", "123","test");
        // then

        mockMvc.perform(postRequest)
                .andExpect(status().isUnauthorized());

    }

    /**Tests if the game phase can correctly be retrieved*/
    @Test
    public void GETPhaseWorks() throws Exception {
        given(logicService.getGamePhase(anyLong())).willReturn(State.ChooseMysteryWord);
        // when/then -> do the request + validate the result

        MockHttpServletRequestBuilder postRequest = get("/games/{gameId}/phases", "1");
        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk());

    }

}

