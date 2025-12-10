package se.kth.iv1351.bankjdbc.model;

import se.kth.iv1351.bankjdbc.model.DTO.TADTO;

public class TeachingActivity implements TADTO {
    private String TAName;
    private double factor;

    /**
     * Creates a teaching activity with a name and factor
     * number.
     *
     * @param TAName     The .
     * @param holderName The account holder's holderName.
     */
    public TeachingActivity(String TAName, double factor) {
        this.TAName = TAName;
        this.factor = factor;
    }

    /**
     * @return The Teaching activity name.
     */
    public String getTAName() {
        return TAName;
    }

    /**
     * @return The factor.
     */
    public double getFactor() {
        return factor;
    }


    /**
     * @return A string representation of all fields in this object.
     */
    @Override
    public String toString() {
        StringBuilder stringRepresentation = new StringBuilder();
        stringRepresentation.append("Teaching activity: ");
        stringRepresentation.append(TAName);
        stringRepresentation.append(", factor: ");
        stringRepresentation.append(factor);
        return stringRepresentation.toString();
    }
}