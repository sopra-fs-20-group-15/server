package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceIntegrationTestGetCardAmount extends TestSETUPLogicService {

    @Test
    public void getCardAmountWorksOnStart() {
        assertEquals(13,logicService.getCardAmount(createdActiveGame.getId()).getCardsOnStack());
    }

    @Transactional
    @Test
    public void getCardAmountWorksAfterChange() {
        assertEquals(13,logicService.getCardAmount(createdActiveGame.getId()).getCardsOnStack());
        createdActiveGame= logicService.drawCardFromStack(createdActiveGame);
        assertEquals(12,logicService.getCardAmount(createdActiveGame.getId()).getCardsOnStack());
    }
}