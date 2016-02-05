/*
 Информационно-вычислительный центр
 космодрома Байконур
 */
package grid.elements;

import java.util.List;
import java.util.Objects;
import units.Unit;

/**
 *
 * @author alextim
 */
public class Mesh {
    //-------------------Logger---------------------------------------------------

    //-------------------Constants------------------------------------------------
    //-------------------Fields---------------------------------------------------
    private final GroundType type;
    private final int altitude;
    private final double expense;
    private Unit unit;
    private List<Unit> content;

    //-------------------Constructors---------------------------------------------
    public Mesh(GroundType type, int altitude, double expense) {
        this.type = type;
        this.altitude = altitude;
        this.expense = expense;
    }

    //-------------------Getters and setters--------------------------------------
    public GroundType getType() {
        return type;
    }

    public int getAltitude() {
        return altitude;
    }

    public double getExpense() {
        return expense;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public List<Unit> getContent() {
        return content;
    }

    //-------------------Methods--------------------------------------------------
    /**
     * Перегрузка метода equals класса Object.
     *
     * @param otherObject - сравниваемый на эквивалентность объект
     * @return если объекты совпадают, то возвращает true.
     *
     * Сравнивает только тип, высоту и цену. Наличие или отсутствие юнитов
     * игнорируется.
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null) {
            return false;
        }
        if (getClass() != otherObject.getClass()) {
            return false;
        }
        Mesh other = (Mesh) otherObject;

        return (this.altitude == other.altitude) && (this.expense == other.expense)
                && (this.type == other.type);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.type);
        hash = 79 * hash + this.altitude;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.expense)
                ^ (Double.doubleToLongBits(this.expense) >>> 32));
        return hash;
    }

}
