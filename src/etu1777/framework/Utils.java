package etu1777.framework;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;

public class Utils {
    public String getCoreURL(String url){
        String[] newUrl=url.split("/");
        String rep=new String();
        for(int i=2; i<newUrl.length-1; i++){
            rep+=newUrl[i]+"/";
        }
        rep+=newUrl[newUrl.length-1];
        return rep;
    }
    @SuppressWarnings("rawtypes")
    public LinkedList<Class> getAllPackagesClasses(String path, String packageName) throws ClassNotFoundException{
        File first=new File(path);
        LinkedList<Class> liste=new LinkedList<>();
        File[] listFiles=first.listFiles();
        ClassLoader loader=Thread.currentThread().getContextClassLoader();
        for(File f:listFiles){
            if(f.isDirectory()){
                LinkedList<Class> otherList=getAllPackagesClasses(f.getAbsolutePath(), packageName+f.getName()+".");
                for(Class c:otherList){
                    liste.add(c);
                }
            }else if(f.getName().endsWith(".class")){
                liste.add(loader.loadClass(packageName+f.getName().substring(0, f.getName().length()-6)));
            }
        }
        return liste;
    }
    @SuppressWarnings("rawtypes")
    public HashMap<Method, Annotation> getAllAnnotedMethods(String annote, Class classe){
        HashMap<Method, Annotation> liste=new HashMap<>();
        Method[] methods=classe.getDeclaredMethods();
        for(Method m:methods){
            Annotation[] annotes=m.getAnnotations();
            for(Annotation an:annotes){
                if(an.annotationType().getSimpleName().equals(annote)){
                    liste.put(m, an);
                    break;
                }
            }
        }
        return liste;
    }
    @SuppressWarnings("rawtypes")
    public HashMap<String, Mapping> getAllURLMapping(String path) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
        LinkedList<Class> listeClass=getAllPackagesClasses(path, "");
        HashMap<String, Mapping> listeUrl=new HashMap<>();
        for(Class c:listeClass){
            HashMap<Method, Annotation> listeMeth=getAllAnnotedMethods("urlpattern", c);
            Object[] methodes=listeMeth.keySet().toArray();
            for(Object m:methodes){
                Mapping map=new Mapping();
                map.setClassName(c.getName());
                Method meth=(Method)m;
                map.setMethod(meth.getName());
                Annotation annote=listeMeth.get(meth);
                String value=annote.getClass().getMethod("value").invoke(annote).toString();
                listeUrl.put(value, map);
            }
        }
        return listeUrl;
    }
}