package asset;

import etu1777.framework.FileUpload;
import etu1777.framework.ModelView;
import etu1777.framework.annotations.urlpattern;

public class Client {
    private String nom;
    private FileUpload file;
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public FileUpload getFile() {
        return file;
    }
    public void setFile(FileUpload file) {
        this.file = file;
    }

    public Client() {
    }
    public Client(String nom) {
        this.nom = nom;
    }
    @urlpattern(url="client-ident.dodo")
    public ModelView identify(){
        ModelView view= new ModelView();
        view.setView("clientDetails.jsp");
        view.addItem("client", this);
        return view;
    }
}
