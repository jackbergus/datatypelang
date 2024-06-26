import it.giacomobergami.datatypelang.compiler.ParserLexer;
import it.giacomobergami.datatypelang.compiler.parser.grammar.associations.ANTerm;
import it.giacomobergami.datatypelang.compiler.parser.grammar.domast.XMLAst;
import it.giacomobergami.datatypelang.compiler.parser.grammar.input.OnInput;
import it.giacomobergami.examples.DTL.DTLCompiler;
import it.giacomobergami.datatypelang.utils.ForFiles;
import it.giacomobergami.datatypelang.utils.funcs.OptErr;

import java.io.File;

/**
 * Created by vasistas on 13/12/16.
 */
public class DTLTest {

    public static void main(String args[]) {

        //Parsing the input for the programming language
        ParserLexer parserLexer = new ParserLexer(new File("typetest/typeslang.txt").getAbsoluteFile());
        File ff = new File("typetest/input.txt").getAbsoluteFile();
        parserLexer.showEditor(ForFiles.toString(ff), parserLexer);
        OptErr<ANTerm,OnInput> result = parserLexer.parseInput(ff);
        if (result.isError()) {
            System.err.println(result.error());
            parserLexer.showError(result.errorElement());
        }

        //Getting the intermediate representation
        XMLAst d = parserLexer.toXML(result.value());

        //Defining an user-defined
        DTLCompiler c = new DTLCompiler();
        c.compile(d);

    }


}
