package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.rest.dto.StatisticsGetDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class orderStatisticsGetDTOListUnitTest {
    @Autowired
    LogicService logicService;
    List<StatisticsGetDTO> list;
    StatisticsGetDTO s1;
    StatisticsGetDTO s2;
    StatisticsGetDTO s3;
    StatisticsGetDTO s4;
    StatisticsGetDTO s5;

    @BeforeEach
    public void setup(){
        s1=new StatisticsGetDTO();
        s2=new StatisticsGetDTO();
        s3=new StatisticsGetDTO();
        s4=new StatisticsGetDTO();
        s5=new StatisticsGetDTO();

        s1.setScore(5);
        s2.setScore(4);
        s3.setScore(3);
        s4.setScore(2);
        s5.setScore(1);

        s1.setPlayerName("1");
        s2.setPlayerName("2");
        s3.setPlayerName("3");
        s4.setPlayerName("4");
        s5.setPlayerName("5");

        list = new ArrayList<>();
        list.add(s3);
        list.add(s1);
        list.add(s4);
        list.add(s5);
        list.add(s2);
    }

    @Test
    public void orderStatisticsGetDTOWorksWithPositiveScores(){
        list=logicService.orderStatisticsGetDTOList(list);
        assertEquals(list.get(0).getPlayerName(),s1.getPlayerName());
        assertEquals(list.get(1).getPlayerName(),s2.getPlayerName());
        assertEquals(list.get(2).getPlayerName(),s3.getPlayerName());
        assertEquals(list.get(3).getPlayerName(),s4.getPlayerName());
        assertEquals(list.get(4).getPlayerName(),s5.getPlayerName());
    }

    @Test
    public void orderStatisticsGetDTOWorksWithNegativeScores(){
        s1.setScore(-1);
        s2.setScore(-2);
        s3.setScore(-3);
        s4.setScore(-4);
        s5.setScore(-5);

        list=logicService.orderStatisticsGetDTOList(list);
        assertEquals(list.get(0).getPlayerName(),s1.getPlayerName());
        assertEquals(list.get(1).getPlayerName(),s2.getPlayerName());
        assertEquals(list.get(2).getPlayerName(),s3.getPlayerName());
        assertEquals(list.get(3).getPlayerName(),s4.getPlayerName());
        assertEquals(list.get(4).getPlayerName(),s5.getPlayerName());
    }

    @Test
    public void orderStatisticsGetDTOListWorksWithPositiveAndNegativeNumbers(){
        s1.setScore(15);
        s2.setScore(7);
        s3.setScore(0);
        s4.setScore(-1);
        s5.setScore(-5);

        list=logicService.orderStatisticsGetDTOList(list);
        assertEquals(list.get(0).getPlayerName(),s1.getPlayerName());
        assertEquals(list.get(1).getPlayerName(),s2.getPlayerName());
        assertEquals(list.get(2).getPlayerName(),s3.getPlayerName());
        assertEquals(list.get(3).getPlayerName(),s4.getPlayerName());
        assertEquals(list.get(4).getPlayerName(),s5.getPlayerName());
    }

    @Test
    public void orderStatisticsGetDTOListWorksWithDuplicateScores(){
        s1.setScore(15);
        s2.setScore(15);
        s3.setScore(0);
        s4.setScore(-1);
        s5.setScore(-5);

        list=logicService.orderStatisticsGetDTOList(list);
        assertEquals(list.get(0).getPlayerName(),s1.getPlayerName());
        assertEquals(list.get(1).getPlayerName(),s2.getPlayerName());
        assertEquals(list.get(2).getPlayerName(),s3.getPlayerName());
        assertEquals(list.get(3).getPlayerName(),s4.getPlayerName());
        assertEquals(list.get(4).getPlayerName(),s5.getPlayerName());
    }
}
