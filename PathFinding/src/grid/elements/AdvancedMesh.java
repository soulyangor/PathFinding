/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grid.elements;


/**
 *
 * @author Sokolov@ivc.org
 */
public class AdvancedMesh extends Mesh {

    private int waterValue;
    private int groundValue;

    public AdvancedMesh(Mesh mesh) {
        super(mesh.getType(), mesh.getAltitude(), mesh.getExpense());
        this.waterValue = mesh.getType() == GroundType.WATER ? 1 : 0;
        this.groundValue = (mesh.getType() == GroundType.WATER)
                || (mesh.getType() == GroundType.ROCKS)
                || (mesh.getType() == GroundType.FOREST) ? 0 : 1;
    }

    public int getWaterValue() {
        return waterValue;
    }

    public void setWaterValue(int waterValue) {
        this.waterValue = waterValue;
    }

    public int getGroundValue() {
        return groundValue;
    }

    public void setGroundValue(int groundValue) {
        this.groundValue = groundValue;
    }

    public boolean isWalkable(int value) {
        return groundValue >= value;
    }

    public boolean canSwiming(int value) {
        return waterValue >= value;
    }

}
