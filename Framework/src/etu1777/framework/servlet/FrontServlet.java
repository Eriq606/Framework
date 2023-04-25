package etu1777.framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import etu1777.framework.Mapping;
import etu1777.framework.ModelView;
import etu1777.framework.Utils;

public class FrontServlet extends HttpServlet{
    HashMap<String, Mapping> mappingUrls;
    public void init(){
        Utils utils=new Utils();
        try {
            String classpath=getInitParameter("classpath");
            mappingUrls=utils.getAllURLMapping(classpath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void processRequest(HttpServletRequest req, HttpServletResponse res) throws Exception{
        String url=req.getRequestURI();
        Utils utils=new Utils();
        url=utils.getCoreURL(url);
        PrintWriter writer=res.getWriter();
        for(Map.Entry<String, Mapping> entry:mappingUrls.entrySet()){
            writer.println(entry.getKey()+": "+entry.getValue());
        }
        if(mappingUrls.containsKey(url)){
            ClassLoader loader=Thread.currentThread().getContextClassLoader();
            Class<? extends Object > classe=loader.loadClass(mappingUrls.get(url).getClassName());
            Object objet=classe.getConstructor().newInstance();
            ModelView view=(ModelView)(classe.getMethod(mappingUrls.get(url).getMethod()).invoke(objet));
            RequestDispatcher dispat=req.getRequestDispatcher(view.getView());
            dispat.forward(req, res);
        }else{
            writer.println("URL introuvable");
        }
    }
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        try {
            processRequest(req, res);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        try {
            processRequest(req, res);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}