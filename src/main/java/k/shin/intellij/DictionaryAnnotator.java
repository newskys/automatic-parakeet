// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package k.shin.intellij;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DictionaryAnnotator implements Annotator {

  @Override
  public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
    if (!(element instanceof PsiIdentifier)) {
      return;
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
