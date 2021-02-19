package fr.cned.emdsgil.suividevosfrais;

import android.util.Log;


import fr.cned.emdsgil.suividevosfrais.AccesHTTP;
import fr.cned.emdsgil.suividevosfrais.AsyncResponse;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by emds on 25/02/2018.
 */

public class AccesDistant implements AsyncResponse {

    // constante
    //private static final String SERVERADDR = "http://192.168.0.18/coach/serveurcoach.php";

    // propriétés
    //private Controle controle;

    /**
     * Constructeur
     */
    /*public AccesDistant(){
        controle = Controle.getInstance(null);
    }*/

    /**
     * Retour du serveur HTTP
     * @param output
     */
    @Override
    public void processFinish(String output) {
        // pour vérification, affiche le contenu du retour dans la console
        Log.d("serveur", "************" + output);
        // découpage du message reçu
        String[] message = output.split("%");
        // contrôle si le retour est correct (au moins 2 cases)
        if(message.length>1){
            if(message[0].equals("enreg")){
                Log.d("enreg","****************"+message[1]);
            }else{
                if(message[0].equals("dernier")){
//                    Log.d("dernier","****************"+message[1]);
                    try {
                        // récupération des informations
                        JSONObject info = new JSONObject(message[1]);
                        Integer poids = info.getInt("poids");
                        Integer taille = info.getInt("taille");
                        Integer age = info.getInt("age");
                        Integer sexe = info.getInt("sexe");
                        //Date dateMesure = MesOutils.convertStringToDate(info.getString("datemesure"),"yyyy-MM-dd hh:mm:ss");
                        // création et mémorisation du profil
                        //Profil profil = new Profil(dateMesure, poids, taille, age, sexe);
                        //controle.setProfil(profil);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    if(message[0].equals("Erreur !")){
                        Log.d("Erreur !","****************"+message[1]);
                    }
                }
            }
        }
    }

    /**
     * Envoi de données vers le serveur distant
     * @param operation information précisant au serveur l'opération à exécuter
     * @param lesDonneesJSON les données à traiter par le serveur
     */
    public void envoi(String operation, JSONArray lesDonneesJSON){
        AccesHTTP accesDonnees = new AccesHTTP();
        // lien avec AccesHTTP pour permettre à delegate d'appeler la méthode processFinish
        // au retour du serveur
        //accesDonnees.delegate = this;
        // ajout de paramètres dans l'enveloppe HTTP
        accesDonnees.addParam("operation", operation);
        accesDonnees.addParam("lesdonnees", lesDonneesJSON.toString());
        // envoi en post des paramètres, à l'adresse SERVERADDR
        //accesDonnees.execute(SERVERADDR);
    }

}
