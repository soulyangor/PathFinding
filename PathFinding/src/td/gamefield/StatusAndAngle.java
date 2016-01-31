/*
   Информационно-вычислительный центр
         космодрома Байконур
*/

package td.gamefield;

import td.unit.Status;

/**
 *
 * @author AlexTim@td
 */
class StatusAndAngle {
  //-------------------Logger---------------------------------------------------  

  //-------------------Constants------------------------------------------------

  //-------------------Fields---------------------------------------------------
    public final Status status;
    public final double angle;

  //-------------------Constructors---------------------------------------------

    public StatusAndAngle(Status status, double angle) {
        this.status = status;
        this.angle = angle;
    }

}