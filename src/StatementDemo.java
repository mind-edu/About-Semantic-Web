import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.VCARD;

/**
 * Creator: DreamBoy
 * Date: 2019/4/21.
 */

public class StatementDemo {
    public static void main(String[] args){

        //Introduction
        String personURI = "http://somewhere/JohnSmith";
        String givenName = "John";
        String familyName = "Smith";
        String fullName = givenName + " " + familyName;
        Model model = ModelFactory.createDefaultModel();

        Resource johnSmith = model.createResource(personURI);
        johnSmith.addProperty(VCARD.FN, fullName);
        johnSmith.addProperty(VCARD.N,
                model.createResource()
                        .addProperty(VCARD.Given, givenName)
                        .addProperty(VCARD.Family, familyName));

        //Statement
        StmtIterator iter = model.listStatements();

        while(iter.hasNext()){
            Statement stmt = iter.nextStatement();
            Resource subject = stmt.getSubject();
            Property predicate = stmt.getPredicate();
            RDFNode object = stmt.getObject();

            System.out.print(subject.toString());
            System.out.print(" "+predicate.toString());
            if(object instanceof Resource){
                System.out.print(object.toString());
            }else{
                System.out.print("\"" + object.toString() + "\"");
            }

            System.out.println(" .");
        }
    }
}
