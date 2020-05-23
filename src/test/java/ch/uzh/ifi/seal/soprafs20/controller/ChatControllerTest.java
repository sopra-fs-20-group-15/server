package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.SopraServiceException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ChatGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ChatPostDTO;
import ch.uzh.ifi.seal.soprafs20.service.ActiveGameService;
import ch.uzh.ifi.seal.soprafs20.service.ChatService;
import ch.uzh.ifi.seal.soprafs20.service.GameSetUpService;
import ch.uzh.ifi.seal.soprafs20.service.LogicService;
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

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * ChatControllerTest
 * Structure of all tests:
 * The tests always have the same structure:
 * 1. Preparations for the test, e. g. creation of a player etc.
 * 2. mocking of all the functions that will be called in that endpoint
 * 3. Request
 * 4. assertions that should hold
 */
@WebMvcTest(ChatController.class)
public class ChatControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    /**Tests Post-Request to /gameSetUps/chatMessages" :addChatMessage*/
    @Test
    public void addMessageWorks() throws Exception {
        ChatPostDTO chatPostDTO=new ChatPostDTO();
        chatPostDTO.setPlayerToken("token");
        chatPostDTO.setMessage("message");
        chatPostDTO.setGameId(0L);

        // mock the functions
        doNothing().when(chatService).addChatMessage(chatPostDTO);

        // when
        MockHttpServletRequestBuilder postRequest = post("/gameSetUps/chatMessages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(chatPostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isCreated());
    }

    @Test
    public void addMessageFailsBecauseOfEmptyMessage() throws Exception {
        ChatPostDTO chatPostDTO=new ChatPostDTO();
        chatPostDTO.setPlayerToken("token");
        chatPostDTO.setMessage("message");
        chatPostDTO.setGameId(0L);

        // mock the functions
        Mockito.doThrow(new ConflictException("LaLiLuLeLo")).when(chatService).addChatMessage(Mockito.any());
        // when
        MockHttpServletRequestBuilder postRequest = post("/gameSetUps/chatMessages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(chatPostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isConflict());
    }

    @Test
    public void addMessageFailsBecauseLobbyDoesNotExist() throws Exception {
        ChatPostDTO chatPostDTO=new ChatPostDTO();
        chatPostDTO.setPlayerToken("token");
        chatPostDTO.setMessage("message");
        chatPostDTO.setGameId(0L);

        // mock the functions
        Mockito.doThrow(new NotFoundException("LaLiLuLeLo")).when(chatService).addChatMessage(Mockito.any());
        // when
        MockHttpServletRequestBuilder postRequest = post("/gameSetUps/chatMessages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(chatPostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isNotFound());
    }

    @Test
    public void addMessageFailsBecausePlayerNotPartOfLobby() throws Exception {
        ChatPostDTO chatPostDTO=new ChatPostDTO();
        chatPostDTO.setPlayerToken("token");
        chatPostDTO.setMessage("message");
        chatPostDTO.setGameId(0L);

        // mock the functions
        Mockito.doThrow(new UnauthorizedException("LaLiLuLeLo")).when(chatService).addChatMessage(Mockito.any());
        // when
        MockHttpServletRequestBuilder postRequest = post("/gameSetUps/chatMessages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(chatPostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isUnauthorized());
    }

    /**Tests Get-Request to /gameSetUps/{gameSetUpId}/chatMessages/{playerToken}" :getChatMessages*/
    @Test
    public void getChatMessagesWorksWhenNoMessagesSendYet() throws Exception {
        List<ChatGetDTO> list=new ArrayList<>();

        // mock the functions
        given(chatService.getChatMessages(Mockito.anyLong(),Mockito.anyString())).willReturn(list);

        // when
        MockHttpServletRequestBuilder postRequest = get("/gameSetUps/{gameSetUpId}/chatMessages/{playerToken}", 1L, "sdfsdfsd");

        // then
        mockMvc.perform(postRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", is(empty())));
    }

    @Test
    public void getChatMessagesWorksWithMessages() throws Exception {
        List<ChatGetDTO> list=new ArrayList<>();
        ChatGetDTO chatGetDTO=new ChatGetDTO();
        chatGetDTO.setMessage("1");
        chatGetDTO.setPlayerName("1");
        chatGetDTO.setTime(1L);
        list.add(chatGetDTO);
        chatGetDTO=new ChatGetDTO();
        chatGetDTO.setMessage("2");
        chatGetDTO.setPlayerName("2");
        chatGetDTO.setTime(2L);
        list.add(chatGetDTO);

        // mock the functions
        given(chatService.getChatMessages(Mockito.anyLong(),Mockito.anyString())).willReturn(list);

        // when
        MockHttpServletRequestBuilder postRequest = get("/gameSetUps/{gameSetUpId}/chatMessages/{playerToken}", 1L, "sdfsdfsd");

        // then
        mockMvc.perform(postRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].playerName", is("1")))
                .andExpect(jsonPath("$[0].message", is("1")))
                .andExpect(jsonPath("$[0].time", is(1)))
                .andExpect(jsonPath("$[1].playerName", is("2")))
                .andExpect(jsonPath("$[1].message", is("2")))
                .andExpect(jsonPath("$[1].time", is(2)));
    }

    @Test
    public void getChatMessagesFailsBecauseWrongIdFormat() throws Exception {
        // when
        MockHttpServletRequestBuilder postRequest = get("/gameSetUps/{gameSetUpId}/chatMessages/{playerToken}", "sdf24", "sdfsdfsd");

        // then
        mockMvc.perform(postRequest).andExpect(status().isBadRequest());
    }

    @Test
    public void getChatMessagesFailsBecauseGameDoesNotExists() throws Exception {

        // mock the functions
        given(chatService.getChatMessages(Mockito.anyLong(),Mockito.anyString())).willThrow(NotFoundException.class);

        // when
        MockHttpServletRequestBuilder postRequest = get("/gameSetUps/{gameSetUpId}/chatMessages/{playerToken}", 1L, "sdfsdfsd");

        // then
        mockMvc.perform(postRequest).andExpect(status().isNotFound());
    }

    @Test
    public void getChatMessagesFailsBecausePlayerIsNotPartOfLobby() throws Exception {

        // mock the functions
        given(chatService.getChatMessages(Mockito.anyLong(),Mockito.anyString())).willThrow(UnauthorizedException.class);

        // when
        MockHttpServletRequestBuilder postRequest = get("/gameSetUps/{gameSetUpId}/chatMessages/{playerToken}", 1L, "sdfsdfsd");

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


}
