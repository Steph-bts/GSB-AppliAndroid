package fr.cned.emdsgil.suividevosfrais;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class activity_total_recap extends AppCompatActivity {

    private Integer mois; // mois concerné
    private Integer annee; // année concernée
    private Integer etape; // nombre d'étapes du mois
    private Integer km; // nombre de km du mois
    private Integer nuitee; // nombre de nuitées du mois
    private Integer repas; // nombre de repas du mois
    private Float totalFraisHf; // total des frais hors forfait du mois

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_recap);
        setTitle("GSB : Total des frais à transférer");
        afficheMois();
        valoriseProprietes();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.retour_accueil))) {
            retourActivityPrincipale() ;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Retour à l'activité principale (le menu)
     */
    private void retourActivityPrincipale() {
        Intent intent = new Intent(activity_total_recap.this, MainActivity.class) ;
        startActivity(intent) ;
    }

    /**
     * Affiche le mois en cours dans le TextView approprié, format MM - AAAA
     */
    private void afficheMois() {
        annee = Calendar.getInstance().get(Calendar.YEAR);
        // Attention Calendar indexe les mois à partir de 0, donc il faut rajouter 1 pour avoir le bon
        // n° de mois
        mois = Calendar.getInstance().get(Calendar.MONTH) + 1;
        if(mois < 10) {
            ((TextView) findViewById(R.id.txtMoisAnnee)).setText("0" + mois.toString() + " - " + annee.toString());
        } else {
            ((TextView) findViewById(R.id.txtMoisAnnee)).setText(mois.toString() + " - " + annee.toString());
        }

    }

    /**
     * Récupère les informations de la liste de frais du mois (forfait et hors forfait)
     * et les affiche dans les TextView dédiés
     */
    private void valoriseProprietes() {
        Integer key = annee*100+mois ;
        // Initialisation à 0 des frais
        km = 0 ;
        etape = 0;
        nuitee = 0;
        repas = 0;
        ArrayList<FraisHf> liste = new ArrayList<>();
        totalFraisHf = Float.valueOf(0);
        // récupération des frais :
        if (Global.listFraisMois.containsKey(key)) {
            km = Global.listFraisMois.get(key).getKm();
            etape = Global.listFraisMois.get(key).getEtape();
            nuitee = Global.listFraisMois.get(key).getNuitee();
            repas = Global.listFraisMois.get(key).getRepas();
            liste = Global.listFraisMois.get(key).getLesFraisHf();
        }
        // Maj des TextView concernés
        ((TextView)findViewById(R.id.txtKmMois)).setText(String.format(Locale.FRANCE, "%d", km));
        ((TextView)findViewById(R.id.txtEtpMois)).setText(String.format(Locale.FRANCE, "%d", etape));
        ((TextView)findViewById(R.id.txtNuitMois)).setText(String.format(Locale.FRANCE, "%d", nuitee));
        ((TextView)findViewById(R.id.txtRepasMois)).setText(String.format(Locale.FRANCE, "%d", repas));
        // Addition des frais hors forfait du mois
        for(FraisHf frais : liste) {
            totalFraisHf += frais.getMontant();
        }
        // Maj du montant dans le TextView
        ((TextView)findViewById(R.id.txtHfMois)).setText(String.format(Locale.FRANCE, "%.2f", totalFraisHf));
    }
}