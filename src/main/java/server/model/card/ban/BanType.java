package server.model.card.ban;

import java.io.Serializable;

/**
 * Created by LBARCELLA on 20/05/2017.
 */
public enum BanType implements Serializable {
    dice,
    valuableMalus,
    special,
    noVictoryPoints,
    victoryMalus;
}
