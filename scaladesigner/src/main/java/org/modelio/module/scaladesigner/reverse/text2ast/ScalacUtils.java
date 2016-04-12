package org.modelio.module.scaladesigner.reverse.text2ast;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScalacUtils {

    private static Pattern successShortNamePattern = Pattern.compile("^(?:\\[\\[syntax trees at end of parser]]|)\\s*// Scala source: (.*)$");
    private static Pattern errorLinePattern = Pattern.compile("(.*):(\\d+): error: (.*)");

    //we have only short name of files => can't get full name => may be duplicates => use List of Pair's
    //instead of Map
    public static List<ResultInfo> mapSourceAndStringAst(List<File> sources, List<String> scalacOutput) {
        File resFile = null;
        String resString = null;
        List<ResultInfo> toReturn = new ArrayList<>();
        for (int i = 0; i < scalacOutput.size(); i++) {
            String scalacLine = scalacOutput.get(i);
            if (scalacLine == null || scalacLine.isEmpty()) continue;
            resFile = findFileByShortName(sources, getShortNameFromSuccessOutput(scalacLine)).get();
            if (i < scalacOutput.size() - 1)
                scalacLine = scalacOutput.get(++i);
            else break;
            if (scalacLine == null || scalacLine.isEmpty()) continue;
            resString = scalacLine;
            toReturn.add(new ResultInfo(resFile, resString));
        }
        return toReturn;
    }

    public static List<ErrorInfo> mapSourceAndErrors(List<File> sources, List<String> scalacErrors) {
        List<ErrorInfo> toReturn = new ArrayList<>();

        for (int i = 0; i < scalacErrors.size(); i++) {
            String scalacLine = scalacErrors.get(i);
            Matcher matcher = errorLinePattern.matcher(scalacLine);
            if (matcher.matches()) {
                toReturn.add(new ErrorInfo(
                        new File(matcher.group(1)),
                        Integer.parseInt(matcher.group(2)),
                        matcher.group(3),
                        scalacErrors.get(++i)));
            }
        }
        return toReturn;

    }

    public static Map<File, List<String>> mapSourceAndErrors() {
        return null; //TODO
    }

    private static String getShortNameFromSuccessOutput(String scalacLine) {
        //System.out.println(scalacLine);
        Matcher matcher = successShortNamePattern.matcher(scalacLine);
        if (matcher.matches() && matcher.groupCount() == 1)
            return matcher.group(1);
        return null;
    }

    private static Optional<File> findFileByShortName(List<File> sources, String shortName) {
        return sources.stream().filter(s -> s.getName().equals(shortName)).findFirst();
    }

    public static class ErrorInfo {
        private File file;
        private String error;
        private Integer lineNumber;
        private String line;

        public ErrorInfo(File file, Integer lineNumber, String error,  String line) {
            this.file = file;
            this.error = error;
            this.lineNumber = lineNumber;
            this.line = line;
        }

        public File getFile() {
            return file;
        }

        public String getError() {
            return error;
        }

        public Integer getLineNumber() {
            return lineNumber;
        }

        public String getLine() {
            return line;
        }

        @Override
        public String toString() {
            return "ErrorInfo{" +
                    "file=" + file +
                    ", error='" + error + '\'' +
                    ", lineNumber=" + lineNumber +
                    ", line='" + line + '\'' +
                    '}';
        }
    }

    public static class ResultInfo {
        private File file;
        private String result;

        public ResultInfo(File file, String result) {
            this.file = file;
            this.result = result;
        }

        public File getFile() {
            return file;
        }

        public String getResult() {
            return result;
        }

        @Override
        public String toString() {
            return "ResultInfo{" +
                    "file=" + file +
                    ", result='" + result + '\'' +
                    '}';
        }
    }

}
