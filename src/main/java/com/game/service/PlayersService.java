package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exceptions.IdNotFoundException;
import com.game.exceptions.IdNotValidException;
import com.game.exceptions.ParametersNotValidException;
import com.game.repository.PlayerRepository;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service
public class PlayersService {

    private final PlayerRepository repository;
    @PersistenceContext
    private final EntityManager entityManager;

    public PlayersService(PlayerRepository repository, LocalContainerEntityManagerFactoryBean entityManager) {
        this.repository = repository;
        this.entityManager = entityManager.getObject().createEntityManager();
    }

    @Transactional
    public List<Player> getPlayers(Map<String, String> params) {
        CriteriaQuery<Player> playerCriteriaQuery = getPlayerCriteriaQuery(params);

        int pageSize = Integer.parseInt(params.getOrDefault("pageSize", "3"));
        int firstResult = pageSize * Integer.parseInt(params.getOrDefault("pageNumber", "0"));

        return entityManager.createQuery(playerCriteriaQuery).setFirstResult(firstResult).setMaxResults(pageSize).getResultList();
    }

    @Transactional
    public int getCount(Map<String, String> params) {
        CriteriaQuery<Player> playerCriteriaQuery = getPlayerCriteriaQuery(params);
        return entityManager.createQuery(playerCriteriaQuery).getResultList().size();
    }

    @Transactional
    public Player create(Player player) {
        validatePlayer(player);
        player.calcLevel();
        return repository.saveAndFlush(player);
    }

    @Transactional(readOnly = true)
    public Player get(long id) {
        validateId(id);
        return repository.findById(id).get();
    }

    @Transactional
    public Player update(int id, Player player) {
        validateId(id);
        Player playerUp = get(id);
        if (player.getName() != null) playerUp.setName(player.getName());
        if (player.getTitle() != null) playerUp.setTitle(player.getTitle());
        if (player.getRace() != null) playerUp.setRace(player.getRace());
        if (player.getProfession() != null) playerUp.setProfession(player.getProfession());
        if (player.getBirthday() != null) playerUp.setBirthday(player.getBirthday());
        if (player.getBanned() != null) playerUp.setBanned(player.getBanned());
        if (player.getExperience() != null) playerUp.setExperience(player.getExperience());
        validatePlayer(playerUp);
        return repository.saveAndFlush(playerUp);
    }


    @Transactional
    public void delete(long id) {
        validateId(id);
        repository.deleteById(id);
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

    private void validateId(long id) {
        if (!repository.existsById(id)) {
            throw new IdNotFoundException();
        }
        if (id < 1) {
            throw new IdNotValidException();
        }
    }


    private void validatePlayer(Player player) {
        if (player.getName() == null || player.getTitle() == null || player.getRace() == null
                || player.getProfession() == null || player.getBirthday() == null || player.getExperience() == null) {
            throw new ParametersNotValidException();
        }
        if (player.getName().isEmpty() || player.getName().length() > 12) {
            throw new ParametersNotValidException();
        }
        if (player.getTitle().length() > 30) {
            throw new ParametersNotValidException();
        }
        if (player.getExperience() < 0 || player.getExperience() > 10_000_000) {
            throw new ParametersNotValidException();
        }
        if (player.getBirthday().getTime() < new GregorianCalendar(2000, Calendar.JANUARY, 1).getTimeInMillis()
                || player.getBirthday().getTime() > new GregorianCalendar(3001, Calendar.JANUARY, 1).getTimeInMillis() - 1) {
            throw new ParametersNotValidException();
        }
    }
}
