package com.sajeon.kyu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiElement;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class JSAnnotator implements Annotator {

  @Override
  public void annotate(PsiElement element, AnnotationHolder holder) {
    //    System.out.println("annotate: " +  element.getText());
//    System.out.println("annotateEl: " +  element.getClass());
    if (!(element instanceof JSFile)) {
      return;
      // Annotate all functions in the file.
//      for (JSFunction function : ((JSFile) element).getgetFunctions()) {
//        holder.createInfoAnnotation(function, "This is a function.");
//      }
//
//      // Annotate all variables in the file.
//      for (JSVariable variable : ((JSFile) element).getVariables()) {
//        holder.createInfoAnnotation(variable, "This is a variable.");
//      }
    }

    ObjectMapper mapper = new ObjectMapper();
    String key = element.getText();
    String targetKey;

    try {
      Map<String, String> object = mapper.readValue(FileUtil.loadFile(new File(element.getProject().getBasePath() + "/preset.json")), Map.class);
      Set<String> entry = object.keySet();
      System.out.println("entry: " + entry);
      Optional<String> optionalTargetKey = entry.stream().filter(entryKey -> key.contains(entryKey)).findFirst();
      if (optionalTargetKey.isEmpty()) return;
      targetKey = optionalTargetKey.get();
    } catch (IOException e) {
      return;
    }

    System.out.println("targetKey: " + targetKey);
    int startIndex = element.getText().indexOf(targetKey);

    if (startIndex == -1) return;

    TextRange prefixRange = TextRange.from(element.getTextRange().getStartOffset() + startIndex, targetKey.length());
    holder.newSilentAnnotation(HighlightSeverity.WARNING).range(prefixRange).create();
  }
}