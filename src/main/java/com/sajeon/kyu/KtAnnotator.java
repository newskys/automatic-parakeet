package com.sajeon.kyu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class KtAnnotator implements Annotator {

  @Override
  public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
//    System.out.println("annotate: " +  element.getText());
//    System.out.println("annotateEl: " +  element.getClass());
    if (!(element instanceof LeafPsiElement)) {
      return;
    }

    ObjectMapper mapper = new ObjectMapper();
    String key = element.getText();
    String targetKey;

    System.out.println("annotate: " + key);

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
