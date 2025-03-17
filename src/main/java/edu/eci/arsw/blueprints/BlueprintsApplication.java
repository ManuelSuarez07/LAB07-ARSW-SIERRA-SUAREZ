package edu.eci.arsw.blueprints;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.services.BlueprintsServices;

@SpringBootApplication
public class BlueprintsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlueprintsApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(BlueprintsServices blueprintsServices) {
        return args -> {
            Point[] housePoints = {
                // Techo
                new Point(150, 150),
                new Point(250, 150),
                new Point(200, 100),
                new Point(150, 150),
                // Base de la casa
                new Point(150, 150),
                new Point(150, 250),
                new Point(250, 250),
                new Point(250, 150),
                new Point(150, 150),
                // Puerta
                new Point(175, 250),
                new Point(175, 200),
                new Point(225, 200),
                new Point(225, 250)
            };
            Point[] buildingPoints = {
                // Estructura principal del edificio
                new Point(150, 100), new Point(250, 100),
                new Point(250, 300), new Point(150, 300),
                new Point(150, 100),
                // Piso 1
                new Point(160, 120), new Point(190, 120),
                new Point(190, 140), new Point(160, 140),
                new Point(160, 120),
                // Piso 2
                new Point(160, 160), new Point(190, 160),
                new Point(190, 180), new Point(160, 180),
                new Point(160, 160),
                // Piso 3
                new Point(160, 200), new Point(190, 200),
                new Point(190, 220), new Point(160, 220),
                new Point(160, 200),
                // Piso 4
                new Point(160, 240), new Point(190, 240),
                new Point(190, 260), new Point(160, 260),
                new Point(160, 240),
                // Puerta en el primer piso
                new Point(210, 280), new Point(230, 280),
                new Point(230, 300), new Point(210, 300),
                new Point(210, 280)
            };

            Blueprint TowerBlueprint = new Blueprint("Manuel", "Torre", buildingPoints);
            Blueprint houseBlueprint = new Blueprint("Yeka", "Casa", housePoints);
            Blueprint bp1 = new Blueprint("Manuel", "Plano1", new Point[]{new Point(10, 10), new Point(10, 10), new Point(20, 20), new Point(20, 20), new Point(30, 30)});
            Blueprint bp2 = new Blueprint("Yeka", "Plano2", new Point[]{new Point(30, 30), new Point(40, 40)});

            blueprintsServices.addNewBlueprint(bp1);
            blueprintsServices.addNewBlueprint(bp2);
            blueprintsServices.addNewBlueprint(TowerBlueprint);
            blueprintsServices.addNewBlueprint(houseBlueprint);

            System.out.println("Después de agregar: " + blueprintsServices.getAllBlueprints());

            // Consultar planos
            System.out.println("Todos los planos: " + blueprintsServices.getAllBlueprints());
            System.out.println("Planos de Manuel: " + blueprintsServices.getBlueprintsByAuthor("Manuel"));
            System.out.println("Plano específico: " + blueprintsServices.getBlueprint("Manuel", "Plano1"));
        };
    }
}
