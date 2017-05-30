package exception;

/**
 * Created by LBARCELLA on 30/05/2017.
 */

/**
 * this exception is raised whenever a player has the preacher card that forbid to position a family member in certains towerslot
 */
public class PreacherEffectException extends ActionException {

    public PreacherEffectException(String message) {
        super(message);
    }
}
