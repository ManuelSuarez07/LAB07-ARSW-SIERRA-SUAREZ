package edu.eci.arsw.blueprints.services;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.BlueprintsPersistence;
import edu.eci.arsw.blueprints.Filter.BlueprintFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class BlueprintsServices {

    @Autowired
    @Qualifier("inMemoryBlueprintPersistence")
    BlueprintsPersistence bpp;

    @Autowired
    @Qualifier("redundancyFilter")
    BlueprintFilter blueprintFilter;

    public void addNewBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        bpp.saveBlueprint(bp);
    }

    public Set<Blueprint> getAllBlueprints() {
        Set<Blueprint> blueprints = bpp.getAllBlueprints();
        blueprints.forEach(blueprintFilter::filter);
        return blueprints;
    }

    public Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException {
        Blueprint blueprint = bpp.getBlueprint(author, name);
        blueprintFilter.filter(blueprint);
        return blueprint;
    }

    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        Set<Blueprint> blueprints = bpp.getBlueprintsByAuthor(author);
        blueprints.forEach(blueprintFilter::filter);
        return blueprints;
    }

    public void updateBlueprint(String author, String bpname, Blueprint blueprint) throws BlueprintNotFoundException {
        bpp.updateBlueprint(author, bpname, blueprint);
    }

    public void deleteBlueprint(String author, String bpname) throws BlueprintNotFoundException {
        bpp.deleteBlueprint(author, bpname);
    }

    public Blueprint addPointToBlueprint(String author, String bpname, Point point) throws BlueprintNotFoundException {
        Blueprint blueprint = bpp.getBlueprint(author, bpname);
        blueprint.addPoint(point);
        return blueprint;
    }
}