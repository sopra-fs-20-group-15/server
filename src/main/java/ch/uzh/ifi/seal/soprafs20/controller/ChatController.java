package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.rest.dto.ChatGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ChatPostDTO;
import ch.uzh.ifi.seal.soprafs20.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ChatController {


    private final ChatService chatService;


    ChatController(ChatService chatService) {
        this.chatService=chatService;
    }

    /**Allows to send chat message
     * @Param: ChatPostDTO: String playerToken, String message, Long gameId
     * @Returns: @Returns: void
     * @Throws: 401: The player is not part of the lobby
     * @Throws: 404: No game with this gameId
     * @Throws: 409: empty message
     * * */
    @PostMapping("/gameSetUps/chatMessages")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void addChatMessage(@RequestBody ChatPostDTO chatPostDTO) {
        chatService.addChatMessage(chatPostDTO);
    }

    /**Gets all chat messages
     * @Param: Long gameId (since path variable), String playerToken
     * @Returns: List<ChatGetDTO>: String playerName, String message, Long time
     * @Throws: 401: The player is not part of the lobby
     * @Throws: 404: No game with this gameId
     * @Throws: 400: gameSetupId wrong format
     * * */
    @GetMapping("/gameSetUps/{gameSetUpId}/chatMessages/{playerToken}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ChatGetDTO> getChatMessages(@PathVariable Long gameSetUpId, @PathVariable String playerToken) {
        return chatService.getChatMessages(gameSetUpId, playerToken);
    }
}
