package fr.cned.emdsgil.suividevosfrais;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;




public class ConnexionActivity extends AppCompatActivity {

    private String login;
    private String mdp;
    private EditText txtLogin;
    private EditText txtMdp;
    //public AsyncResponse delegate=null; // gestion du retour asynchrone



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        setTitle("GSB : Identification Utilisateur");
        cmdTransfert_clic();
        Log.d("ConnexionActivity", "********** onCreate");
    }

    private void cmdTransfert_clic() {
        findViewById(R.id.cmdTransfert).setOnClickListener(new Button.OnClickListener() {
            String donneesJson = convertToJSONArray(Global.listFraisMois, login, mdp).toString();
            public void onClick(View v) {
                txtLogin = (EditText)findViewById(R.id.txtLogin);
                login = txtLogin.getText().toString();
                txtMdp = (EditText)findViewById(R.id.txtMdp);
                mdp = txtMdp.getText().toString();
                if(login.isEmpty() || mdp.isEmpty()) {
                    Toast.makeText(
                            ConnexionActivity.this,
                            "vous devez saisir votre login et votre mot de passe",
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    AccesDistant accesDistant = new AccesDistant();
                    accesDistant.envoi("enreg", convertToJSONArray(Global.listFraisMois, login, mdp));
                    Log.d("enreg","************** enreg : "+ convertToJSONArray(Global.listFraisMois, login, mdp).toString());
                }

            }
        }) ;
    }

    private JSONArray convertToJSONArray(Hashtable<Integer, FraisMois> listeFrais, String login, String mdp) {
        Log.d("Global", "********** convertToJSONArray");
        List laListe = new ArrayList();
        laListe.add(login);
        laListe.add(mdp);
        Integer annee = Calendar.getInstance().get(Calendar.YEAR);
        // Attention Calendar indexe les mois à partir de 0, donc il faut rajouter 1 pour avoir le bon
        // n° de mois
        Integer mois = Calendar.getInstance().get(Calendar.MONTH) + 1;
        Integer key = annee * 100 + mois;
        if (listeFrais.containsKey(key)) {
            laListe.add(listeFrais.get(key).getKm());
            laListe.add(listeFrais.get(key).getNuitee());
            laListe.add(listeFrais.get(key).getRepas());
            laListe.add(listeFrais.get(key).getEtape());
        }
        for (int m = 0; m < 12; m++) {
            // calcul de la période concernée
            if ((mois - m) >= 1) {
                key = annee * 100 + (mois - m);
                Log.d("Key", "********************" + key);
            } else {
                key = (annee - 1) * 100 + (mois + 12 - m);
            }
            List laListeHF = new ArrayList();
            // vérification si dans la liste de frais du mois déterminé, il y a des frais
            if (listeFrais.containsKey(key)) {
                for (FraisHf n : listeFrais.get(key).getLesFraisHf()) {
                    laListe.add(key);
                    laListe.add(n.getJour());
                    laListe.add(n.getMontant());
                    laListe.add(n.getMotif());
                }
            }
        }
        Log.d("convertJsonToArray", "********** " + (new JSONArray((laListe))));
        return new JSONArray(laListe);
    }


    /**
     * Cas particulier du bouton pour le transfert d'informations vers le serveur
     */
    /*private void cmdTransfert_clic(JSONArray lesdonneesJSON) {
        findViewById(R.id.cmdTransfert).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                login = ((EditText)findViewById(R.id.txtLogin)).toString();
                mdp = ((EditText)findViewById(R.id.txtMdp)).toString();

                try {
                    envoi("test", convertToJSONArray(Global.listFraisMois));
                } catch (Exception e) {
                    Log.d("Message", "*******************Au secours " + e);
                }

            }
        });
        Log.d("ConnexionActivity", "********** cmdTransfert_clic");
    }*/

    private void reinitilisationSerialize() {
        Global.listFraisMois = new Hashtable<>();
    }

    /**
     * Envoi de données vers le serveur distant
     *
     * @param lesDonneesJSON les données à traiter par le serveur
     */
    /*public void envoi(String operation, JSONArray lesDonneesJSON){
        AccesHTTP accesDonnees = new AccesHTTP();
        // lien avec AccesHTTP pour permettre à delegate d'appeler la méthode processFinish
        // au retour du serveur
        accesDonnees.delegate = this;
        // ajout de paramètres dans l'enveloppe HTTP
        //accesDonnees.addParam("envoi d'Android", "Coucou !!!");
        accesDonnees.addParam("operation", operation);
        accesDonnees.addParam("lesdonnees", lesDonneesJSON.toString());
        Log.d("accesDonnees", "**************" + accesDonnees.toString());
        // envoi en post des paramètres, à l'adresse SERVERADDR
        accesDonnees.execute(SERVERADDR);
        Log.d("ConnexionActivity", "********** envoi");
    }*/

   // @Override
    /*public void processFinish(String output) {
        Log.d("ConnexionActivity", "********** processFinish");
        // pour vérification, affiche le contenu du retour dans la console
        Log.d("serveur", "************" + output);
        // découpage du message reçu
        String[] message = output.split("%");
        // contrôle si le retour est correct (au moins 2 cases)
        if(message.length>1){
            if(message[0].equals("Erreur !")){
                Log.d("Erreur !","****************"+message[1]);
            }
        }

    }*/
}