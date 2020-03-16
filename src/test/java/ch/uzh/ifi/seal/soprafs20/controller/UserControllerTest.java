package ch.uzh.ifi.seal.soprafs20.controller;



import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.*;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserPostDTO;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
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



import java.util.Collections;
import java.util.List;



import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



/**

 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST request without actually sending them over the network.
 * This tests if the UserController works.

 */

@WebMvcTest(UserController.class)

public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    /**Tests a get-Request to /games*/

    //Valid game
    @Test
    public void POSTgamesCreateGameWorks() throws Exception {

        // given
        Game game = new Game();

        //from Client through DTO
        GamesPostDTO gamesPostDTO = new GamesPostDTO();
        gamesPostDTO.setNumberOfPlayers(3L);
        gamesPostDTO.setNumberOfBots(0L);

        // mock the main Function
        given(gameService.createGame()).willReturn(game);
        // when
        MockHttpServletRequestBuilder postRequest = post("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamesPostDTO));
        // then
        mockMvc.perform(postRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].numberOfPlayers", is(3)))
                .andExpect(jsonPath("$[0].numberOfBots", is(0)));

    }

    //Invalid game
    @Test
    public void POSTgamesCreateGameFails() throws Exception {

        // given
        Game game = new Game();

        //from Client through DTO
        GamesPostDTO gamesPostDTO = new GamesPostDTO();
        gamesPostDTO.setNumberOfPlayers(8L);
        gamesPostDTO.setNumberOfBots(0L);

        // mock the main Function
        String exceptionMessage = "Too many players!";
        when(gameService.createGame(Mockito.any())).thenThrow(new ConflictException(exceptionMessage));
        // when
        MockHttpServletRequestBuilder postRequest = post("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamesPostDTO));
        // then
        mockMvc.perform(postRequest).andExpect(status().isConflict())

    }

    /**Tests a Put-Request to /games/{gameId}*/

    //A player joins a game successfully
    @Test
    public void PUTgamesGameIdJoinSuccessfull() throws Exception {

        // given
        Game game = new Game();
        List<Player> playerList = new LinkedList<Player>();
        playerList.add(new Player());
        game.setPlayerList(playerList);

        // mock the main Function
        given(gameService.createGame()).willReturn(game);
        // when
        MockHttpServletRequestBuilder putRequest = put("/games/{gameId}")
                .contentType(MediaType.APPLICATION_JSON));
        // then
        mockMvc.perform(putRequest).andExpect(status().isNoContent())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].numberOfPlayers", is(1)))
                .andExpect(jsonPath("$[0].numberOfBots", is(0)));

    }

    //A player joins cannot join the game because he or she does not have the permission to do so, p. ex. because the game is already full or has already started
    @Test
    public void PUTgamesGameIdJoinConflictGameAlreadyFull() throws Exception {

        // given
        Game game = new Game();
        List<Player> playerList = new LinkedList<Player>();
        playerList.add(new Player());
        game.setPlayerList(playerList);

        // mock the main Function
        String exceptionMessage = "The games already full!";
        when(gameService.joinGame(Mockito.any())).thenThrow(new ConflictException(exceptionMessage));
        // when
        MockHttpServletRequestBuilder putRequest = put("/games/{gameId}")
                .contentType(MediaType.APPLICATION_JSON);
        // then
        mockMvc.perform(putRequest).andExpect(status().isConflict());


    }

    //Error because gameId was not found
    @Test
    public void PUTgamesGameIdJoinGameIdNotFound() throws Exception {

        // given
        Game game = new Game();
        List<Player> playerList = new LinkedList<Player>();
        playerList.add(new Player());
        game.setPlayerList(playerList);

        // mock the main Function
        String exceptionMessage = "This gameId does not exist";
        when(gameService.joinGame(Mockito.any())).thenThrow(new NotFoundException(exceptionMessage));
        // when
        MockHttpServletRequestBuilder putRequest = put("/games/{gameId}")
                .contentType(MediaType.APPLICATION_JSON);
        // then
        mockMvc.perform(putRequest).andExpect(status().isNotFound());


    }

    /** Test /games/{gameId}/clues Post*/
    //Valid Post
    @Test
    public void POSTgamesGameIdCluesValidClue() throws Exception {

        // given
        Game game = new Game();

        //from Client through DTO
        CluesDTO cluesPostDTO = new CluesPutDTO();
        cluesPostDTO.setClue("cat");

        // mock the main Function
        given(gameService.evaluateClue()).willReturn(true);
        // when
        MockHttpServletRequestBuilder postRequest = post("/games/{gameId}/clues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(cluesPostDTO));
        // then
        mockMvc.perform(postRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].isValid", is(true)));

    }
    //Unauthorized Post
    @Test
    public void POSTgamesGameIdCluesInvalidClueFromPlayerThatIsNotPartOfGame() throws Exception {

        // given
        Game game = new Game();

        //from Client through DTO
        CluesDTO cluesPostDTO = new CluesPutDTO();
        cluesPostDTO.setClue("cat");

        // mock the main Function
        String exceptionMessage = "This gameId does not exist";
        when(gameService.evaluateClue(Mockito.any())).thenThrow(new UnauthorizedException(exceptionMessage));

        // when
        MockHttpServletRequestBuilder postRequest = post("/games/{gameId}/clues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(cluesPostDTO));
        // then
        mockMvc.perform(postRequest).andExpect(status().isUnauthorized());


    }
    //Conflict Post
    @Test
    public void POSTgamesGameIdCluesInvalidClueFromActivePlayer() throws Exception {

        // given
        Game game = new Game();

        //from Client through DTO
        CluesDTO cluesPostDTO = new CluesPutDTO();
        cluesPostDTO.setClue("cat");

        // mock the main Function
        String exceptionMessage = "This gameId does not exist";
        when(gameService.evaluateClue(Mockito.any())).thenThrow(new ConflictException(exceptionMessage));

        // when
        MockHttpServletRequestBuilder postRequest = post("/games/{gameId}/clues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(cluesPostDTO));
        // then
        mockMvc.perform(postRequest).andExpect(status().isConflict());


    }

    /** Test /games/{gameId}/guesses Post*/
    //Valid Post
    @Test
   public void POSTgamesGameIdGuessesValidGuess() throws Exception {

        // given
        Game game = new Game();

        //from Client through DTO
        GuessDTO guessPostDTO = new GuessPutDTO();
        guessPostDTO.setGuess("cat");

        // mock the main Function
        given(gameService.evaluateGuess()).willReturn(true);
        // when
        MockHttpServletRequestBuilder postRequest = post("/games/{gameId}/guesses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(guessPostDTO));
        // then
        mockMvc.perform(postRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].isValid", is(true)));

    }

    //Unauthorized Guess
    @Test
    public void POSTgamesGameIdGuessesUnauthorizedGuess() throws Exception {

        // given
        Game game = new Game();

        //from Client through DTO
        GuessDTO guessPostDTO = new GuessPutDTO();
        guessPostDTO.setGuess("cat");

        // mock the main Function
        String exceptionMessage = "You are not part of this game!";
        when(gameService.evaluateClue(Mockito.any())).thenThrow(new UnauthorizedException(exceptionMessage));
        // when
        MockHttpServletRequestBuilder postRequest = post("/games/{gameId}/guesses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(guessPostDTO));
        // then
        mockMvc.perform(postRequest).andExpect(status().isUnauthorized());

    }
    //Conflict Guess
    @Test
    public void POSTgamesGameIdGuessesConflictGuess() throws Exception {

        // given
        Game game = new Game();

        //from Client through DTO
        GuessDTO guessPostDTO = new GuessPutDTO();
        guessPostDTO.setGuess("cat");

        // mock the main Function
        String exceptionMessage = "It is not your turn to guess!";
        when(gameService.evaluateClue(Mockito.any())).thenThrow(new ConflictException(exceptionMessage));
        // when
        MockHttpServletRequestBuilder postRequest = post("/games/{gameId}/guesses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(guessPostDTO));
        // then
        mockMvc.perform(postRequest).andExpect(status().isConflict());

    }
    /** Test /games/{gameId}/delete DELETE*/
    //Valid Delete
    @Test
    public void DELETEgamesGameIdDeleteValidDelete() throws Exception {

        // mock the main Function
        given(gameService.deleteGame()).willReturn(null);
        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/games/{gameId}/delete")
                .contentType(MediaType.APPLICATION_JSON);
        // then
        mockMvc.perform(deleteRequest).andExpect(status().isOk();

    }

    //Game to delete not found
    @Test
    public void DELETEgamesGameIdDeleteGameNotFound() throws Exception {

        // mock the main Function
        String exceptionMessage = "You cannot delete a game that has not finished yet!";
        when(gameService.deleteGame(Mockito.any())).thenThrow(new NotFoundException(exceptionMessage));

        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/games/{gameId}/delete")
                .contentType(MediaType.APPLICATION_JSON);
        // then
        mockMvc.perform(deleteRequest).andExpect(status().isNotFound();

    }
    //Conflict since game is still running
    @Test
    public void DELETEgamesGameIdDeleteGameNotFinishedYet() throws Exception {

        // mock the main Function
        String exceptionMessage = "You cannot delete a game that has not finished yet!";
        when(gameService.deleteGame(Mockito.any())).thenThrow(new ConflictException(exceptionMessage));

        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/games/{gameId}/delete")
                .contentType(MediaType.APPLICATION_JSON);
        // then
        mockMvc.perform(deleteRequest).andExpect(status().isConflict();

    }

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new SopraServiceException(String.format("The request body could not be created.%s", e.toString()));
        }
    }

}