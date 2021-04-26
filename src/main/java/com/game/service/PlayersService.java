package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exceptions.IdNotFoundException;
import com.game.exceptions.IdNotValidException;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PlayersService {

    private final PlayerRepository repository;
    private final EntityManager entityManager;

    @Autowired
    public PlayersService(PlayerRepository repository, LocalContainerEntityManagerFactoryBean entityManager) {
        this.repository = repository;
        this.entityManager = entityManager.getObject().createEntityManager();
    }

    @Transactional
    public void delete(long id) {
        validateId(id);
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
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

    @Transactional
    public List<Player> getPlayers(Map<String, String> params) {
        CriteriaQuery<Player> playerCriteriaQuery = getPlayerCriteriaQuery(params);

        int pageNumber = Integer.parseInt(params.getOrDefault("pageNumber", "0"));
        int pageSize = Integer.parseInt(params.getOrDefault("pageSize", "3"));
        int firstResult = pageSize * pageNumber;
        int maxResult = firstResult + pageSize;

        return entityManager.createQuery(playerCriteriaQuery).setFirstResult(firstResult).setMaxResults(maxResult).getResultList();
    }

    private CriteriaQuery<Player> getPlayerCriteriaQuery(Map<String, String> params) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Player> playerCriteriaQuery = criteriaBuilder.createQuery(Player.class);
        Root<Player> playerRoot = playerCriteriaQuery.from(Player.class);
        playerCriteriaQuery.select(playerRoot);

        List<Predicate> predicates = getPredicates(params, criteriaBuilder, playerRoot);

        String order = PlayerOrder.valueOf(params.getOrDefault("order", "ID")).getFieldName();

        playerCriteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                .orderBy(criteriaBuilder.asc(playerRoot.get(order)));
        return playerCriteriaQuery;
    }

    private List<Predicate> getPredicates(Map<String, String> params, CriteriaBuilder criteriaBuilder, Root<Player> playerRoot) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.and(criteriaBuilder.like(playerRoot.get("name"), "%" + params.getOrDefault("name", "") + "%"),
                criteriaBuilder.like(playerRoot.get("title"), "%" + params.getOrDefault("title", "") + "%")));
        if (params.get("race") != null) {
            predicates.add(criteriaBuilder.equal(playerRoot.get("race"), Race.valueOf(params.get("race"))));
        }
        if (params.get("profession") != null) {
            predicates.add(criteriaBuilder.equal(playerRoot.get("profession"), Profession.valueOf(params.get("profession"))));
        }
        if (params.get("after") != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(playerRoot.get("birthday"), Long.valueOf(params.get("after"))));
        }
        if (params.get("before") != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(playerRoot.get("birthday"), Long.valueOf(params.get("before"))));
        }
        if (params.get("banned") != null) {
            predicates.add(criteriaBuilder.equal(playerRoot.get("banned"), Boolean.valueOf(params.get("banned"))));
        }
        if (params.get("minExperience") != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(playerRoot.get("experience"), Integer.valueOf(params.get("minExperience"))));
        }
        if (params.get("maxExperience") != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(playerRoot.get("experience"), Integer.valueOf(params.get("maxExperience"))));
        }
        if (params.get("minLevel") != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(playerRoot.get("level"), Integer.valueOf(params.get("minLevel"))));
        }
        if (params.get("maxLevel") != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(playerRoot.get("level"), Integer.valueOf(params.get("maxLevel"))));
        }
        return predicates;
    }

    @Transactional
    public int getPlayersCount(Map<String, String> params) {
        CriteriaQuery<Player> playerCriteriaQuery = getPlayerCriteriaQuery(params);
        return entityManager.createQuery(playerCriteriaQuery).getResultList().size();
    }
}
