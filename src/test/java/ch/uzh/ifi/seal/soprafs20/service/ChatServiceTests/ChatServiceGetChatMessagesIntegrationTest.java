package ch.uzh.ifi.seal.soprafs20.service.ChatServiceTests;

import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.constant.GameType;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.repository.ChatRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ChatGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ChatPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GamePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.ChatService;
import ch.uzh.ifi.seal.soprafs20.service.GameSetUpService;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class ChatServiceGetChatMessagesIntegrationTest {
    @Qualifier("chatRepository")
    @Autowired
    private ChatRepository chatRepository;
    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private GameSetUpRepository gameSetUpRepository;
    @Autowired
    private ChatService chatService;
    @Autowired
    private GameSetUpService gameSetUpService;

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    private PlayerEntity one;
    private PlayerEntity two;
    private GameSetUpEntity gameSetUpEntity;

    @BeforeTransaction
    public void clean(){
        gameRepository.deleteAll();
        gameSetUpRepository.deleteAll();
        chatRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @BeforeEach
    public void setUp(){
        PlayerEntity p1=new PlayerEntity();
        p1.setStatus(PlayerStatus.ONLINE);
        p1.setUsername("1");
        p1.setPassword("p1");
        one=playerService.createUser(p1);

        PlayerEntity p2=new PlayerEntity();
        p2.setStatus(PlayerStatus.ONLINE);
        p2.setUsername("2");
        p2.setPassword("p2");
        two=playerService.createUser(p2);

        GamePostDTO gamePostDTO=new GamePostDTO();
        gamePostDTO.setGameName("name");
        gamePostDTO.setGameType(GameType.PUBLIC);
        gamePostDTO.setNumberOfAngles(0L);
        gamePostDTO.setNumberOfDevils(0L);
        gamePostDTO.setNumberOfPlayers(4L);
        gamePostDTO.setPlayerToken(one.getToken());

        gameSetUpEntity=DTOMapper.INSTANCE.convertGameSetUpPostDTOtoEntity(gamePostDTO);
        gameSetUpEntity.setHostName("1");

        gameSetUpEntity=gameSetUpService.createGame(gameSetUpEntity);
        List<String> list=new ArrayList<>();
        list.add(one.getToken());
        list.add(two.getToken());
        gameSetUpEntity.setPlayerTokens(list);
        gameSetUpRepository.save(gameSetUpEntity);
    }

    @Test
    public void getChatMessagesWorksWithZeroMessagesSend(){
        List<ChatGetDTO> list=chatService.getChatMessages(gameSetUpEntity.getId(),one.getToken());

        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    public void getChatMessagesWorksWithOneMessageSend(){
        ChatPostDTO chatPostDTO=new ChatPostDTO();
        chatPostDTO.setGameId(gameSetUpEntity.getId());
        chatPostDTO.setPlayerToken(one.getToken());
        chatPostDTO.setMessage("test");
        chatService.addChatMessage(chatPostDTO);

        List<ChatGetDTO> list=chatService.getChatMessages(gameSetUpEntity.getId(),one.getToken());

        assertNotNull(list);
        assertEquals(1,list.size());
        assertEquals("test",list.get(0).getMessage());
        assertEquals(one.getUsername(),list.get(0).getPlayerName());
        assertNotNull(list.get(0).getTime());
    }

    @Test
    public void getChatMessagesWorksWithMultipleMessagesSend(){
        ChatPostDTO chatPostDTO=new ChatPostDTO();
        chatPostDTO.setGameId(gameSetUpEntity.getId());
        chatPostDTO.setPlayerToken(one.getToken());
        chatPostDTO.setMessage("test");
        chatService.addChatMessage(chatPostDTO);
        chatPostDTO.setPlayerToken(two.getToken());
        chatService.addChatMessage(chatPostDTO);


        List<ChatGetDTO> list=chatService.getChatMessages(gameSetUpEntity.getId(),one.getToken());

        assertNotNull(list);
        assertEquals(2,list.size());
        assertEquals("test",list.get(0).getMessage());
        assertEquals(one.getUsername(),list.get(0).getPlayerName());
        assertNotNull(list.get(0).getTime());
        assertEquals("test",list.get(1).getMessage());
        assertEquals(two.getUsername(),list.get(1).getPlayerName());
        assertNotNull(list.get(1).getTime());
    }

    @Test
    public void getChatMessageFailsBecauseGameDoesNotExist(){
        assertThrows(NotFoundException.class,()-> chatService.getChatMessages(7L,one.getToken()));
    }
}
