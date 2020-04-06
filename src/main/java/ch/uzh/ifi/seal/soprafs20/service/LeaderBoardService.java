package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.LeaderBoard;
import ch.uzh.ifi.seal.soprafs20.repository.LeaderBoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class LeaderBoardService {

    @Autowired
    private LeaderBoardRepository leaderBoardRepository;

    public List<LeaderBoard> getAllLeaderBoardEntries() {
        List<LeaderBoard> leaderBoardEntries = new ArrayList<>();
        leaderBoardRepository.findAll().forEach(leaderBoardEntries::add);
        return leaderBoardEntries;
    }

    public List<LeaderBoard> getSortedList() {
        List<LeaderBoard> leaderBoardEntries = new ArrayList<>();
        leaderBoardRepository.findAll().forEach(leaderBoardEntries::add);
        leaderBoardEntries.sort(Collections.reverseOrder());
        return leaderBoardEntries;
    }

    public void addLeaderBoard(LeaderBoard leaderBoard) {
        leaderBoardRepository.save(leaderBoard);
    }

    public Optional<LeaderBoard> getLeaderBoard(Long playerId){
        return leaderBoardRepository.findById(playerId);
    }

    public void updateLeaderBoard (LeaderBoard leaderBoard, int upd){
        leaderBoard.setScore(leaderBoard.getScore()+upd);
        leaderBoardRepository.save(leaderBoard);
    }

    public void deleteLeaderBoardEntry(LeaderBoard leaderBoard){
        leaderBoardRepository.delete(leaderBoard);
    }

    public void deleteLeaderBoard(){
        leaderBoardRepository.deleteAll();
    }

}