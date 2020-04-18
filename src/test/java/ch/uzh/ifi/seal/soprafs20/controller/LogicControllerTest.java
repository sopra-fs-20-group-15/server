
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
import org.mockito.MockitoAnnotations;
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
    private GameService gameService;

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
    public void passivePlayerCannotGiveClueBecauseTurnOrderViolated() throws Exception {
        CluePostDTO clue=new CluePostDTO();
        clue.setClue("clue");
        clue.setPlayerToken("token");
        GameEntity game = new GameEntity();
        Map<String, String> clueList= new HashMap<>();
        List<PlayerEntity> players= new ArrayList<>();
        game.setClueMap(clueList);
        game.setPlayers(players);
        game.setActiveMysteryWord("");

        PlayerEntity player = new PlayerEntity();
        player.setUsername("playerName");
        game.getPlayers().add(player);
        game.getPlayers().add(new PlayerEntity());

        given(validationService.checkPlayerIsPassivePlayerOfGame(Mockito.anyString(),Mockito.anyLong())).willReturn(true);
        given(gameService.getGameById(Mockito.any())).willReturn(game);
        given(playerService.getPlayerByToken(Mockito.anyString())).willReturn(player);

        // when/then -> do the request + validate the result

        MockHttpServletRequestBuilder postRequest = post("/games/{gameId}/clues/", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(clue));

        // then

        mockMvc.perform(postRequest)
                .andExpect(status().isConflict());

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
        game.setValidCluesAreSet(false);


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
                .andExpect(status().isBadRequest());

    }

    /**Test the a Request to /games/{gameId}/ends/{playerToken} in order to see if the game has already ended*/
    @Test
    public void GetHasGameEndedWorks() throws Exception {
        //given
        GameEndedDTO gameEndedDTO = new GameEndedDTO();
        gameEndedDTO.setHasGameEnded(false);


        given(validationService.checkPlayerIsPartOfGame(Mockito.anyString(),Mockito.anyLong())).willReturn(true);
        given(logicService.hasGameEnded(Mockito.any())).willReturn(false);



        // when/then -> do the request + validate the result

        MockHttpServletRequestBuilder getRequest = get("/games/{gameId}/ends/{playerToken}", "123","test");
        // then

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasGameEnded", is(gameEndedDTO.getHasGameEnded())));

    }



}
/**
 public void createUser_invalidInput_userExistsAlready() throws Exception {

 // given

 User user = new User();

 user.setId(1L);

 user.setName("Test User");

 user.setUsername("testUsername");

 user.setPassword("1234");

 user.setDate(1582974000903L);

 user.setToken("1");

 user.setStatus(UserStatus.ONLINE);



 UserPostDTO userPostDTO = new UserPostDTO();

 userPostDTO.setName("Test User");

 userPostDTO.setUsername("testUsername");



 String exceptionMessage = "The name provided is not unique. Therefore, the user could not be created!";



 when(userService.createUser(Mockito.any())).thenThrow(new ConflictException(exceptionMessage));



 // when/then -> do the request + validate the result

 MockHttpServletRequestBuilder postRequest = post("/users")

 .contentType(MediaType.APPLICATION_JSON)

 .content(asJsonString(userPostDTO));



 // then It should throw the right message

 mockMvc.perform(postRequest)

 .andExpect(status().isConflict());

 }*/
