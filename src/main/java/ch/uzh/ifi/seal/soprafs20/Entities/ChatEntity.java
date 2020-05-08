package ch.uzh.ifi.seal.soprafs20.Entities;

import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.chat.ChatMessage;
import ch.uzh.ifi.seal.soprafs20.repository.ChatRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ChatGetDTO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ChatEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    protected Long id;

    @ElementCollection
    List<ChatMessage> chat= new ArrayList<>();

    @OneToOne
    GameSetUpEntity gameSetUpEntity;

    public void addMessage(String playerName, String message){
        ChatMessage chatMessage=new ChatMessage();
        chatMessage.initChatMessage(playerName, message, System.currentTimeMillis());
        chat.add(chatMessage);
    }

    public List<ChatMessage> getChatMessages(){
        return chat;
    }
}
