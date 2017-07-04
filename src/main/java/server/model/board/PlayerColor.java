package server.model.board;


import java.io.Serializable;

/**
 * Created by LBARCELLA on 18/05/2017.
 */
public enum PlayerColor implements Serializable {

    Green(0),
    Blue(1),
    Purple(2),
    Red(3),
    Yellow(4);

    int value;

    PlayerColor(int value){
        this.value = value;
    }

    public int toInt(){
        return this.value;
    }


    public static PlayerColor toEnum(int num){

        if(num == 0)
            return PlayerColor.Green;
        if(num == 1)
            return PlayerColor.Blue;
        if(num == 2)
            return PlayerColor.Purple;
        if(num == 3)
            return PlayerColor.Red;
        else
            return PlayerColor.Yellow;
    }

}
