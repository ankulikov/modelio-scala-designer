package org.modelio.module.scaladesigner.reverse.text2ast;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScalacUtils {

    private static Pattern successShortNamePattern = Pattern.compile("^(?:\\[\\[syntax trees at end of parser]]|)\\s*// Scala source: (.*)$");

    public static Map<File, String> mapSourceAndStringAst(List<File> sources, List<String> scalacOutput) {
        File resFile = null;
        String resString = null;
        Map<File, String> toReturn = new HashMap<>();
        for (int i = 0; i < scalacOutput.size(); i++) {
            String scalacLine = scalacOutput.get(i);
            if (scalacLine == null || scalacLine.isEmpty()) continue;
            resFile = findByShortName(sources, getShortNameFromSuccessOutput(scalacLine)).get();
            if (i < scalacOutput.size() - 1)
                scalacLine = scalacOutput.get(++i);
            else break;
            if (scalacLine == null || scalacLine.isEmpty()) continue;
            resString = scalacLine;
            toReturn.put(resFile, resString);
        }
        return toReturn;
    }

    public static Map<File, List<String>> mapSourceAndErrors() {
        return null; //TODO
    }

    private static String getShortNameFromSuccessOutput(String scalacLine) {
        System.out.println(scalacLine);
        Matcher matcher = successShortNamePattern.matcher(scalacLine);
        if (matcher.matches() && matcher.groupCount() == 1)
            return matcher.group(1);
        return null;
    }

    private static Optional<File> findByShortName(List<File> sources, String shortName) {
        return sources.stream().filter(s -> s.getName().equals(shortName)).findFirst();
    }


}
