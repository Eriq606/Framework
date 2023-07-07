## Utilisation du framework

# Installation
- Placer dans le .jar dans le dossier WEB-INF/lib du projet tomcat
- Configurer web.xml comme suit:

        <servlet> 
            <servlet-name>FrontServlet</servlet-name> 
            <servlet-class>etu1777.framework.servlet.FrontServlet</servlet-class>
            <init-param>
                <param-name>classpath</param-name>
                <param-value> <chemin_absolu/repertoire/fichiers_.class_des_modeles> </param-value>
            </init-param>
        </servlet>
        <servlet-mapping> 
            <servlet-name>FrontServlet</servlet-name>
            <url-pattern>/</url-pattern> 
        </servlet-mapping>

# Classes modeles
- Les classes modeles doivent avoir des getter et setter pour chaque attribut

        get<NomAttribut>()
        set<NomAttribut>()

* Les arguments des setters doivent etre des objets ou l'equivalent objet des classes de base (int -> Integer)

- Les methodes d'actions doivent etre annotees par urlpattern(url="") et le type de retour est ModelView
- La classe ModelView comporte 2 attributs:

<p>String view</p>
<p>HashMap data</p>

Pour passer un quelconque objet vers une vue .jsp, utiliser la methode addItem de ModelView
L'attribut String view est le nom du fichier .jsp a ouvrir en vue.

        import etu1777.framework.ModelView;

        @urlpattern(url=" <votre URL> ")
        public ModelView <votreMethode>(){
            ModelView model=new ModelView();
            model.setView(" <votre page .jsp> ");
            ...
            model.addItem(" <nom de l'objet lors de l'appel dans le .jsp> ", <la variable Objet>);
            return model;
        }

- Pour utiliser la variable que vous avez passee dans le ModelView en tant que data,
il suffit de le recuperer en tant qu'attribut de requete dans la vue .jsp

        <%@page import=" <vos classes> " %>
        ...
        <% <VotreClasse> element=( <VotreClasse> )request.getAttribute(" <nom de l'objet depuis la methode dans le modele> "); %>
        ...
    
# Un exemple: Formulaire
- La destination du formulaire est l'url defini dans l'annotation urlpattern de la methode cible
- Pour les methodes CRUD, il est preferable de nommer les champs du formulaire par les attributs de la classe modele

Classe Modele:

        public class MaClasse{
            String attribut1;
            int attribut2;
            ...
            @urlpattern(url="mon-url")
            public ModelView maFonction(){
                ...
            }
        }

Page jsp

        ...
        <form action="mon-url">
            ...
            <input type="text" name="attribut1">
            <input type="number" name="attribut2">
            ...
        </form>

* Si le(s) nom(s) d'un/des champ(s) est/sont egal/egaux a un ou plusieurs attributs,
les valeurs sont directement accessibles dans la methode controller cible du formulaire

        @urlpattern(url="mon-url")
        public ModelView maFonction(){
            ...
            this.getAttribut1();
            this.getAttribut2();
            ...
        }

## Methodes controller
- les arguments à definir a partir des parametres de requetes doivent être annotées par @annote_param
- le nom des champs ou parametres de requetes doit etre egal au parametre correspondant

### Vue.jsp

    <a href="/projet/findid?id=5&nom=jean">Lien</a>

### Methode controller

    @urlpattern(url="findid")
    ModelView findById(@annote_param("id") int id, @annote_param("nom") String nom){
        ...
    }

## Pre-requis 
- Java 8+
- Windows / Linux