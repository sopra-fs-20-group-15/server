package ch.uzh.ifi.seal.soprafs20.service.StatesForLogicService;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class LSSateChooseMysteryWordUnitTestChooseAutomatically {

    @MockBean
    LSStateChooseMysteryWord lsStateChooseMysteryWord;


}
