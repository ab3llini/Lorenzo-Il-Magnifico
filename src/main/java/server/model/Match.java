package server.model;

import server.model.board.Board;
import server.model.board.TowerSlot;
import server.model.card.developement.BuildingDvptCard;
import server.model.card.developement.DvptCard;
import server.model.card.developement.TerritoryDvptCard;

import java.util.ArrayList;
import java.util.Random;

import static java.util.Collections.shuffle;


/**
 * Created by Federico on 15/05/2017.
 */

public class Match {
    private Board board;
    private ArrayList<Player> players;
    private ArrayList<Player> playersOrder;
    private Integer era;
    private Integer round;
    private MatchSettings matchSettings;
    private final int dvptCardOffset = 8;

    public void PrepareBoard (){
        public void prepareTowers (){
            Random random = new Random;
            Integer randomCardId;
            ArrayList<TowerSlot> territoryTowerFirstEra = new ArrayList<TowerSlot>();
            int i=0;
            TowerSlot temporary = new TowerSlot;
            for(; i<dvptCardOffset; i++){
                temporary.setDvptCard(GameSingleton.getDvptCard(i));
                territoryTowerFirstEra.add(temporary);
            }
            ArrayList<TowerSlot> territoryTowerSecondEra = new ArrayList<TowerSlot>();
            for(; i<2*dvptCardOffset; i++){
                temporary.setDvptCard(GameSingleton.getDvptCard(i));
                territoryTowerSecondEra.add(temporary);
            }
            ArrayList<TowerSlot> territoryTowerThirdEra = new ArrayList<TowerSlot>();
            for(; i<3*dvptCardOffset; i++){
                temporary.setDvptCard(GameSingleton.getDvptCard(i));
                territoryTowerThirdEra.add(temporary);
            }
            ArrayList<TowerSlot> buildingTowerFirstEra = new ArrayList<TowerSlot>();
            for(; i<4*dvptCardOffset; i++){
                temporary.setDvptCard(GameSingleton.getDvptCard(i));
                buildingTowerFirstEra.add(temporary);
            }
            ArrayList<TowerSlot> buildingTowerSecondEra = new ArrayList<TowerSlot>();
            for(; i<5*dvptCardOffset; i++){
                temporary.setDvptCard(GameSingleton.getDvptCard(i));
                buildingTowerSecondEra.add(temporary);
            }
            ArrayList<TowerSlot> buildingTowerThirdEra = new ArrayList<TowerSlot>();
            for(; i<6*dvptCardOffset; i++){
                temporary.setDvptCard(GameSingleton.getDvptCard(i));
                buildingTowerThirdEra.add(temporary);
            }
            ArrayList<TowerSlot> characterTowerFirstEra = new ArrayList<TowerSlot>();
            for(; i<7*dvptCardOffset; i++){
                temporary.setDvptCard(GameSingleton.getDvptCard(i));
                characterTowerFirstEra.add(temporary);
            }
            ArrayList<TowerSlot> characterTowerSecondEra = new ArrayList<TowerSlot>();
            for(; i<8*dvptCardOffset; i++){
                temporary.setDvptCard(GameSingleton.getDvptCard(i));
                characterTowerSecondEra.add(temporary);
            }
            ArrayList<TowerSlot> characterTowerThirdEra = new ArrayList<TowerSlot>();
            for(; i<9*dvptCardOffset; i++){
                temporary.setDvptCard(GameSingleton.getDvptCard(i));
                characterTowerThirdEra.add(temporary);
            }
            ArrayList<TowerSlot> ventureTowerFirstEra = new ArrayList<TowerSlot>();
            for(; i<10*dvptCardOffset; i++){
                temporary.setDvptCard(GameSingleton.getDvptCard(i));
                ventureTowerFirstEra.add(temporary);
            }
            ArrayList<TowerSlot> ventureTowerSecondEra = new ArrayList<TowerSlot>();
            for(; i<11*dvptCardOffset; i++){
                temporary.setDvptCard(GameSingleton.getDvptCard(i));
                ventureTowerSecondEra.add(temporary);
            }
            ArrayList<TowerSlot> ventureTowerThirdEra = new ArrayList<TowerSlot>();
            for(; i<12*dvptCardOffset; i++){
                temporary.setDvptCard(GameSingleton.getDvptCard(i));
                ventureTowerThirdEra.add(temporary);
            }
            ArrayList<TowerSlot> TerritoryTower = new ArrayList<TowerSlot>;
            shuffle(territoryTowerFirstEra);
            shuffle(territoryTowerSecondEra);
            shuffle(territoryTowerThirdEra);
            for(i=0; i<4; i++){
                temporary = territoryTowerFirstEra.remove(0);
                TerritoryTower.add(temporary);
            }
            for(i=0; i<4; i++){
                temporary = territoryTowerFirstEra.remove(0);
                TerritoryTower.add(temporary);
            }
            for(i=0; i<4; i++){
                temporary = territoryTowerFirstEra.remove(0);
                TerritoryTower.add(temporary);
            }
            shuffle(territoryTowerFirstEra);
            shuffle(territoryTowerSecondEra);
            shuffle(territoryTowerThirdEra);
            for(i=0; i<4; i++){
                temporary = territoryTowerFirstEra.remove(0);
                TerritoryTower.add(temporary);
            }
            for(i=0; i<4; i++){
                temporary = territoryTowerFirstEra.remove(0);
                TerritoryTower.add(temporary);
            }
            for(i=0; i<4; i++){
                temporary = territoryTowerFirstEra.remove(0);
                TerritoryTower.add(temporary);
            }




    }
}
