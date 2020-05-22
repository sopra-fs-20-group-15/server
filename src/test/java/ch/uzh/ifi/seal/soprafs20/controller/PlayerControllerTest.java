package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.exceptions.*;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.PlayerTokenDTO;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
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
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PlayerController.class)
public class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerService playerService;

    @MockBean
    PlayerRepository playerRepository;

    @Test
    public void givenPlayers_whenGetPlayers_thenReturnJsonArray() throws Exception {
        // given
        PlayerEntity player = new PlayerEntity();
        player.setUsername("firstname@lastname");
        player.setPassword("password");
        player.setStatus(PlayerStatus.OFFLINE);

        List<PlayerEntity> allUsers = Collections.singletonList(player);

        // this mocks the PlayerService -> we define above what the userService should return when getUsers() is called
        given(playerService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/players").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(player.getUsername())))
                .andExpect(jsonPath("$[0].status", is(player.getStatus().toString())));
    }

    @Test
    public void createUser_validInput_playerCreated() throws Exception {
        // given
        PlayerEntity player = new PlayerEntity();
        player.setId(1L);
        player.setUsername("testUsername");
        player.setUsername("password");
        player.setToken("1");
        player.setStatus(PlayerStatus.OFFLINE);


        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setUsername("testUsername");
        playerPostDTO.setUsername("testPassword");

        given(playerService.createUser(Mockito.any())).willReturn(player);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(player.getId().intValue())))
                .andExpect(jsonPath("$.username", is(player.getUsername())))
                .andExpect(jsonPath("$.status", is(player.getStatus().toString())));}

    @Test
    public void createPlayer_duplicateUsername_playerCreated() throws Exception {
        // given
        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setUsername("testUsername");
        playerPostDTO.setPassword("testPassword");

        given(playerService.createUser(Mockito.any())).willThrow(UsernameAlreadyExists.class);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isConflict());
    }

    @Test
    public void createPlayer_InvalidInput() throws Exception {
        // given
        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setUsername("testUsername");
        playerPostDTO.setPassword("testPassword");

        given(playerService.createUser(Mockito.any())).willThrow(IllegalRegistrationInput.class);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void login_validCredentials() throws Exception {
        // given
        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setUsername("testUsername");
        playerPostDTO.setPassword("testPassword");

        PlayerEntity player= new PlayerEntity();
        player.setId(1L);
        player.setToken("test");

        given(playerService.loginUser(Mockito.any())).willReturn(player);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = put("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("token", is(player.getToken())))
                .andExpect(jsonPath("id", is(player.getId().intValue())));
    }

    @Test
    public void login_validCredentials_but_alreadyLoggedIn() throws Exception {
        // given
        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setUsername("testUsername");
        playerPostDTO.setPassword("testPassword");


        given(playerService.loginUser(Mockito.any())).willThrow(new NoContentException(""));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = put("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    public void login_invalidCredentials() throws Exception {
        // given
        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setUsername("testUsername");
        playerPostDTO.setPassword("testPassword");

        given(playerService.loginUser(Mockito.any())).willThrow(UnauthorizedException.class);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = put("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void logout_valid_Token() throws Exception {
        // given
        PlayerTokenDTO playerTokenDTO = new PlayerTokenDTO();
        playerTokenDTO.setToken("testUsername");
        playerTokenDTO.setId(123L);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = put("/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerTokenDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void logout_invalid_Token() throws Exception {
        // given
        PlayerTokenDTO playerTokenDTO = new PlayerTokenDTO();
        playerTokenDTO.setToken("testUsername");
        playerTokenDTO.setId(123L);

        doThrow(new NotFoundException("No user with same token as yours exists.")).when(playerService).logOutUser(Mockito.any());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = put("/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerTokenDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void logout_userAlreadyLoggedOut() throws Exception {
        // given
        PlayerTokenDTO playerTokenDTO = new PlayerTokenDTO();
        playerTokenDTO.setToken("testUsername");
        playerTokenDTO.setId(123L);

        doThrow(new NoContentException("")).when(playerService).logOutUser(Mockito.any());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = put("/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerTokenDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    public void getUser_UserIdExists() throws Exception {
        // given
        PlayerEntity player= new PlayerEntity();

        player.setUsername("test");
        player.setId(1L);
        player.setStatus(PlayerStatus.ONLINE);

        given(playerService.getPlayerById(Mockito.anyLong())).willReturn(player);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = get("/players/{userId}", 1);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(player.getId().intValue())))
                .andExpect(jsonPath("username", is(player.getUsername())))
                .andExpect(jsonPath("status", is(player.getStatus().toString())));
    }

    @Test
    public void getPlayer_IdDoesNotExists() throws Exception {
        // given
        given(playerService.getPlayerById(Mockito.anyLong())).willThrow(new NotFoundException("No User with this id available!"));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = get("/players/{userId}", 1);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isNotFound());
    }

    /**Tests a Get-Request to /players/tokens/{playerToken} */
    @Test
    public void aPlayerWithGivenTokenExists() throws Exception {
        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/players/tokens/{playerToken}", "token");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk());
    }

    /**Tests a Get-Request to /players/tokens/{playerToken} */
    @Test
    public void noPlayerWithGivenTokenExists() throws Exception {
        // given
        Mockito.doThrow(PlayerNotAvailable.class).when(playerService).checkIfPlayerExistsByToken(Mockito.anyString());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/players/tokens/{playerToken}", "token");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
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