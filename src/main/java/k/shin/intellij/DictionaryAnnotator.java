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
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class DictionaryAnnotator implements Annotator {

  // Define strings for the Simple language prefix - used for annotations, line markers, etc.
  public static final String SIMPLE_PREFIX_STR = "simple";
  public static final String SIMPLE_SEPARATOR_STR = ":";

  @Override
  public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
    // Ensure the Psi Element is an expression
    if (!(element instanceof PsiIdentifier)) {
      return;
    }

    // print instance type
//    System.out.println("instance type: " + element.getClass().getName());

//    // Ensure the Psi element contains a string that starts with the prefix and separator
//    PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
//    String value = literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;
//    if ((value == null) || !value.startsWith(SIMPLE_PREFIX_STR + SIMPLE_SEPARATOR_STR)) {
//      return;
//    }
//
//    // Define the text ranges (start is inclusive, end is exclusive)
//    // "simple:key"
//    //  01234567890

    ObjectMapper mapper = new ObjectMapper();
//    try {
      //read json file
//      Map<String, String> object = mapper.readValue(FileUtil.loadFile(new File(element.getProject().getBasePath() + "/preset.json")), Map.class);
//      value = object.get(key);
//      if (value == null) return;

    String testStr = "kyu";
    int startIndex = element.getText().indexOf(testStr);

    if (startIndex == -1) return;

      TextRange prefixRange = TextRange.from(element.getTextRange().getStartOffset() + startIndex, testStr.length());
//      System.out.println("element.getTextRange()" + prefixRange + ",,"+element.getText());
      // get element text range from start position of textStr


//      TextRange keyRange = new TextRange(prefixRange.getEndOffset(), element.getTextRange().getEndOffset() - 1);
      TextRange keyRange = new TextRange(startIndex, startIndex + testStr.length());
//      System.out.println("keyRangeStr"+keyRange.toString()+"keyRangeLength: " + keyRange.getLength());

      // Found at least one property, force the text attributes to Simple syntax value character
      holder.newSilentAnnotation(HighlightSeverity.WARNING).range(prefixRange).create();
//    } catch (IOException e) {
      return;
//    }
//
//    // highlight "simple" prefix and ":" separator
//    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
//            .range(prefixRange).textAttributes(DefaultLanguageHighlighterColors.KEYWORD).create();
//    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
//            .range(separatorRange).textAttributes(SimpleSyntaxHighlighter.SEPARATOR).create();
//
//
//    // Get the list of properties for given key
//    String key = value.substring(SIMPLE_PREFIX_STR.length() + SIMPLE_SEPARATOR_STR.length());
//    List<SimpleProperty> properties = SimpleUtil.findProperties(element.getProject(), key);
//    if (properties.isEmpty()) {
//      holder.newAnnotation(HighlightSeverity.ERROR, "Unresolved property")
//              .range(keyRange)
//              .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
//              // ** Tutorial step 18.3 - Add a quick fix for the string containing possible properties
//              .withFix(new SimpleCreatePropertyQuickFix(key))
//              .create();
//    } else {
//    }
  }

}
