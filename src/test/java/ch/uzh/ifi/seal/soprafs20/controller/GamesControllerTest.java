//package ch.uzh.ifi.seal.soprafs20.controller;
//
//import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
//import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
//import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
//import ch.uzh.ifi.seal.soprafs20.GameLogic.CardService;
//import ch.uzh.ifi.seal.soprafs20.GameLogic.GameService;
//import ch.uzh.ifi.seal.soprafs20.GameLogic.ValidationService;
//import ch.uzh.ifi.seal.soprafs20.exceptions.*;
//import ch.uzh.ifi.seal.soprafs20.rest.dto.CardPostDTO;
//import ch.uzh.ifi.seal.soprafs20.rest.dto.GamePostDTO;
//import ch.uzh.ifi.seal.soprafs20.rest.dto.WordPostDTO;
//import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//
//import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;
//import static org.hamcrest.Matchers.*;
//import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
///**
// * UserControllerTest
// * This is a WebMvcTest which allows to test the UserController i.e. GET/POST request without actually sending them over the network.
// * This tests if the UserController works.
// */
//
//@WebMvcTest(GamesController.class)
//public class GamesControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private GameService gameService;
//
//    @MockBean
//    private PlayerService playerService;
//
//    /**
//     * Tests a post-Request to /games/
//     */
//    /**works: three players*/
//    @Test
//    public void POSTGamesCreateGameWorksWithThreePlayers() throws Exception {
//
//        // given
//        //a game
//        GameEntity game = new GameEntity();
//        game.setNumberOfPlayers(3L);
//        game.setNumberOfBots(0L);
//        game.setGameType(PRIVATE);
//        game.setPassword("Cara");
//        game.setHostId(1L);
//        //a player
//        PlayerEntity player = new PlayerEntity();
//        player.setId(1L);
//
//        //from Client through DTO
//        GamePostDTO gamePostDTO = new GamePostDTO();
//        gamePostDTO.setNumberOfPlayers(3L);
//        gamePostDTO.setNumberOfBots(0L);
//        gamePostDTO.setGameType(PRIVATE);
//        gamePostDTO.setPassword("Cara");
//
//        // mock the functions
//        given(playerService.getPlayerByToken(Mockito.any())).willReturn(player);
//        given(gameService.createGame(Mockito.any())).willReturn(game);
//        // when
//        MockHttpServletRequestBuilder postRequest = post("/games")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(gamePostDTO));
//        // then
//        mockMvc.perform(postRequest).andExpect(status().isCreated())
//                .andExpect(jsonPath("$.numberOfPlayers", is(3)))
//                .andExpect(jsonPath("$.numberOfBots", is(0)))
//                .andExpect(jsonPath("$.gameType", is("PRIVATE")))
//                .andExpect(jsonPath("$.password", is(gamePostDTO.getPassword())));
//
//    }
//
//    /**
//     * Tests a get-Request to /games/{gameId}/activeWord/{playerToken}/
//     */
//    @Test
//    public void getRequestPassivePlayerGetWordSuccessful() throws Exception {
//        // given
//        CardPostDTO cardPostDTO = new CardPostDTO();
//        cardPostDTO.setPlayerToken("df");
//        cardPostDTO.setWordId(1L);
//        //Game
//        GameEntity game = new GameEntity();
//        game.setActiveWord("Eis");
//        //returns
//        WordPostDTO wordPostDTO = new WordPostDTO();
//        wordPostDTO.setWord("Eis");
//    }
//
//    /**
//     * Helper Method to convert userPostDTO into a JSON string such that the input can be processed
//     * <p>
//     * Input will look like this: {"name": "Test User", "username": "testUsername"}
//     *
//     * @param object
//     * @return string
//     */
//
//    private String asJsonString(final Object object) {
//
//        try {
//            return new ObjectMapper().writeValueAsString(object);
//        }
//
//        catch (JsonProcessingException e) {
//            throw new SopraServiceException(String.format("The request body could not be created.%s", e.toString()));
//        }
//    }
//}
///**
// public void createUser_invalidInput_userExistsAlready() throws Exception {
//
// // given
//
// User user = new User();
//
// user.setId(1L);
//
// user.setName("Test User");
//
// user.setUsername("testUsername");
//
// user.setPassword("1234");
//
// user.setDate(1582974000903L);
//
// user.setToken("1");
//
// user.setStatus(UserStatus.ONLINE);
//
//
//
// UserPostDTO userPostDTO = new UserPostDTO();
//
// userPostDTO.setName("Test User");
//
// userPostDTO.setUsername("testUsername");
//
//
//
// String exceptionMessage = "The name provided is not unique. Therefore, the user could not be created!";
//
//
//
// when(userService.createUser(Mockito.any())).thenThrow(new ConflictException(exceptionMessage));
//
//
//
// // when/then -> do the request + validate the result
//
// MockHttpServletRequestBuilder postRequest = post("/users")
//
// .contentType(MediaType.APPLICATION_JSON)
//
// .content(asJsonString(userPostDTO));
//
//
//
// // then It should throw the right message
//
// mockMvc.perform(postRequest)
//
// .andExpect(status().isConflict());
//
// }*/