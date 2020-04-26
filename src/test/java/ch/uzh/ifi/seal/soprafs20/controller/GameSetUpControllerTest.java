package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.service.*;
import ch.uzh.ifi.seal.soprafs20.exceptions.*;
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

import java.util.ArrayList;
import java.util.List;

import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST request without actually sending them over the network.
 * This tests if the UserController works.
 */

@WebMvcTest(GameSetUpController.class)
public class GameSetUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameSetUpService gameService;

    @MockBean
    private PlayerService playerService;

    @MockBean
    private CardService cardService;

    @MockBean
    private ValidationService validationService;

    @MockBean
    private LogicService logicService;

    /**
     * Tests a post-Request to /games/
     */
    /**works: three players*/
    @Test
    public void POSTGamesCreateGameWorksWithThreePlayers() throws Exception {

        // given
        //a game
        GameSetUpEntity game = new GameSetUpEntity();
        game.setNumberOfPlayers(3L);
        game.setNumberOfAngles(0L);
        game.setNumberOfDevils(0L);
        game.setGameType(PRIVATE);
        game.setPassword("Cara");
        game.setHostName("Peter");
        game.setId(1L);
        //a player
        PlayerEntity player = new PlayerEntity();
        player.setId(1L);

        //from Client through DTO
        GamePostDTO gamePostDTO = new GamePostDTO();
        gamePostDTO.setNumberOfPlayers(3L);
        gamePostDTO.setNumberOfAngles(0L);
        gamePostDTO.setNumberOfDevils(0L);
        gamePostDTO.setGameType(PRIVATE);
        gamePostDTO.setPassword("Cara");

        // mock the functions
        given(playerService.getPlayerByToken(Mockito.any())).willReturn(player);
        given(gameService.createGame(Mockito.any())).willReturn(game);
        // when
        MockHttpServletRequestBuilder postRequest = post("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePostDTO));
        // then
        mockMvc.perform(postRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$.numberOfPlayers", is(3)))
                .andExpect(jsonPath("$.numberOfAngles", is(0)))
                .andExpect(jsonPath("$.gameType", is("PRIVATE")))
                .andExpect(jsonPath("$.gameId", is(1)));

    }

    /**
     * Tests a Get-Request to /games/lobbies/{gameSetupId}/{playerToken}
     */
    @Test
    public void GETLobby() throws Exception {
        LobbyGetDTO lobbyGetDTO = new LobbyGetDTO();
        lobbyGetDTO.setHostName("Frederick");
        List<String> playerNames= new ArrayList<>();
        playerNames.add("Frederick");
        playerNames.add("Khan");
        lobbyGetDTO.setPlayerNames(playerNames);
        lobbyGetDTO.setNumOfDesiredPlayers(5L);
        lobbyGetDTO.setNumOfHumanPlayers(2L);
        lobbyGetDTO.setNumOfAngels(0L);
        lobbyGetDTO.setNumOfDevils(0L);
        lobbyGetDTO.setGameSetUpId(22L);
        lobbyGetDTO.setGameName("game1");

        // mock the functions
        given(gameService.getLobbyInfo(Mockito.any(),Mockito.anyString())).willReturn(lobbyGetDTO);

        // when
        MockHttpServletRequestBuilder postRequest = get("/games/lobbies/{gameSetupId}/{playerToken}", 123, "hatooooooken");

        // then
        mockMvc.perform(postRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.numOfDesiredPlayers", is(5)))
                .andExpect(jsonPath("$.numOfHumanPlayers", is(2)))
                .andExpect(jsonPath("$.numOfAngels", is(0)))
                .andExpect(jsonPath("$.numOfDevils", is(0)))
                .andExpect(jsonPath("$.gameSetUpId", is(22)))
                .andExpect(jsonPath("$.gameName", is("game1")))
                .andExpect(jsonPath("$.hostName", is("Frederick")))
                .andExpect(jsonPath("$.playerNames", contains("Frederick","Khan")));
    }

    @Test
    public void GETLobbyFailsBecauseIdHasWrongFormat() throws Exception {
        // when
        MockHttpServletRequestBuilder postRequest = get("/games/lobbies/{gameSetupId}/{playerToken}",
                "asdfa fdsf", "tik-token");

        // then
        mockMvc.perform(postRequest).andExpect(status().isConflict());
    }

    @Test
    public void GETLobbyFailsBecausePlayerTokenOrGameSetUpIdDoesNotExist() throws Exception {
        given(gameService.getLobbyInfo(Mockito.any(),Mockito.anyString())).willThrow(new NotFoundException("Test"));

        // when
        MockHttpServletRequestBuilder postRequest = get("/games/lobbies/{gameSetupId}/{playerToken}",
                "123", "tik-token");

        // then
        mockMvc.perform(postRequest).andExpect(status().isNotFound());
    }

    @Test
    public void GETLobbyFailsBecausePlayerNotInLobby() throws Exception {
        given(gameService.getLobbyInfo(Mockito.any(),Mockito.anyString())).willThrow(new UnauthorizedException("Test"));

        // when
        MockHttpServletRequestBuilder postRequest = get("/games/lobbies/{gameSetupId}/{playerToken}",
                "123", "tik-token");

        // then
        mockMvc.perform(postRequest).andExpect(status().isUnauthorized());
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


/**Test put player into game*/
/**Works with valid player and valid password for private game*/
@Test
public void PUTaPlayerIntoPrivateGame() throws Exception {

    // given
    //a player
    PlayerEntity player = new PlayerEntity();
    player.setId(1L);
    player.setToken("F");
    //a game
    GameSetUpEntity game = new GameSetUpEntity();
    game.setNumberOfPlayers(3L);
    game.setNumberOfAngles(0L);
    game.setNumberOfDevils(0L);
    game.setGameType(PRIVATE);
    game.setPassword("Cara");
    game.setHostName("L");
    game.setId(1L);
    List<String> playerTokens = new ArrayList<String>();
    playerTokens.add(player.getToken());
    game.setPlayerTokens(playerTokens);

    //from Client through DTO
    PlayerIntoGameSetUpDTO playerIntoGameSetUpDTO = new PlayerIntoGameSetUpDTO();
    playerIntoGameSetUpDTO.setPlayerToken("A");
    playerIntoGameSetUpDTO.setPassword("Cara");

    // mock the functions
    given(playerService.getPlayerByToken(Mockito.any())).willReturn(player);
    given(gameService.putPlayerIntoGame(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(game);
    // when
    MockHttpServletRequestBuilder putRequest = put("/games/{gameId}/players", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(playerIntoGameSetUpDTO));
    // then
    mockMvc.perform(putRequest).andExpect(status().isOk());
    }



/**Test take player out of game*/
    /**Works with valid player and valid password for private game*/
    @Test
    public void DELETEaPlayerFromGameSetUpIntoOverview() throws Exception {

        // given
        //a player
        PlayerEntity player = new PlayerEntity();
        player.setId(1L);
        player.setToken("F");
        //a game
        GameSetUpEntity game = new GameSetUpEntity();
        game.setNumberOfPlayers(3L);
        game.setNumberOfAngles(0L);
        game.setNumberOfDevils(0L);
        game.setGameType(PRIVATE);
        game.setPassword("Cara");
        game.setHostName("Aba");
        game.setId(1L);
        List<String> playerTokens = new ArrayList<String>();
        playerTokens.add(player.getToken());
        game.setPlayerTokens(playerTokens);

        //from Client through DTO
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setPlayerToken("A");

        // mock the functions
        given(playerService.getPlayerByToken(Mockito.any())).willReturn(player);
        given(gameService.removePlayerFromGame(Mockito.any(), Mockito.any())).willReturn(game);
        // when
        MockHttpServletRequestBuilder putRequest = delete("/games/{gameId}/players", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(tokenDTO));
        // then
        mockMvc.perform(putRequest).andExpect(status().isOk());
    }

    /**Test DeletionOfAGameSetUp*/
    /**Works with valid player and valid password for private game*/
    @Test
    public void DELETEGameSetUpEntity() throws Exception {

        // given
        String playerToken = "ABC";
        PlayerEntity player = new PlayerEntity();
        player.setUsername("Anna");

        TokenDTO tokenDTO =  new TokenDTO();
        tokenDTO.setPlayerToken("A");
        // mock the functions
        given(playerService.getPlayerByToken(Mockito.any())).willReturn(player);
        given(gameService.deleteGameSetUpEntity(Mockito.any(), Mockito.any())).willReturn(true);
        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/gameSetUps/{gameSetUpId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(tokenDTO));
        // then
        mockMvc.perform(deleteRequest).andExpect(status().isOk());
    }


}
