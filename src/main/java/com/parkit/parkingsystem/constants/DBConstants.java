package com.parkit.parkingsystem.constants;

/**
 * Contient toutes les requêtes SQL utilisées par l'application pour son fonctionnement.
 * Utilise des paramètres sous la forme du symbole "?"
 */
public class DBConstants {

    /**
     * Récupère la première place de parking disponible pour le type de véhicule qui est précisé par paramètre.
     */
    public static final String GET_NEXT_PARKING_SPOT = "select min(PARKING_NUMBER) from parking where AVAILABLE = true and TYPE = ?";

    /**
     * Modifie le status d'occupation de la place de parking précisé grâce au second paramètre de la requête avec la valeur du premier paramètre.
     */
    public static final String UPDATE_PARKING_SPOT = "update parking set available = ? where PARKING_NUMBER = ?";

    /**
     * Crée un nouveau ticket dont les valeurs sont passées en paramètre.
     */
    public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME) values(?,?,?,?,?)";

    /**
     * Modifie le ticket dont le numéro est précisé en troisième paramètre afin de lui renseigner un prix en premier paramètre et une dateTime de sortie en second paramètre.
     */
    public static final String UPDATE_TICKET = "update ticket set PRICE=?, OUT_TIME=? where ID=?";

    /**
     * Permet de récupérer un ticket correspondant au véhicule dont le numéro d'enregistrement est passé en paramètre.
     */
    public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? order by t.IN_TIME  limit 1";
}
