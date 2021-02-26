package fr.cned.emdsgil.suividevosfrais;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


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
    private static final String SERVERADDR = "http://192.168.56.1/gsb/accesAndroid/serveurGSB.php";

    // propriétés
    private Context context;


    /**
     * Constructeur
     */
    public AccesDistant(){
        super();
        //this.context = (Context)ConnexionActivity.getApp();
    }

    /**
     * Retour du serveur HTTP
     * @param output
     */
    @Override
    public void processFinish(String output) {
        // pour vérification, affiche le contenu du retour dans la console
        Log.d("serveur", "************" + output);
        Global.messageServeur = "";
        // découpage du message reçu
        String[] message = output.split("%");
        // contrôle si le retour est correct (au moins 2 cases)
        if(message.length>1){
            Global.messageServeur = message[1];
            if(message[0].equals("Echec")){
                Log.d("Echec","****************"+message[1]);
                //Global.messageServeur = message[1];

            }else if(message[0].equals("Authentification_OK")){
                Log.d("Authentification","****************"+message[1]);
                //Global.messageServeur = message[1];

            }else if(message[0].equals("Erreur !")){
                Log.d("Erreur !","****************"+message[1]);

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
        accesDonnees.delegate = this;
        // ajout de paramètres dans l'enveloppe HTTP
        accesDonnees.addParam("operation", operation);
        accesDonnees.addParam("lesdonnees", lesDonneesJSON.toString());
        // envoi en post des paramètres, à l'adresse SERVERADDR
        accesDonnees.execute(SERVERADDR);
    }

}