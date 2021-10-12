package engine;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;  
import org.hibernate.SessionFactory;  
import org.hibernate.Transaction;  
import org.hibernate.boot.Metadata;  
import org.hibernate.boot.MetadataSources;  
import org.hibernate.boot.registry.StandardServiceRegistry;  
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;  
  
public class StoreData {  
  
	public static void main( String[] args ){  
		StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();  
		Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build();  
          
        SessionFactory factory = meta.getSessionFactoryBuilder().build();  
        Session session = factory.openSession();  
        Transaction t = session.beginTransaction();  
        
        Set<Genre> set = new HashSet<Genre>();
        set.add(Genre.THRILLER);
          
        Movie m1=new Movie("Matrix", "When computer programmer "
        		+ "Thomas Anderson, under the hacker alias \"Neo\","
        		+ " uncovers the truth, he \"is drawn into a rebellion "
        		+ "against the machines\" along with other people who have "
        		+ "been freed from the Matrix.", null, set);       
         
        session.save(m1);  
        t.commit();  
        System.out.println("successfully saved");    
        factory.close();  
        session.close();     
    }  
}  