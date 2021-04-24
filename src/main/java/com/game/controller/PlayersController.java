package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/players")
public class PlayersController {
    @Autowired
    PlayersService service;

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<Player> getPlayersList(@RequestParam Map<String, String> params) {
        return null;
    }

    @GetMapping("/count")
    @ResponseStatus(HttpStatus.OK)
    public Integer getPlayersCount(@RequestParam Map<String, String> params) {
        return null;
    }

    @PostMapping("/")
    public Player createPlayer(@RequestBody Map<String, String> params) {
        return null;
    }

    @GetMapping("/{id}")
    public Player getPlayer(@PathVariable Integer id) {
        return null;
    }

    @PostMapping("/{id}")
    public Player updatePlayer(@PathVariable Integer id, @RequestBody Map<String, String> params) {
        return null;
    }

    @DeleteMapping("/{id}")
    public void deletePlayer(@PathVariable Integer id) {
    }

//    @GetMapping("/rest/players/{id}")
//    public ResponseEntity<Player> getPlayer(@PathVariable Integer id) {
//        return new ResponseEntity<>(service.getPlayer(id), HttpStatus.OK);
//    }
}
