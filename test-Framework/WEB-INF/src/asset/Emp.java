package asset;

import java.util.LinkedList;
import java.text.ParseException;

import etu1777.framework.ModelView;
import etu1777.framework.annotations.annote_param;
import etu1777.framework.annotations.scope;
import etu1777.framework.annotations.urlpattern;

@scope("singleton")
public class Emp {
    private int id;
    private String nom;
    public int getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public Emp(){}
    public Emp(int i, String n){
        id=i;
        nom=n;
    }
    @urlpattern(url = "huhu.dodo")
    public ModelView bidon() throws ParseException{
        ModelView model=new ModelView();
        model.setView("hello.jsp");
        LinkedList<Emp> l=new LinkedList<>();
        l.add(new Emp(1, "Jean"));
        l.add(new Emp(2, "Marie"));
        l.add(new Emp(3, "Jules"));
        model.addItem("lst", l);
        return model;
    }
    @urlpattern(url="emp-save.dodo")
    public ModelView save(){
        ModelView model=new ModelView();
        model.setView("hello.jsp");
        LinkedList<Emp> l=new LinkedList<>();
        l.add(this);
        model.addItem("lst", l);
        return model;
    }
    @urlpattern(url="find-by-id.dodo")
    public ModelView findById(@annote_param("id") int ID,@annote_param("nom") String nom) throws ParseException{
        ModelView model=new ModelView();
        model.setView("details.jsp");
        LinkedList<Emp> l=new LinkedList<>();
        l.add(new Emp(1, "Jean"));
        l.add(new Emp(2, "Marie"));
        l.add(new Emp(3, "Jules"));
        for(Emp e:l){
            if(e.getId()==ID&&e.getNom().equals(nom)){
                model.addItem("emp", e);
                break;
            }
        }
        return model;
    }
}
