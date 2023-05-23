package etu1777.framework.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import etu1777.framework.FileUpload;
import etu1777.framework.Mapping;
import etu1777.framework.ModelView;
import etu1777.framework.Utils;

@WebServlet("/upload")
@MultipartConfig
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
            Field[] fields=classe.getDeclaredFields();
            for(Field f:fields){
                Class<? extends Object> typeClass=utils.getClassFromName(f.getType().getName());
                String param=req.getParameter(f.getName());
                Method setter=classe.getMethod("set"+utils.majStart(f.getName()), typeClass);
                if(param!=null){    
                    if(f.getType().getSimpleName().equals("String")==false){
                        String parse=utils.getParseMethod(typeClass);
                        Method parser=typeClass.getMethod(parse, String.class);
                        setter.invoke(objet, parser.invoke(typeClass, param));
                    }else{
                        setter.invoke(objet, param);
                    }
                }
                if(req.getContentType()!=null&&req.getContentType().startsWith("multipart/")){
                    Part filePart=req.getPart(f.getName());
                    if(filePart!=null&&typeClass.equals(FileUpload.class)){
                        InputStream fileData=filePart.getInputStream();
                        ByteArrayOutputStream byteOut=new ByteArrayOutputStream();
                        try{
                            byte[] byteArray=new byte[8192];
                            int byteRead;
                            while((byteRead=fileData.read(byteArray))!=-1){
                                byteOut.write(byteArray, 0, byteRead);
                            }
                            byte[] file=byteOut.toByteArray();
                            FileUpload uploaded=new FileUpload();
                            uploaded.setName(filePart.getSubmittedFileName());
                            uploaded.setFile(file);
                            setter.invoke(objet, uploaded);
                        }finally{
                            fileData.close();
                            byteOut.close();
                        }
                    }
                }
            }

            Method method_view=utils.getMethodeByAnnotation("urlpattern", url, classe);
            Parameter[] params=method_view.getParameters();
            ModelView view=null;
            if(params.length>0){
                Object[] listParams=new Object[params.length];
                for(int i=0; i<params.length; i++){
                    Annotation[] annotes=params[i].getAnnotations();
                    listParams[i]=null;
                    for(Annotation a:annotes){
                        if(a.annotationType().getSimpleName().equals("annote_param")){
                            String req_param=req.getParameter(a.annotationType().getMethod("value").invoke(a).toString());
                            if(req_param!=null){
                                Class class_param=utils.getClassFromName(params[i].getType().getName());
                                if(class_param.getSimpleName().equals("String")==false){
                                    String parse=utils.getParseMethod(class_param);
                                    Method parser=class_param.getMethod(parse, String.class);
                                    listParams[i]=parser.invoke(class_param, req_param);
                                }else{
                                    listParams[i]=req_param;
                                }
                            }
                            if(req.getContentType()!=null&&req.getContentType().startsWith("multipart/")){
                                Part filePart=req.getPart(a.annotationType().getMethod("value").invoke(a).toString());
                                if(filePart!=null){
                                    InputStream fileData=filePart.getInputStream();
                                    ByteArrayOutputStream byteOut=new ByteArrayOutputStream();
                                    byte[] byteArray=new byte[8192];
                                    int byteRead;
                                    while((byteRead=fileData.read(byteArray))!=-1){
                                        byteOut.write(byteArray, 0, byteRead);
                                    }
                                    byte[] file=byteOut.toByteArray();
                                    fileData.close();
                                    byteOut.close();
                                    FileUpload uploaded=new FileUpload();
                                    uploaded.setName(filePart.getSubmittedFileName());
                                    uploaded.setFile(file);
                                    listParams[i]=uploaded;
                                }
                            }
                            break;
                        }
                    }
                }
                view=(ModelView)(method_view.invoke(objet, listParams));
            }else if(params.length==0){
                view=(ModelView)(method_view.invoke(objet));
            }
            for(Map.Entry<String, Object> entry:view.getData().entrySet()){
                req.setAttribute(entry.getKey(), entry.getValue());
            }
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