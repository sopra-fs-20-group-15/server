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

@WebMvcTest(ActiveGamesController.class)
public class ActiveGameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActiveGameService gameService;

    @MockBean
    private PlayerService playerService;

    @MockBean
    private LogicService logicService;


    @Test
    public void POSTActiveGameCreation() throws Exception {
        // given
        //a game
        ActiveGamePostDTO activeGamePostDTO = new ActiveGamePostDTO();
        activeGamePostDTO.setId(1L);
        PlayerTokenDTO playerTokenDTO=new PlayerTokenDTO();
        playerTokenDTO.setToken("Test");


        // mock the functions
        given(gameService.getGameSetupById(Mockito.any())).willReturn(new GameSetUpEntity());
        given(gameService.createActiveGame(Mockito.any(), Mockito.any())).willReturn(activeGamePostDTO);
        given(logicService.initializeTurn(Mockito.any())).willReturn(new GameEntity());

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
        given(gameService.getGameInformationById(Mockito.any())).willReturn(gameGetDTO);

        // when
        MockHttpServletRequestBuilder getRequest = get("/activeGames/{gameId}", 1);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
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
