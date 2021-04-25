package com.game.service;

import com.game.entity.Player;
import com.game.exceptions.IdNotFoundException;
import com.game.exceptions.IdNotValidException;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class PlayersService {

    private final PlayerRepository repository;

    @Autowired
    public PlayersService(PlayerRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void delete(long id) {
        validateId(id);
        repository.deleteById(id);
    }

    @Transactional
    public Player get(long id) {
        validateId(id);
        return repository.findById(id).get();
    }

    private void validateId(long id) {
        if (!repository.existsById(id)) {
            throw new IdNotFoundException();
        }
        if (id < 1) {
            throw new IdNotValidException();
        }
    }

    public List<Player> getPlayers(Map<String, String> params) {
        if(params.isEmpty()) {

        }
        return null;
    }
}
