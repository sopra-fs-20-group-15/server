package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.ChatEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.chat.ChatMessage;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.ChatRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ChatGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ChatPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service("chatService")
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;
    private final PlayerService playerService;
    private final GameSetUpRepository gameSetUpRepository;

    @Autowired
    ChatService(@Qualifier("chatRepository") ChatRepository chatRepository, PlayerService playerService, GameSetUpRepository gameSetUpRepository){
        this.chatRepository=chatRepository;
        this.gameSetUpRepository=gameSetUpRepository;
        this.playerService=playerService;
    }

    public ChatEntity createChat(){
        ChatEntity chat=new ChatEntity();
        chat=chatRepository.save(chat);
        return chat;
    }

    public GameSetUpEntity checkForGameSetup(Long id){
        Optional<GameSetUpEntity> optional=gameSetUpRepository.findById(id);
        if (optional.isEmpty()) throw new NotFoundException("No game with specified id exists.");
        return optional.get();
    }

    /**adds chat message
     * @Param: ChatPostDTO: String playerToken, String message, Long gameId
     * @Returns: void
     * @Throws: 401: The player is not part of the lobby
     * @Throws: 404: No game with this gameId
     * @Throws: 409: empty message
     * */
    public void addChatMessage(ChatPostDTO chatPostDTO){
        if (chatPostDTO.getMessage().isBlank()) throw new ConflictException("Empty messages are not allowed!");
        String playerName=playerService.getPlayerByToken(chatPostDTO.getPlayerToken()).getUsername();
        GameSetUpEntity game= checkForGameSetup(chatPostDTO.getGameId());
        if (!game.getPlayerTokens().contains(chatPostDTO.getPlayerToken())) throw new UnauthorizedException("You are not part of the lobby and trying to access the chat!");
        ChatEntity chatEntity =game.getChat();
        chatEntity.addMessage(playerName,chatPostDTO.getMessage());
    }

    /**Allows to send chat message
     * @Param: Long gameId, String playerToken
     * @Returns: List<ChatGetDTO>: String playerName, String message, Long time
     * @Throws: 401: The player is not part of the lobby
     * @Throws: 404: No game with this gameId
     * * */
    public List<ChatGetDTO> getChatMessages(Long gameId, String playerToken){
        GameSetUpEntity game=checkForGameSetup(gameId);
        if (!game.getPlayerTokens().contains(playerToken)) throw new UnauthorizedException("You are not part of the lobby and trying to access the chat!");
        ChatEntity chatEntity =game.getChat();
        List<ChatGetDTO> list=new ArrayList<>();
        for (ChatMessage chatMessage: chatEntity.getChatMessages()) {
            list.add(DTOMapper.INSTANCE.convertChatMessageToChatGetDTO(chatMessage));
        }
        return list;
    }

}
