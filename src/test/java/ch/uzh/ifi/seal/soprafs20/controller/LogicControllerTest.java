
package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.CardService;
import ch.uzh.ifi.seal.soprafs20.GameLogic.GameService;
import ch.uzh.ifi.seal.soprafs20.GameLogic.ValidationService;
import ch.uzh.ifi.seal.soprafs20.GameLogic.WordComparer;
import ch.uzh.ifi.seal.soprafs20.exceptions.*;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CardPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.WordPostDTO;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Mock
    private WordComparer wordComparer;




    /**Tests a post-Request to /games/{gameId}/Cards/*/
    @Test
    public void postRequestActivePlayerChoosesWordOnCardSuccessfully() throws Exception {
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

        MockHttpServletRequestBuilder postRequest = post("/games/{gameId}/Cards/", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(cardPostDTO));

        // then

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.word", is(wordPostDTO.getWord())));


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
        game.setActiveWord("Eis");
        //returns
        WordPostDTO wordPostDTO = new WordPostDTO();
        wordPostDTO.setWord("Eis");

        /**
         doReturn(true).when(validationService.checkPlayerIsPassivePlayerOfGame(Mockito.any(),Mockito.any()));
         doReturn(game).when(gameService.getGameById(Mockito.any()));*/
        given(validationService.checkPlayerIsPassivePlayerOfGame(Mockito.any(), Mockito.any())).willReturn(true);
        given(gameService.getGameById(Mockito.any())).willReturn(game);

        // when/then -> do the request + validate the result

        MockHttpServletRequestBuilder postRequest = get("/games/{gameId}/activeWord/{playerToken}/", 1, "df")
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
        game.setClueList(clueList);
        game.setPlayers(players);

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
                .andExpect(status().isCreated());

    }

    @Test
    public void postRequestPassivePlayerTriesToGiveTwoClues() throws Exception {
        CluePostDTO clue=new CluePostDTO();
        clue.setClue("clue");
        clue.setPlayerToken("token");
        GameEntity game = new GameEntity();
        Map<String, String> clueList= new HashMap<>();
        List<PlayerEntity> players= new ArrayList<>();
        game.setClueList(clueList);
        game.setPlayers(players);

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
                .andExpect(status().isCreated());

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
        game.setClueList(clueList);
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
        game.setClueList(clueList);
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
