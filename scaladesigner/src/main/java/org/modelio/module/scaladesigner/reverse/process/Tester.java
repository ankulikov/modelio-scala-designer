package org.modelio.module.scaladesigner.reverse.process;

import edu.kulikov.ast_parser.AstTreeParser;
import edu.kulikov.ast_parser.reader.ReaderConfig;
import org.modelio.module.scaladesigner.reverse.Reversor;

public class Tester {
    public static void main(String[] args) {

        ReaderConfig readerConfig = new ReaderConfig(true);
        AstTreeParser treeParser = new AstTreeParser(readerConfig);

    }
}
