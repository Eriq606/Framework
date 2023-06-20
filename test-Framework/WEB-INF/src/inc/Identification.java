package inc;

import etu1777.framework.ModelView;
import etu1777.framework.annotations.annote_param;
import etu1777.framework.annotations.scope;
import etu1777.framework.annotations.urlpattern;

@scope("singleton")
public class Identification {
    
    @urlpattern(url="identify.dodo")
    public ModelView estAdmin(@annote_param("username")String username, @annote_param("password")String password){
        ModelView modele=new ModelView();
        modele.setView("client.jsp");
        if(username.equals("admin")&&password.equals("1234")){
            modele.addSessionAttribute("estConnecte", true);
            modele.addSessionAttribute("role", "admin");
        }else{
            modele.addSessionAttribute("estConnecte", false);
            modele.addSessionAttribute("role", "anonyme");
        }
        return modele;
    }
}
