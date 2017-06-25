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
    Yellow(4),
    White(5),
    Cyan(6),
    Black(7);

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
        if(num == 4)
            return PlayerColor.Yellow;
        if(num == 5)
            return PlayerColor.White;
        if(num == 6)
            return PlayerColor.Cyan;
        else
            return PlayerColor.Black;
    }

}
