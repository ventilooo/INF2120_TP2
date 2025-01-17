package tp3;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import tda.Individu;
import tda.Liste;
import test.TestGenealogie;
import util.Personne;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.TimeZone;

/*
 * Created by SZAI29079604 on 04/04/2016.
 */
public class VueGenealogie {
    private Gen arbre;
    private JFrame frame;
    private DefaultListModel model;
    private JList genList;
    private Personne active;
    private JTextArea zoneTexte;
    private JPanel zoneDeTexte;
    private boolean parent1 = true;

    public VueGenealogie() {
        initialize();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                VueGenealogie window = new VueGenealogie();
                window.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void initialize() {
        frame = new JFrame();
        arbre = new Gen();
        model = new DefaultListModel();
        //
        JPanel panel = new JPanel(new FlowLayout());
        JPanel jListPanel = new JPanel(new FlowLayout());
        zoneDeTexte = new JPanel(new FlowLayout());
        //
        setJlist(jListPanel);
        setZoneTexte(zoneDeTexte);
        setCheckBox(panel);
        //
        panel.add(jListPanel);
        panel.add(zoneDeTexte);
        createToolBar(frame);
        test();
        //
        frame.add(panel);
    }

    @SuppressWarnings("unchecked")
    private void createToolBar(JFrame frame) {
        JMenuBar menuBar = new JMenuBar();
        //Menu Gérer
        JMenu gestionMenu = new JMenu("Gérer");
        JMenuItem addIndividu = new JMenuItem("Ajouter un individu");
        addIndividu.addActionListener(e -> addIndividuDialog());
        JMenuItem addParents = new JMenuItem("Ajouetr un parent");
        addParents.addActionListener(e -> addParentDialog());
        JMenuItem displayIndividu = new JMenuItem("Afficher les individus");
        displayIndividu.addActionListener(e -> {
            model.clear();
            for (Object personne : arbre.lesIndividus()) {
                model.addElement(personne);
            }
            genList.setVisible(true);
        });
        JMenuItem exit = new JMenuItem("Quitter");
        exit.addActionListener(e -> System.exit(0));
        gestionMenu.add(addIndividu);
        gestionMenu.add(addParents);
        gestionMenu.add(displayIndividu);
        gestionMenu.add(exit);
        menuBar.add(gestionMenu);

        //Menu Explorer
        JMenu explorationMenu = new JMenu("Exploration");
        JMenuItem kidsMenuItem = new JMenuItem("Les enfants");
        kidsMenuItem.addActionListener(e -> {
            genList.setVisible(true);
            if (genList.getSelectedValue() != null) {
                model.clear();
                active = (Personne) genList.getSelectedValue();
                for (Individu personne : getArrayOfIndividu(arbre.lesEnfants(null, active))) {
                    model.addElement(personne);
                    genList.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(new JDialog(), "aucun individu sélectionner");
            }

        });
        JMenuItem parentsMenuItem = new JMenuItem("Les parents");
        parentsMenuItem.addActionListener(e -> {
            genList.setVisible(true);
            if (genList.getSelectedValue() != null) {
                active = (Personne) genList.getSelectedValue();
                model.clear();
                for (Individu personne : getArrayOfIndividu(arbre.lesParents(active))) {
                    model.addElement(personne);
                    genList.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(new JDialog(), "aucun individu sélectionner");
            }

        });
        JMenuItem brothersMenuItem = new JMenuItem("Les frères");
        brothersMenuItem.addActionListener(e -> {
            if (genList.getSelectedValue() != null) {
                active = (Personne) genList.getSelectedValue();
                model.clear();
                for (Individu personne : getArrayOfIndividu(arbre.laFratrie(active))) {
                    model.addElement(personne);
                    genList.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(new JDialog(), "aucun individu sélectionner");
            }

        });
        JMenuItem grandKidsMenuItem = new JMenuItem("Les petits enfants");
        grandKidsMenuItem.addActionListener(e -> {
            model.clear();
            if (genList.getSelectedValue() != null) {
                active = (Personne) genList.getSelectedValue();
                for (Individu personne : getArrayOfIndividu(arbre.lesPetitsEnfants(null, active))) {
                    model.addElement(personne);
                    genList.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(new JDialog(), "aucun individu sélectionner");
            }

        });

        explorationMenu.add(kidsMenuItem);
        explorationMenu.add(parentsMenuItem);
        explorationMenu.add(brothersMenuItem);
        explorationMenu.add(grandKidsMenuItem);
        menuBar.add(explorationMenu);

        //Menu Test
        JMenu menuTester = new JMenu("Tester");
        JMenuItem itemVerifier = new JMenuItem("Vérifier");
        itemVerifier.addActionListener(e -> {
            zoneTexte.setText("");
            if (arbre != null) {

                TestGenealogie.definirGenealogie(arbre);
                Result res = JUnitCore.runClasses(TestGenealogie.class);
                if (res.getFailureCount() == 0) {
                    zoneTexte.append("Pas d'erreurs");
                } else {
                    for (Failure fail : res.getFailures()) {
                        zoneTexte.append(fail.getTestHeader() + "\n");
                        zoneTexte.append(fail.getMessage() + "\n");
                    }
                }
            } else {
                zoneTexte.append("Le graphe n'est pas défini");
            }
        });
        menuTester.add(itemVerifier);
        menuBar.add(menuTester);

        frame.setJMenuBar(menuBar);
    }

    private void setZoneTexte(JPanel panel) {
        zoneTexte = new JTextArea();
        zoneTexte.setBackground(Color.orange);
        zoneTexte.setLocation(250, 250);
        frame.setBounds(300, 300, 1000, 1000);
        zoneTexte.setPreferredSize(new Dimension(500, 350));
        panel.add(zoneTexte);
    }

    private void setNomInZoneDeText(Personne individu, JPanel panel) {
        JLabel nomLabel = new JLabel("NOM:");
        JTextField nomField = new JTextField(individu.leNom());
        panel.add(nomLabel);
        panel.add(nomField);
    }

    private void setDateInZoneDeText(Personne individu, JPanel panel) {
        JLabel dateLabel = new JLabel("NOM:");
        JTextField dateField = new JTextField(individu.laDate().toString());
        panel.add(dateLabel);
        panel.add(dateField);
    }

    private void setPrenomInZoneDeText(Personne individu, JPanel panel) {
        JLabel prenomLabel = new JLabel("PRÉNOM:");
        JTextField prenomField = new JTextField(individu.lesPrenoms().toString());
        panel.add(prenomLabel);
        panel.add(prenomField);
    }

    private void setCheckBox(JPanel panel) {
        JCheckBox buttonNom = new JCheckBox("Nom");
        JCheckBox buttonPrenom = new JCheckBox("Prénom");
        JCheckBox buttonDate = new JCheckBox("Date");
        active = (Personne) genList.getSelectedValue();
        buttonNom.addActionListener(e -> {
            if (buttonNom.isSelected()) {
                active = (Personne) genList.getSelectedValue();
                setNomInZoneDeText(active, zoneDeTexte);
            }
        });
        buttonPrenom.addActionListener(e -> {
            if (buttonPrenom.isSelected()) {
                active = (Personne) genList.getSelectedValue();
                setPrenomInZoneDeText(active, zoneDeTexte);
            }
        });
        buttonDate.addActionListener(e -> {
            if (buttonDate.isSelected()) {
                active = (Personne) genList.getSelectedValue();
                setDateInZoneDeText(active, zoneDeTexte);
            }
        });
        panel.add(buttonNom);
        panel.add(buttonPrenom);
        panel.add(buttonDate);
    }

    @SuppressWarnings("unchecked")
    private void setJlist(JPanel panel) {
        genList = new JList(model);
        panel.add(genList);
    }

    private void addIndividuDialog() {
        JDialog jDialog = new JDialog();
        jDialog.setTitle("Ajouter Individu");
        jDialog.setSize(300, 200);
        jDialog.setVisible(true);
        jDialog.setLayout(new FlowLayout(FlowLayout.TRAILING));
        addIndividuDialogComponents(jDialog);

    }

    @SuppressWarnings("unchecked")
    private void addIndividuDialogComponents(JDialog jDialog) {
        JLabel nameLabel = new JLabel("Nom:");
        final JTextField nameField = new JTextField(21);
        JLabel prenomLabel = new JLabel("Prénoms:", SwingConstants.CENTER);
        final JTextField prenomsField = new JTextField(19);
        JLabel dateLabel = new JLabel("Date de naissance sous format jj-mm-yyy");
        final JTextField dateField = new JTextField(20);
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            try {
                DateFormat dfm = new SimpleDateFormat("dd-MM-YYYY");
                Personne moi = new Personne(nameField.getText(), getPrenoms(prenomsField.getText()), dfm.parse(dateField.getText()));
                arbre.ajout(moi);
                JOptionPane.showMessageDialog(new JDialog(), moi + " a été ajouté");
                jDialog.dispose();
            } catch (ParseException E) {
                JOptionPane.showMessageDialog(new JDialog(), "Date invalide");
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> jDialog.dispose());
        jDialog.add(nameLabel);
        jDialog.add(nameField);
        jDialog.add(prenomLabel);
        jDialog.add(prenomsField);
        jDialog.add(dateLabel);
        jDialog.add(dateField);
        jDialog.add(okButton);
        jDialog.add(cancelButton);
    }

    private ArrayList<String> getPrenoms(String str) {
        StringTokenizer tokenizer = new StringTokenizer(str);
        ArrayList<String> stringList = new ArrayList<>();
        while (tokenizer.hasMoreElements()) {
            stringList.add(tokenizer.nextElement().toString());
        }
        return stringList;
    }

    private void addParentDialog() {
        JDialog jDialog = new JDialog();
        jDialog.setTitle("Choix du parent");
        jDialog.setSize(300, 200);
        jDialog.setVisible(true);
        jDialog.setLayout(new FlowLayout(FlowLayout.CENTER));
        choixDuParent(jDialog);
    }

    @SuppressWarnings("unchecked")
    private void leParentDialog() {
        JDialog jDialog = new JDialog();
        jDialog.setSize(300, 200);
        jDialog.setVisible(true);
        jDialog.setLayout(new FlowLayout(FlowLayout.CENTER));
        jDialog.setTitle("Le parent");
        JLabel instruction = new JLabel("Choix d'un parent");
        JComboBox parentsBox = new JComboBox(getArrayPersone());
        JButton ouiButton = new JButton("OUI");
        JButton annulerButton = new JButton("Annuler");
        jDialog.add(instruction);
        jDialog.add(parentsBox);
        jDialog.add(ouiButton);
        jDialog.add(annulerButton);
        ouiButton.addActionListener(e -> {
            Individu activeIndividu = (Individu) parentsBox.getSelectedItem();
            if (genList.getSelectedValue() != null) {
                active = (Personne) genList.getSelectedValue();
                if (parent1) {
                    arbre.definirParent1(active, activeIndividu);
                    JOptionPane.showMessageDialog(new JDialog(), activeIndividu + " est maintenant le parent1 de " + active);
                } else {
                    arbre.definirParent2(active, activeIndividu);
                    JOptionPane.showMessageDialog(new JDialog(), activeIndividu + " est maintenant le parent2 de " + active);
                }
            } else {
                JOptionPane.showMessageDialog(new JDialog(), "aucun individu sélectionner");
            }

            jDialog.dispose();

        });
        annulerButton.addActionListener(e -> jDialog.dispose());

    }

    private void choixDuParent(JDialog jDialog) {
        JLabel instruction2 = new JLabel("Est-ce le premier parent ?");
        JButton ouiButton = new JButton("OUI");
        JButton annulerButton = new JButton("Annuler");
        JButton nonButton = new JButton("NON");
        jDialog.add(instruction2);
        jDialog.add(ouiButton);
        jDialog.add(nonButton);
        jDialog.add(annulerButton);
        ouiButton.addActionListener(e -> {
            jDialog.dispose();
            leParentDialog();
        });
        nonButton.addActionListener(e -> {
            parent1 = false;
            jDialog.dispose();
            leParentDialog();
        });
        annulerButton.addActionListener(e -> jDialog.dispose());
    }

    private Individu[] getArrayPersone() {
        Individu[] arr = new Personne[arbre.lesIndividus().longueur()];
        for (int i = 0; i < arbre.lesIndividus().longueur(); i++) {
            arr[i] = arbre.lIndividu(i);
        }
        return arr;
    }

    private Individu[] getArrayOfIndividu(Liste<Individu> liste) {
        Individu[] arr = new Personne[liste.longueur()];
        for (int i = 0; i < liste.longueur(); i++) {
            arr[i] = liste.elementPosition(i);
        }
        return arr;
    }

    @SuppressWarnings("unchecked")
    private void test() {
        DateFormat dfm = new SimpleDateFormat("Y");
        dfm.setTimeZone(TimeZone.getTimeZone("Canada/Montreal"));
        try {
            Personne p1 = new Personne("Tremblay", new ArrayList<String>(Arrays.asList("Guy")), dfm.parse("1990"));
            Personne p2 = new Personne("Mili", new ArrayList<String>(Arrays.asList("Hafedh")), dfm.parse("1994"));
            Personne p3 = new Personne("Gagnon", new ArrayList<String>(Arrays.asList("Étienne")), dfm.parse("1991"));
            Personne p4 = new Personne("Laforest", new ArrayList<String>(Arrays.asList("Louise")), dfm.parse("1992"));
            Personne p5 = new Personne("Cherkaoui", new ArrayList<String>(Arrays.asList("Omar")), dfm.parse("1967"));
            Personne p6 = new Personne("Kerhervé", new ArrayList<String>(Arrays.asList("Brigitte")), dfm.parse("1965"));
            Personne p7 = new Personne("Moha", new ArrayList<String>(Arrays.asList("Naouel")), dfm.parse("1964"));
            Personne p8 = new Personne("Boukadoum", new ArrayList<String>(Arrays.asList("Mounir")), dfm.parse("1963"));
            Personne p9 = new Personne("Villemaire", new ArrayList<String>(Arrays.asList("Roger")), dfm.parse("1945"));
            Personne p10 = new Personne("Dupuis", new ArrayList<String>(Arrays.asList("Robert")), dfm.parse("1940"));
            Personne p11 = new Personne("Gabrini", new ArrayList<String>(Arrays.asList("Philippe")), dfm.parse("1941"));
            Personne p12 = new Personne("Bergeron", new ArrayList<String>(Arrays.asList("Anne")), dfm.parse("1923"));
            Personne p13 = new Personne("Davidson", new ArrayList<String>(Arrays.asList("Paul")), dfm.parse("1948"));
            Personne p14 = new Personne("Makarenkov", new ArrayList<String>(Arrays.asList("Vladimir")), dfm.parse("1962"));
            Personne p15 = new Personne("Bouisset", new ArrayList<String>(Arrays.asList("Marc")), dfm.parse("1922"));
            arbre.ajout(p1);
            arbre.ajout(p2);
            arbre.ajout(p3);
            arbre.ajout(p4);
            arbre.ajout(p5);
            arbre.ajout(p6);
            arbre.ajout(p7);
            arbre.ajout(p8);
            arbre.ajout(p9);
            arbre.ajout(p10);
            arbre.ajout(p11);
            arbre.ajout(p12);
            arbre.ajout(p13);
            arbre.ajout(p14);
            arbre.ajout(p15);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    //Je n'arrive pas à faire fonctionner les TEST que vous nous avez fournis cependant quand j'évalue au débug, mes list sont bien former.
    //Si possible J'aimerais prendre RDV avec vous pour discuter de la correction de se TP.

}
