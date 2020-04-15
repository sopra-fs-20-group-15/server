package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.service.CardService;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import ch.uzh.ifi.seal.soprafs20.exceptions.*;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import ch.uzh.ifi.seal.soprafs20.service.ValidationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
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

@WebMvcTest(GamesController.class)
public class GamesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private PlayerService playerService;

    @MockBean
    private CardService cardService;

    @MockBean
    private ValidationService validationService;

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
     * Tests a post-Request to /games/{gameSetupId}
     */
    @Test
    public void POSTActiveGameCreation() throws Exception {

        // given
        //a game
        GameSetUpEntity game = new GameSetUpEntity();
        game.setNumberOfPlayers(3L);
        game.setNumberOfAngles(0L);
        game.setNumberOfDevils(0L);
        game.setGameType(PRIVATE);
        game.setPassword("Cara");
        game.setHostName("Peter");
        PlayerTokenDTO playerTokenDTO=new PlayerTokenDTO();
        playerTokenDTO.setToken("Test");


        // mock the functions
        given(gameService.getGameSetupById(Mockito.any())).willReturn(game);

        // when
        MockHttpServletRequestBuilder postRequest = post("/games/{gameSetupId}", 123)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerTokenDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isCreated());
    }

    @Test
    public void POSTActiveGameCreationFailsBecauseWrongFormatOfGameSetupId() throws Exception {
        MockHttpServletRequestBuilder postRequest = post("/games/{gameSetupId}", "abc");
        mockMvc.perform(postRequest).andExpect(status().isBadRequest());
    }

    @Test
    public void POSTActiveGameCreationFailsBecauseOfMissingBody() throws Exception {
        MockHttpServletRequestBuilder postRequest = post("/games/{gameSetupId}", "123");
        mockMvc.perform(postRequest).andExpect(status().isBadRequest());
    }

    @Test
    public void POSTActiveGameCreationFailsBecauseGameSetupWithSpecifiedIdDoesNotExist() throws Exception {
        given(gameService.createActiveGame(Mockito.any(),Mockito.anyString())).willThrow(new NotFoundException("Test"));
        PlayerTokenDTO playerTokenDTO=new PlayerTokenDTO();
        playerTokenDTO.setToken("Test");

        MockHttpServletRequestBuilder postRequest = post("/games/{gameSetupId}", 123)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerTokenDTO));

        mockMvc.perform(postRequest).andExpect(status().isNotFound());
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
        lobbyGetDTO.setNumOfActualPlayers(2L);
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
                .andExpect(jsonPath("$.numOfActualPlayers", is(2)))
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
        mockMvc.perform(postRequest).andExpect(status().isBadRequest());
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
    public void DELETEaPlayerFromGameSetUp() throws Exception {

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
        PlayerIntoGameSetUpDTO playerIntoGameSetUpDTO = new PlayerIntoGameSetUpDTO();
        playerIntoGameSetUpDTO.setPlayerToken("A");
        playerIntoGameSetUpDTO.setPassword("Cara");

        // mock the functions
        given(playerService.getPlayerByToken(Mockito.any())).willReturn(player);
        given(gameService.removePlayerFromGame(Mockito.any(), Mockito.any())).willReturn(game);
        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/games/{gameId}/players", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerIntoGameSetUpDTO));
        // then
        mockMvc.perform(deleteRequest).andExpect(status().isOk());
    }

    /**Test DeletionOfAGameSetUp*/
    /**Works with valid player and valid password for private game*/
    @Test
    public void DELETEGameSetUpEntity() throws Exception {

        // given
        String playerToken = "ABC";
        PlayerEntity player = new PlayerEntity();
        player.setUsername("Anna");

        // mock the functions
        given(playerService.getPlayerByToken(Mockito.any())).willReturn(player);
        given(gameService.deleteGameSetUpEntity(Mockito.any(), Mockito.any())).willReturn(true);
        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/gameSetUps/{gameSetUpId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerToken));
        // then
        mockMvc.perform(deleteRequest).andExpect(status().isOk());
    }

    /**Test getGameInformationAndItWorks*/
    /**GetRequest at /activeGames/{gameId}*/
    @Test
    public void getGameInformationAndItWorks() throws Exception {
        //given
        GameGetDTO gameGetDTO = new GameGetDTO();
        gameGetDTO.setActivePlayerName("Kirk");
        List<String> names = new ArrayList<String>();
        names.add("Spock");
        names.add("Scotty");
        names.add("Chekov");
        gameGetDTO.setPassivePlayerNames(names);
        names.add("Kirk");
        gameGetDTO.setPlayerNames(names);
        gameGetDTO.setId(1L);

        // mock the functions
        given(gameService.getGameInformationById(1L)).willReturn(gameGetDTO);

        // when
        MockHttpServletRequestBuilder getRequest = get("/activeGames/{gameId}", 123);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)));
    }

}
