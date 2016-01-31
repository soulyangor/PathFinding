/*
   Информационно-вычислительный центр
         космодрома Байконур
*/

package td.gamefield;

import java.util.Map;

/**
 *
 * @author rmo
 */
public class GameField {
  //-------------------Logger---------------------------------------------------

  //-------------------Constants------------------------------------------------

  //-------------------Fields---------------------------------------------------
  private final int width;
  private final int height;
  private final Mesh[][] meshMatrix;
  private Map<Long, StatusAndAngle> unitActionList;

  //-------------------Constructors---------------------------------------------

  //-------------------Getters and setters--------------------------------------

  //-------------------Methods--------------------------------------------------

    public GameField(int width, int height, Mesh[][] meshMatrix) {
        this.width = width;
        this.height = height;
        this.meshMatrix = meshMatrix;
    }

}