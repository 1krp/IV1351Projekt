package se.kth.iv1351.bankjdbc.model.DTO;

/**
 * Specifies a read-only view of a teaching activity.
 */
public interface TADTO {
    /**
     * @return The Teaching activity name.
     */
    public String getTAName();

    /**
     * @return The factor.
     */
    public double getFactor();


}