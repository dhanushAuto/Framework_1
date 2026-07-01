package utilities;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class config_utils {
   private static final Properties properties = new Properties();

   static{
       try(FileInputStream fis = new FileInputStream("Config.properties")){
           properties.load(fis);
       } catch (IOException e) {
           throw new RuntimeException("Failed to load configuration properties", e);
       }
   }

   public static String get_property(String env, String appType){
       String key = env.toLowerCase() + "." + appType.toLowerCase();
       String value = properties.getProperty(key);
         if(value == null){
             throw new RuntimeException("Failed to find property: " + key);
         }
         return value;
   }
}