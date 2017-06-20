package server.model.effect;

import java.io.Serializable;

/**
 * Created by LBARCELLA on 16/05/2017.
 */
public enum PermanentLeaderEffectType implements Serializable {
    cesareEffect("You don't need to satisfy the Military Points requirement when you take territory card"),
    filippoEffect("You don't have to spend 3 coins when you place your Famiy Members in a occupied tower"),
    lorenzoEffect("Copy the ability of another Leader Card already played by another player. Once you decide the ability to copy, it can’t be changed"),
    lucreziaEffect("Your colored Family Members have a bonus of +2 on their value"),
    ariostoEffect("You can place your Family Members in occupied action spaces"),
    sigismondoEffect("Your uncoloured Family Member has a bonus of +3 on its value"),
    borgiaEffect("Your colored Family Members have a bonus of +2 on their value"),
    picoEffect("When you take Development Cards, you get a discount of 3 coins"),
    ritaEffect("Each time you receive rosources as an immediate effect from Development Cards (not from an action space), you receive the resources twice"),
    moroEffect("Your coloured Family Member has a value of 5, regardless of their related dice"),
    sistoEffect("You gain 5 additional victory points when you support the church in a vatican report phase");

    String description;

    PermanentLeaderEffectType(String description) {

        this.description = description;

    }

    @Override
    public String toString() {
        return description;
    }
}
