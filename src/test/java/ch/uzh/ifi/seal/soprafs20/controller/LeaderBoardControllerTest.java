package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.exceptions.SopraServiceException;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LeaderBoardGetDTO;
import ch.uzh.ifi.seal.soprafs20.service.LeaderBoardService;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(LeaderBoardController.class)
public class LeaderBoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeaderBoardService leaderBoardService;

    @MockBean
    private PlayerService playerService;

    @MockBean
    PlayerRepository playerRepository;

    @Test
    public void getLeaderBoardWorksWithNoPlayers() throws Exception {
        List<LeaderBoardGetDTO> leaderBoardGetDTOS= new ArrayList<>();


        // given
        when(leaderBoardService.getLeaderBoard()).thenReturn(leaderBoardGetDTOS);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = get("/leaderBoards");

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));
    }

    @Test
    public void getLeaderBoardWorksWithPlayers() throws Exception {
        List<LeaderBoardGetDTO> leaderBoardGetDTOS= new ArrayList<>();
        LeaderBoardGetDTO leaderBoardGetDTO= new LeaderBoardGetDTO();
        leaderBoardGetDTO.setPlayerName("Bilbo");
        leaderBoardGetDTO.setScore(50000);
        leaderBoardGetDTO.setRank(1);

        leaderBoardGetDTOS.add(leaderBoardGetDTO);

        // given
        when(leaderBoardService.getLeaderBoard()).thenReturn(leaderBoardGetDTOS);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = get("/leaderBoards");

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].playerName", is("Bilbo")))
                .andExpect(jsonPath("$[0].rank", is(1)))
                .andExpect(jsonPath("$[0].score", is(50000)));
    }

    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
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
