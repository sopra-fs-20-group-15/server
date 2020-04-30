package ch.uzh.ifi.seal.soprafs20.service.ActiveGameServiceTest;

import ch.uzh.ifi.seal.soprafs20.Helper.TestSETUPCreatesActiveGame;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationDeleteActiveGameTest extends TestSETUPCreatesActiveGame {

    @Test
    public void SuccessfullyDeletesGame() {
        createdActiveGame.setHasEnded(true);

        gameService.deleteActiveGame(createdActiveGame.getId());

        assertThrows(NotFoundException.class, ()-> gameService.getGameById(createdActiveGame.getId()));
    }


    @Test
    public void CannotDeleteSinceGameHasNotEndedYet() {

        assertThrows(ConflictException.class, ()->  gameService.deleteActiveGame(createdActiveGame.getId()));
    }
}
