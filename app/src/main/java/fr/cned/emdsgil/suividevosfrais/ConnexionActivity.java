package fr.cned.emdsgil.suividevosfrais;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        setTitle("GSB : Identification Utilisateur");
        cmdTransfert_clic();
    }

    private void cmdTransfert_clic() {
        findViewById(R.id.cmdTransfert).setOnClickListener(new Button.OnClickListener() {
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
                    Log.d("messageServeur", Global.messageServeur);
                    AccesDistant accesDistant = new AccesDistant();
                    accesDistant.envoi("enreg", convertToJSONArray(Global.listFraisMois, login, mdp));
                    // pour qu'au clic sur le bouton transférer, le clavier disparaisse pour que
                    // l'utilisateur puisse correctement voir le Toast :
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    // envoi à l'utilisateur le message du serveur via un Toast
                    Toast.makeText(ConnexionActivity.this, Global.messageServeur, Toast.LENGTH_LONG).show();
                    //Global.listFraisMois = new Hashtable<>();
                }
            }
        }) ;
    }

    private JSONArray convertToJSONArray(Hashtable<Integer, FraisMois> listeFrais, String login, String mdp) {
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
        return new JSONArray(laListe);
    }


}