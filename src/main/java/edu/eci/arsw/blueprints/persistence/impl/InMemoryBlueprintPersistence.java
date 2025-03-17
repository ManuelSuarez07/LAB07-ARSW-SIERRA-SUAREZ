package edu.eci.arsw.blueprints.persistence.impl;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.BlueprintsPersistence;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public final class InMemoryBlueprintPersistence implements BlueprintsPersistence {
    private final Map<String, Set<Blueprint>> blueprints = new HashMap<>();

    @Override
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        Set<Blueprint> authorBlueprints = blueprints.get(bp.getAuthor());
        if (authorBlueprints != null) {
            for (Blueprint existingBp : authorBlueprints) {
                if (existingBp.getName().equals(bp.getName())) {
                    throw new BlueprintPersistenceException("El blueprint '" + bp.getName() + "' ya existe.");
                }
            }
        }
        blueprints.computeIfAbsent(bp.getAuthor(), k -> new HashSet<>()).add(bp);
    }

    @Override
    public Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException {
        return blueprints.getOrDefault(author, Collections.emptySet())
                .stream()
                .filter(bp -> bp.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new BlueprintNotFoundException("Blueprint no encontrado"));
    }

    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        if (!blueprints.containsKey(author)) {
            throw new BlueprintNotFoundException("No existen blueprints para el autor: " + author);
        }
        return blueprints.get(author);
    }

    @Override
    public Set<Blueprint> getAllBlueprints() {
        Set<Blueprint> allBlueprints = new HashSet<>();
        blueprints.values().forEach(allBlueprints::addAll);
        return allBlueprints;
    }

    @Override
    public void updateBlueprint(String author, String bpname, Blueprint blueprint) throws BlueprintNotFoundException {
        Set<Blueprint> authorBlueprints = blueprints.get(author);
        if (authorBlueprints != null) {
            Blueprint existingBp = authorBlueprints.stream()
                    .filter(bp -> bp.getName().equals(bpname))
                    .findFirst()
                    .orElseThrow(() -> new BlueprintNotFoundException("Blueprint no encontrado"));
            existingBp.setPoints(blueprint.getPoints());
        } else {
            throw new BlueprintNotFoundException("No existen blueprints para el autor: " + author);
        }
    }

    @Override
    public void deleteBlueprint(String author, String bpname) throws BlueprintNotFoundException {
        Set<Blueprint> authorBlueprints = blueprints.get(author);
        if (authorBlueprints != null) {
            boolean removed = authorBlueprints.removeIf(bp -> bp.getName().equals(bpname));
            if (!removed) {
                throw new BlueprintNotFoundException("Blueprint no encontrado");
            }
        } else {
            throw new BlueprintNotFoundException("No existen blueprints para el autor: " + author);
        }
    }
}