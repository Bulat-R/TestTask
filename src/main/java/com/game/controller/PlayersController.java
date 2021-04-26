package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/players")
public class PlayersController {
    private final PlayersService service;

    @Autowired
    public PlayersController(PlayersService service) {
        this.service = service;
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<Player> getPlayersList(@RequestParam Map<String, String> params) {
        return service.getPlayers(params);
    }

    @GetMapping("/count")
    @ResponseStatus(HttpStatus.OK)
    public int getPlayersCount(@RequestParam Map<String, String> params) {
        return service.getPlayersCount(params);
    }

    @PostMapping()
    public Player createPlayer(@ModelAttribute Player player) {
        return null;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Player getPlayer(@PathVariable long id) {
        return service.get(id);
    }

    @PostMapping("/{id}")
    public Player updatePlayer(@PathVariable int id, @ModelAttribute Player player) {
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePlayer(@PathVariable long id) {
        service.delete(id);
    }
}
