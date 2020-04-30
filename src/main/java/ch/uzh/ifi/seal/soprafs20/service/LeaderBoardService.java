package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LeaderBoardGetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class LeaderBoardService {

    private final PlayerRepository playerRepository;

    @Autowired
    public LeaderBoardService(@Qualifier("playerRepository") PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;

    }

    public List<LeaderBoardGetDTO> getLeaderBoard(){
        List<PlayerEntity> list = this.playerRepository.findAll();
        list.sort(Collections.reverseOrder());
        List<LeaderBoardGetDTO> listLB = new ArrayList<>();
        int displayRank=1;
        int actualRank=1;
        for (PlayerEntity player: list) {
            LeaderBoardGetDTO leaderBoardGetDTO = new LeaderBoardGetDTO();
            leaderBoardGetDTO.setPlayerName(player.getUsername());
            leaderBoardGetDTO.setScore(player.getLeaderBoardScore());
            leaderBoardGetDTO.setGamesPlayed(player.getGamesPlayed());
            if (actualRank == 1) leaderBoardGetDTO.setRank(1);
            else {
                if (listLB.get(actualRank-2).getScore()==player.getLeaderBoardScore()) leaderBoardGetDTO.setRank(displayRank);
                else {
                    displayRank=actualRank;
                    leaderBoardGetDTO.setRank(displayRank);
                }
            }
            listLB.add(leaderBoardGetDTO);
            actualRank++;
        }
        return listLB;
    }
}
