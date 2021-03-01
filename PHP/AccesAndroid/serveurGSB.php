<?php
/**
 * Index du projet GSB
 *
 * PHP Version 7
 *
 * @category  PPE
 * @package   GSB
 * @author    Réseau CERTA <contact@reseaucerta.org>
 * @author    Stéphanie Otto <contact@lencodage.fr> 
 * @version   GIT: <0>
 * @link      http://www.reseaucerta.org Contexte « Laboratoire GSB »
 */


require '../includes/fct.inc.php';
require '../includes/class.pdogsb.inc.php';
$pdo = PdoGsb::getPdoGsb();


if (isset($_POST["operation"])){
    if(isset($_REQUEST['operation']) == "enreg") {
        try {
            $lesdonnees = $_REQUEST["lesdonnees"];

            // conversion du JSONarray
            $donnees = json_decode($lesdonnees);
            // mois en cours, au format aaaamm
            $mois = getMois(date('d/m/Y'));

            $login = $donnees[0];
            $mdp = $donnees[1];
            // Vérification des login et mdp : 
            $leVisiteur = $pdo->getInfosVisiteur($login, $mdp);
            if ($leVisiteur) {
                print("Authentification_OK%");
                $idVisiteur = $leVisiteur['id'];

                // Récupération du dernier mois de saisie des frais 
                $dernierMoisSaisi = $pdo->dernierMoisSaisi($idVisiteur);

                // test si les lignes de frais forfait sont déjà créées pour ce mois
                if ($dernierMoisSaisi == $mois) {
                    // récupération des valeurs déjà entrées pour ce visiteur et ce mois
                    $fraisForfaitEnCours = $pdo->getLesFraisForfait($idVisiteur, $mois);
                    // affectation dans des variables locales des quantités déjà enregistrées
                    $etp = intval($fraisForfaitEnCours[0]['quantite']);
                    $km = intval($fraisForfaitEnCours[1]['quantite']);
                    $nuit = intval($fraisForfaitEnCours[2]['quantite']);
                    $repas = intval($fraisForfaitEnCours[3]['quantite']);


                    // création d'un array, avec l'idFrais en clé et le total (montant
                    // déjà enregistré en base + ce qui est envoyé par l'appli Android)
                    $fraisAAjouter = array(
                        'KM' => $donnees[2] + $km,
                        'NUI' => $donnees[3] + $nuit,
                        'REP' => $donnees[4] + $repas,
                        'ETP' => $donnees[5] + $etp
                    );
                } else {
                    // Si les lignes de frais forfaits n'existent pas pour la période, 
                    // il faut donc les créer : 
                    $pdo->creeNouvellesLignesFrais($idVisiteur, $mois);

                    // création de l'array, avec l'idFrais en clé et le total (montant
                    // qui est envoyé par l'appli Android)
                    $fraisAAjouter = array(
                        'KM' => $donnees[2],
                        'NUI' => $donnees[3],
                        'REP' => $donnees[4],
                        'ETP' => $donnees[5]
                    );                
                }
                // mise à jour des lignes de frais forfait dans la base
                $pdo->majFraisForfait($idVisiteur, $mois, $fraisAAjouter);

                // insertion des lignes de frais HF : 
                // test s'il y a des frais hors forfait : 
                if(count($donnees) > 6) {
                    for ($i = 6; $i <= count($donnees) - 4; $i += 4) {
                        $pdo->creeNouveauFraisHorsForfait(
                            $idVisiteur,
                            $mois,
                            $donnees[$i + 3],
                            convertitPeriodeEnDate($donnees[$i], $donnees[$i + 1]),
                            $donnees[$i + 2]
                        );
                    }
                }
                print("La fiche de frais a été correctement mise à jour.");            
            } else {
                print("Echec%" . "Echec de l'authentification");
            }        
        } catch (PDOException $e) {
            print "Erreur !%" . $e->getTraceAsString();
        }
    }    
}
?>					
